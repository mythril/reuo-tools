package reuo.resources.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import reuo.resources.*;
import reuo.resources.format.Utilities;
import reuo.util.Rect;

public class BitmapRenderer<B extends Bitmap> extends JComponent implements ListCellRenderer<B>, TableCellRenderer{
	final int alphaBits;
	B bmp;
	BufferedImage image;
	boolean isSelected, isFocused;
	Color background, foreground;
	Checker checker = null;
	EnumSet<Metric> metrics;
	
	public enum Metric{
		SCALED,
		CROPPED,
		OPAQUE,
		STRETCHED;
	}
	
	public BitmapRenderer(int alphaBits){
		this(alphaBits, EnumSet.noneOf(Metric.class));
	}
	
	public BitmapRenderer(int alphaBits, Metric first, Metric... metrics){
		this(alphaBits, EnumSet.of(first, metrics));
	}
	
	public BitmapRenderer(int alphaBits, EnumSet<Metric> metrics){
		this.alphaBits = alphaBits;
		this.metrics = metrics;
		
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public void setSelectionBackground(Color c){
		background = c;
	}
	
	public void setSelectionForeground(Color c){
		foreground = c;
	}
	
	public Component getListCellRendererComponent(
		JList<? extends B> list,
		B value,
		int index,
		boolean isSelected,
		boolean isFocused
	){
		this.isSelected = isSelected;
		this.isFocused = isFocused;
		setBitmap(value);
		setSelectionBackground(list.getSelectionBackground());
		setSelectionForeground(list.getSelectionForeground());
		
		return this;
	}
	
	
	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column
	){
		this.isSelected = isSelected;
		this.isFocused = hasFocus;
		
		setBitmap((B)value);
		setSelectionBackground(table.getSelectionBackground());
		setSelectionForeground(table.getSelectionForeground());
		
		return this;
	}
	
	public void setBitmap(final B bmp){
		if(bmp == null){
			this.bmp = null;
			this.image = null;
			return;
		}
		
		this.bmp = bmp;
		image = Utilities.getImage(bmp, alphaBits);		
		
		if(metrics.contains(Metric.CROPPED)){
			setPreferredSize(new Dimension(bmp.getImageWidth(), bmp.getImageHeight()));
		}else{
			setPreferredSize(new Dimension(bmp.getWidth(), bmp.getHeight()));
		}
	}
	
	public void setChecker(Checker checker){
		this.checker = checker;
	}
	
	/*
	private void centerAndFit(
		Rectangle parent,
		Rectangle child
	){
		// TODO: Fix center and fitting algorithm
		if(!parent.contains(child)){
			if(child.width > child.height){
				if(child.width > parent.height){
					float r = child.height / (float)child.width;
					child.width = parent.width;
					child.height = Math.round(child.width * r);
				}
			}
			
			if(child.height > parent.height){
				float r = child.width / (float)child.height;
				child.height = parent.height;
				child.width = Math.round(child.height * r);
			}
		}
		
		child.x = parent.x + Math.round(parent.width * 0.5f - child.width * 0.5f);
		child.y = parent.y + Math.round(parent.height * 0.5f - child.height * 0.5f);
	}
	*/
	
