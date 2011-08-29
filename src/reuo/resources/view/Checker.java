package reuo.resources.view;

import java.awt.*;
import java.awt.geom.Area;

import javax.swing.JComponent;

/**
 * A simple two color checker pattern. If the two colors are the same this
 * drawing operation will perform the same as a solid color. The checker colors
 * are blended between {@link #setForeground(Color)} and
 * {@link #setBackground(Color)} with the {@link #setAlpha(float)} value. The
 * size of the checker may be changed with {@link #setSize(int)}.
 */
final public class Checker{
	private Color background, foreground;
	private int size, size2;
	private float a = 0.15f, na;
	
	/**
	 * Initializes a checker pattern with the colors from the provided
	 * JComponent with the specified size.
	 * 
	 * @param component the component to use for colors
	 * @param size the size of the checker in the pattern
	 */
	public Checker(JComponent component, int size){
		this(component.getBackground(), component.getForeground(), size);
	}
	
	/**
	 * Initializes a checker pattern with the specified colors and size.
	 * 
	 * @param bg the background color
	 * @param fg the foreground color
	 * @param size the size of the checker
	 */
	public Checker(Color bg, Color fg, int size){
		setBackground(bg);
		setForeground(fg);
		setSize(size);
	}
	
	/**
	 * Sets the alpha of blending between the background and foreground. This
	 * defaults to 0.15.
	 * 
	 * @param alpha
	 * @throws IllegalArgumentException if 1 < alpha < 0
	 */
	final public void setAlpha(float alpha) throws IllegalArgumentException{
		if(alpha < 0 || alpha > 1)
			throw new IllegalArgumentException();
		
		this.a = alpha;
		this.na = 1.0f - alpha;
	}
	
	/**
	 * Sets the size of the checker in the pattern
	 * @param size the size of the checker
	 */
	public void setSize(int size){
		this.size = size;
		size2 = size * 2;
	}
	
	/**
	 * Gets the size of the checker in the pattern.
	 * @return the checker size
	 */
	public int getSize(){
		return size;
	}
	
	public void setForeground(Color c){
		if(background == null){
			this.foreground = c;
		}else if(c != null){
			foreground = new Color(
				(int)(background.getRed() * na + c.getRed() * a),
				(int)(background.getGreen() * na + c.getGreen() * a),
				(int)(background.getBlue() * na + c.getBlue() * a),
				(int)(background.getAlpha() * na + c.getAlpha() * a));
		}
	}
	
	/**
	 * Sets the background color of the checker pattern.
	 * @param c the background color
	 */
	public void setBackground(Color c){
		this.background = c;
		setForeground(foreground);
	}
	
	/**
	 * Draws a checker pattern to the provided graphics using the
	 * clipping bounds of <code>g</code>.
	 * @param g the graphics to use for drawing
	 */
	public void paint(Graphics2D g){
		paint(g, g.getClipBounds());
	}
	
	/**
	 * Draws a checker pattern to the provided graphics within the
	 * specified clipping bounds. This will temporarily modify the
	 * clipping region of <code>g</code>.
	 * @param g the graphics to use for drawing
	 * @param bounds the clipping bounds
	 */
	public void paint(Graphics2D g, Rectangle bounds){
		Shape oldClip = g.getClip();
		boolean needsClipping = bounds != oldClip;
		
		if(needsClipping){
			Area clip = new Area(oldClip);
			clip.intersect(new Area(bounds));
			g.setClip(clip);
		}
		
		g.setColor(background);
		g.fill(bounds);
		g.setColor(foreground);
		
		int top = (bounds.y / size - 1) * size;
		int left = (bounds.x / size - 1) * size;
		int bottom = ((bounds.y + bounds.height) / size + 1) * size;
		int right = ((bounds.x + bounds.width) / size + 1) * size;
		int offset = (top + left) / size % 2 * size;
		
		for(int y = top; y < bottom; y += size, offset = size - offset){
			for(int x = left + offset; x < right; x += size2){
				g.fillRect(x, y, size, size);
			}
		}
		
		if(needsClipping){
			g.setClip(oldClip);
		}
	}
}
