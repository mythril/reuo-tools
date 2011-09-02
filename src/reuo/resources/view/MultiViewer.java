package reuo.resources.view;



import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.VolatileImage;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import reuo.resources.*;
import reuo.resources.Multi.Cell;
import reuo.resources.format.Utilities;
import reuo.resources.io.*;
import reuo.util.Rect;

/** A {@link Viewer} for viewing {@link Multi} resources. */
public class MultiViewer extends Viewer<StructureLoader> implements ListSelectionListener, ChangeListener {
	protected SpriteDataLoader spriteDataLoader;
	protected StructureLoader loader;
	protected IndexedLoader<Entry, Bitmap> spriteLoader;
	protected Multi multi;
	protected JTable table;
	protected AsyncLoaderModel<Multi> listModel;
	protected FieldTableModel tableModel;
	protected JScrollPane listScrollPane, multiScrollPane;
	protected DrawArea drawArea;
	protected JPanel mainPanel;
	protected JSplitPane splitPane;
	protected JSlider floorSlider;
	protected JProgressBar progressBar;// = new JProgressBar();
	protected VolatileImage backbuffer;
	protected Graphics2D backbufferGraphics;
	protected boolean hasMultiChanged;
	protected JList<Object> layerList;

	public MultiViewer(File dir, String[] fileNames, IndexedLoader<Entry, Bitmap> spriteLoader, SpriteDataLoader spriteDataLoader) throws IOException {
		this.spriteDataLoader = spriteDataLoader;
		this.spriteLoader = spriteLoader;

		floorSlider = new JSlider();
		floorSlider.addChangeListener(this);
		splitPane = hsplit();

		loader = new StructureLoader(new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				new FileInputStream(new File(dir, fileNames[1])).getChannel(), spriteDataLoader);

		listModel = new AsyncLoaderModel<Multi>(loader, null);
		tableModel = new FieldTableModel(listModel, Multi.class, "Id:getId");
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(this);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(multiScrollPane = scroll(drawArea = new DrawArea()));
		mainPanel.add(floorSlider, BorderLayout.SOUTH);
		
		splitPane.add(mainPanel);
		
		JSplitPane sidePanel = vsplit();
		sidePanel.add(listScrollPane = scroll(table));
		sidePanel.add(scroll(layerList = new JList<Object>()));
		
		splitPane.add(sidePanel);//

		progressBar = new JProgressBar();
		progressBar.setValue(50);

		addStatusSection(progressBar);
		setupDefaultStatusBar();

		add(splitPane);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				splitPane.setDividerLocation(getWidth()-100);
				splitPane.setResizeWeight(1.0);
			}
		});
	}
	
	final private Map<Integer, Bitmap> sprites = new HashMap<Integer, Bitmap>();
	final Map<Integer, Rect> spriteSizes = new HashMap<Integer, Rect>();
	final private RowIndex rows = new RowIndex();

	final private static class RowIndex extends TreeMap<Integer, AltitudeIndex> {
	}

	final private static class AltitudeIndex extends TreeMap<Integer, List<Cell>> {
	}

	Dimension multiDimensions = new Dimension(0, 0);
	Rect multiBounds = new Rect();

	public void setMulti(Multi multi) {
		if (this.multi == multi) {
			return;
		}
		
		if (this.multi != null) {
			if (backbuffer != null) {
				backbuffer.flush();
				backbuffer = null;
			}
			
			this.multi = null;
		}

		rows.clear();
		hasMultiChanged = true;
		this.multi = multi;
		int tallest = 0;

		multiBounds.left = multiBounds.right = multiBounds.top = multiBounds.bottom = 0;

		for (Multi.Cell cell : multi.getCells()) {
			SpriteData data;
			Bitmap bmp;
			
			bmp = sprites.get(cell.getSpriteId());

			try {
				data = spriteDataLoader.get(cell.getSpriteId());

				if (bmp == null) {
					bmp = spriteLoader.get(cell.getSpriteId());
					sprites.put(cell.getSpriteId(), bmp);
				}
				
				if (spriteSizes.get(cell.getSpriteId()) == null) {
					spriteSizes.put(
							cell.getSpriteId(),
							new Rect(0, 0, bmp.getHeight(), bmp.getWidth())
						);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			if (data == null || bmp == null) {
				System.out.printf("%s, %s\n", data, bmp);
				continue;
			}

			assert data.height >= 0;
			tallest = Math.max(tallest, data.height);

			Integer key = cell.getX() + cell.getY();
			AltitudeIndex zindex = rows.get(key);

			if (zindex == null) {
				zindex = new AltitudeIndex();
				rows.put(key, zindex);
			}

			key = cell.getZ() + data.height;
			List<Multi.Cell> cells = zindex.get(key);

			if (cells == null) {
				cells = new LinkedList<Multi.Cell>();
				zindex.put(key, cells);
			}

			cells.add(cell);

			int tx = (cell.getX() - cell.getY()) * 22 - bmp.getWidth() / 2;
			int ty = (cell.getX() + cell.getY()) * 22 - cell.getZ() * 4 - bmp.getHeight();

			multiBounds.left = Math.min(multiBounds.left, tx);
			multiBounds.right = Math.max(multiBounds.right, tx + bmp.getWidth());
			multiBounds.top = Math.min(multiBounds.top, ty);
			multiBounds.bottom = Math.max(multiBounds.bottom, ty + bmp.getHeight());
		}

		floorSlider.setMinimum(multi.getLowest());
		floorSlider.setMaximum(multi.getHighest() + tallest);
		
		drawArea.setPreferredSize(multiDimensions = new Dimension(multiBounds.getWidth(), multiBounds.getHeight()));
		floorSlider.setValue(progressBar.getMaximum());
		
		drawArea.scrollX = multiScrollPane.getWidth()/2 - multiDimensions.width/2;
		drawArea.scrollY = multiScrollPane.getHeight()/2 - multiDimensions.height/2;
		
		drawArea.repaint();
	}

	public void valueChanged(ListSelectionEvent event) {
		int column = table.convertColumnIndexToModel(0);
		int row = table.getSelectedRow();
		Integer id;

		if (row < 0) {
			multi = null;
			return;
		}

		id = (Integer) table.getValueAt(row, column);

		if (id == null) {
			multi = null;
			return;
		}

		try {
			setMulti(loader.get(id));
		} catch (IOException e) {
			multi = null;
			e.printStackTrace();
		}

		multiScrollPane.doLayout();

		updateStatusIDs(listModel.getId(table.getSelectedRow()));
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(new FileInputStream(new File(dir, fileNames[0])).getChannel(), new FileInputStream(new File(dir, fileNames[1])).getChannel());
	}

	public void stateChanged(ChangeEvent e) {
		hasMultiChanged = true;
		drawArea.repaint();
	}

	AffineTransform iso, niso;
	{
		iso = new AffineTransform();
		iso.scale(22, 22);
		iso.rotate(Math.PI / 4);

		niso = new AffineTransform(iso);

		try {
			niso.invert();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

	}

	private class DrawArea extends JComponent implements MouseListener, MouseMotionListener {
		Checker checker = new Checker(Color.WHITE, Color.LIGHT_GRAY, 24);
		int scrollX = 0, scrollY = 0;
		boolean isPanning = false;
		int startX = 0, startY = 0;
		
		public DrawArea() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		@Override
		public Dimension getPreferredSize() {
			int w, h;
			int parentWidth = multiScrollPane.getWidth();
			int parentHeight = multiScrollPane.getHeight();
			
			w = Math.min(scrollX, parentWidth);
			h = Math.min(scrollY, parentHeight);
			
			if (backbuffer != null) {
				w += backbuffer.getWidth();
				h += backbuffer.getHeight();
			}
			
			return new Dimension(w, h);
		}

		@Override public void paintComponent(Graphics lg) {
			Graphics2D g = (Graphics2D)lg;
			
			if (multi == null) {
				return;
			}
			
			if (backbuffer == null) {
				makeBackBuffer();
			}
			
			if (hasMultiChanged || backbuffer.contentsLost()) {
				paintMulti(backbufferGraphics);
				hasMultiChanged = false;
			}
			
			g.setColor(Color.GRAY);
			g.fill(g.getClipBounds());
			g.drawImage(backbuffer, scrollX, scrollY, null);
			
			g.setColor(Color.BLACK);
			g.drawRect(scrollX, scrollY, backbuffer.getWidth(), backbuffer.getHeight());
		}
		
		private void makeBackBuffer() {
			backbuffer = createVolatileImage(multiBounds.getWidth() + 1, multiBounds.getHeight() + 1);
			backbufferGraphics = (Graphics2D)backbuffer.getGraphics();
		}
		
		public void paintMulti(Graphics lg) {
			Graphics2D g = (Graphics2D) lg;
			
			if (multi == null) {
				return;
			}
			
			checker.paint(g, backbuffer.getWidth(), backbuffer.getHeight());
			//g.clearRect(0, 0, backbuffer.getWidth(), backbuffer.getHeight());
			
			int cx = -multiBounds.left;//getWidth() / 2;
			int cy = -multiBounds.top;//getHeight() / 2;
			int top = floorSlider.getValue();

			for (Map.Entry<Integer, AltitudeIndex> zentry : rows.entrySet()) {
				int row = zentry.getKey();
				SortedMap<Integer, List<Cell>> zindex = zentry.getValue().subMap(multi.getLowest(), top + 1);

				for (List<Cell> cells : zindex.values()) {
					for (Cell cell : cells) {
						if (cell.getZ() >= top) {
							continue;
						}
						//
						
						Rect rect = spriteSizes.get(cell.getSpriteId());

						if (rect == null) {
							System.out.printf("null: %d\n", cell.getSpriteId());
							continue;
						}

						int tx, ty;
						int x, y, z;
						int w, h;
						w = rect.getWidth();
						h = rect.getHeight();
						x = cell.getX();
						y = cell.getY();
						z = cell.getZ();

						tx = (x - y) * 22 - w / 2;
						ty = row * 22 - z * 4 - h;
						
						Rectangle clip = getVisibleRect(); // debuggin, works good
						
						Rectangle spriteBounds = new Rectangle(
								cx + tx,
								cy + ty,
								rect.getWidth(),
								rect.getHeight()
							);
						
						System.out.println("Clip:");
						System.out.println(clip);
						System.out.println("SpriteBounds:");
						System.out.println(spriteBounds);
						
						if (clip.intersects(spriteBounds)) {
							Bitmap bmp = sprites.get(cell.getSpriteId());
							Utilities.paint(g, bmp, Utilities.getImage(bmp, 1), cx + tx, cy + ty);
						}
						
					}
				}
			}
		}
		
		public void mouseMoved(MouseEvent e) {
			if (isPanning) {
				int dx, dy;
				//boolean isGrowing;
				
				dx = e.getX() - startX;
				dy = e.getY() - startY;
				//isGrowing = dx > 0 || dy > 0;
				scrollX += dx;
				scrollY += dy;
				
				startX = e.getX();
				startY = e.getY();
				
				//if (isGrowing && backbuffer != null) {
					//d.width = Math.min(startX, getWidth()) + backbuffer.getWidth();
					//d.height = Math.min(startY, getHeight()) + backbuffer.getHeight();
					//setPreferredSize(d);
					
				//}
				
				repaint();
			}
		}

		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
		
		// Unused mouse events
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) { }
		
		public void mousePressed(MouseEvent e) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3:
				isPanning = true;
				startX = e.getX();
				startY = e.getY();
				break;
			}
		}

		public void mouseReleased(MouseEvent e) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3:
				if (isPanning) {
					multiScrollPane.updateUI();
				}
				
				isPanning = false;
				break;
			}
		}
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