	@Override
	public void paintComponent(Graphics lg){
		Graphics2D g = (Graphics2D)lg;
		Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
		
		if(bmp == null || image == null){
			return;
		}
		
		if(getBorder() != null){
			Insets insets = getBorder().getBorderInsets(this);
			
			bounds.x += insets.left;
			bounds.y += insets.top;
			bounds.width -= insets.right;
			bounds.height -= insets.bottom;
		}
		
		if(isSelected && background != null){
			g.setColor(background);
			g.fill(g.getClipBounds());
		}
		
		Rectangle area, bitmap;
		int width, height;
		
		if(metrics.contains(Metric.CROPPED)){
			width = bmp.getImageWidth();
			height = bmp.getImageHeight();
			
			area = new Rectangle(
				bounds.x + bounds.width / 2 - width / 2,
				bounds.y + bounds.height / 2 - height / 2,
				width - 1, height - 1
			);
			
			bitmap = area;
		}else{
			width = bmp.getWidth();
			height = bmp.getHeight();
			
			area = new Rectangle(
				bounds.x + bounds.width / 2 - width / 2,
				bounds.y + bounds.height / 2 - height / 2,
				width, height
			);
			
			Rect insets = bmp.getInsets();
			
			if(insets != null){
				bitmap = new Rectangle(
					area.x + insets.left,
					area.y + insets.top,
					area.width - insets.right,
					area.height - insets.bottom
				);
			}else{
				bitmap = area;
			}
		}
		/*
		if(metrics.contains(Metric.STRETCHED)){
			area = bitmap = bounds;
		}else if(metrics.contains(Metric.SCALED)){
			if(bitmap == area){
				bitmap = new Rectangle(area);
			}
			
			//centerAndFit(bounds, area);
			//centerAndFit(area, bitmap);
		}
		
		if(checker != null){
			checker.paint(g, area);
		}
		
		if(metrics.contains(Metric.SCALED) || metrics.contains(Metric.STRETCHED)){
			g.drawImage(image, bitmap.x, bitmap.y, bitmap.width, bitmap.height, null);
		}else{
		*/
			g.drawImage(image, bitmap.x, bitmap.y, null);
		//}
		
		/*
		if(isCropped){
			int width = bmp.getImageWidth();
			int height = bmp.getImageHeight();
			
			area = new Rectangle(
				bounds.x + bounds.width / 2 - width / 2,
				bounds.y + bounds.height / 2 - height / 2,
				width, height
			);
			
			switch(scale){
			case SCALE_NONE:
				g.drawImage(image, area.x, area.y, this);
				break;
			case SCALE_FIT:
				centerAndFit(bounds, area);
				g.drawImage(image, area.x, area.y, area.width, area.height, null);
				break;
			case SCALE_STRETCH:
				g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
				break;
			}
		}else{
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			
			area = new Rectangle(
				bounds.x + bounds.width / 2 - width / 2,
				bounds.y + bounds.height / 2 - height / 2,
				width - 1, height - 1
			);
			
			Rectangle uncropped = new Rectangle(
				area.x, area.y, bmp.getImageWidth(), bmp.getImageHeight()
			);
			
			Rect insets = bmp.getInsets();
			
			if(insets != null){
				uncropped.x += insets.left;
				uncropped.y += insets.top;
			}
			
			if(checker != null){
				checker.paint(g, area);
			}
			
			switch(scale){
			case SCALE_NONE:
				g.drawImage(image, uncropped.x, uncropped.y, null);
				break;
				
			case SCALE_FIT:
				break;
			}
			
			if(checker != null){
				g.setColor(getForeground());
				
				if(!uncropped.equals(bounds)){
					g.setStroke(cropStroke);
					g.draw(uncropped);
				}
				
				g.setStroke(boundsStroke);
				g.draw(area);
			}
		}
		*/
		
		if(isFocused){
			g.setColor(foreground);
			
			for(int dx=bounds.x; dx < bounds.width-1; dx += 2){
				g.fillRect(dx, bounds.y, 1, 1);
				g.fillRect(dx, bounds.height-1, 1, 1);
			}
			
			for(int dy=bounds.y; dy < bounds.height-1; dy += 2){
				g.fillRect(bounds.x, dy, 1, 1);
				g.fillRect(bounds.width-1, dy, 1, 1);
			}
		}
	}
	/*
	final private static Stroke boundsStroke = new BasicStroke(1.0f);
	final private static Stroke cropStroke = new BasicStroke(
		1.0f,
		BasicStroke.CAP_SQUARE,
		BasicStroke.JOIN_ROUND,
		10.0f,
		new float[]{ 1.0f, 3.0f},
		0.0f
	);
	*/
}