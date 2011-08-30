package reuo.resources.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import reuo.resources.Bitmap;
import reuo.resources.Multi;
import reuo.resources.Multi.Cell;
import reuo.resources.SpriteData;
import reuo.resources.format.Utilities;
import reuo.resources.io.Entry;
import reuo.resources.io.IndexedLoader;
import reuo.resources.io.SpriteDataLoader;
import reuo.resources.io.StructureLoader;
import reuo.util.Rect;

/** A {@link Viewer} for viewing {@link Multi} resources. */
public class MultiViewer extends Viewer<StructureLoader> implements ListSelectionListener, ChangeListener {
	protected SpriteDataLoader spriteDataLoader;
	protected StructureLoader loader;
	protected IndexedLoader<Entry, Bitmap> spriteLoader;
	Multi multi;
	protected JTable table;
	protected AsyncLoaderModel listModel;
	protected FieldTableModel tableModel;
	protected JScrollPane listScrollPane, multiScrollPane;
	protected DrawArea drawArea;
	protected JPanel mainPanel;
	protected JSplitPane splitPane;
	protected JSlider floorSlider;
	protected JProgressBar progressBar;// = new JProgressBar();
	protected VolatileImage backbuffer;
	protected boolean hasMultiChanged;

	public MultiViewer(File dir, String[] fileNames, IndexedLoader<Entry, Bitmap> spriteLoader, SpriteDataLoader spriteDataLoader) throws IOException {

		this.spriteDataLoader = spriteDataLoader;
		this.spriteLoader = spriteLoader;

		floorSlider = new JSlider();
		floorSlider.addChangeListener(this);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		loader = new StructureLoader(new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				new FileInputStream(new File(dir, fileNames[1])).getChannel(), spriteDataLoader);

		listModel = new AsyncLoaderModel(loader, null);
		tableModel = new FieldTableModel(listModel, Multi.class, "Id:getId");
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(this);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(multiScrollPane = new JScrollPane(drawArea = new DrawArea()));
		mainPanel.add(floorSlider, BorderLayout.SOUTH);
		
		splitPane.add(mainPanel);
		splitPane.add(listScrollPane = new JScrollPane(table));

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
	final private RowIndex rows = new RowIndex();

	final private class RowIndex extends TreeMap<Integer, AltitudeIndex> {
	}

	final private class AltitudeIndex extends TreeMap<Integer, List<Cell>> {
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

	private class DrawArea extends JComponent implements MouseMotionListener {
		// int curX, curY;

		public DrawArea() {
			//addMouseMotionListener(this);

			//RepaintManager repaintManager = RepaintManager.currentManager(this);
			//repaintManager.setDoubleBufferingEnabled(false);
			//setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
		}

		/* FIXME Dead code
		
		Point2D.Float min = new Point2D.Float();
		Point2D.Float max = new Point2D.Float();
		
		public Rect screenToWorld(Rectangle screen) {
			min.x = screen.x;
			min.y = screen.y;
			max.x = screen.x + screen.width;
			max.y = screen.y + screen.height;

			niso.transform(min, min);
			niso.transform(max, max);

			return new Rect(Math.round(min.y), Math.round(min.x), Math.round(max.y), Math.round(max.x));
		}
		*/

		@Override
		public void paintComponent(Graphics lg) {
			if (multi == null) {
				return;
			}
			
			if (backbuffer == null) {
				makeBackBuffer();
			}
			
			if (hasMultiChanged || backbuffer.contentsLost()) {
				paintMulti(backbuffer.getGraphics());
				hasMultiChanged = false;
			}
			
			lg.drawImage(backbuffer, 0, 0, null);
		}
		
		private void makeBackBuffer() {
			int left = Integer.MAX_VALUE;
			int top = Integer.MAX_VALUE;
			int bottom = Integer.MIN_VALUE;
			int right = Integer.MIN_VALUE;
			
			for (Map.Entry<Integer, AltitudeIndex> zentry : rows.entrySet()) {
				int row = zentry.getKey();
				//SortedMap<Integer, List<Cell>> zindex = zentry.getValue().subMap(multi.getLowest(), top + 1);

				for (List<Cell> cells : zentry.getValue().values()) {
					for (Cell cell : cells) {
						int tx, ty;
						int x, y, z;
						int w, h;
						
						//if (cell.getZ() >= top) {
						//	continue;
						//}
						
						Bitmap bmp = sprites.get(cell.getSpriteId());

						if (bmp == null) {
							System.out.printf("null: %d\n", cell.getSpriteId());
							continue;
						}
						
						w = bmp.getWidth();
						h = bmp.getHeight();
						x = cell.getX();
						y = cell.getY();
						z = cell.getZ();

						tx = (x - y) * 22 - w / 2;
						ty = row * 22 - z * 4 - h;
						
						left = Math.min(left, tx);
						right = Math.max(right, tx + w);
						top = Math.min(top, ty - h);
						bottom = Math.max(bottom, ty + h);
						
						//Utilities.paint(g, bmp, Utilities.getImage(bmp, 1), cx + tx, cy + ty);
					}
				}
			}
			
			System.out.printf("%d, %d, %d, %d\n", left, right, top, bottom);
			System.out.printf("%d x %d\n", 1+(right-left), 1+(bottom-top));
			
			backbuffer = createVolatileImage(1+(right-left), 1+(bottom-top));
		}
		
		public void paintMulti(Graphics lg) {
			Graphics2D g = (Graphics2D) lg;
			
			if (multi == null) {
				return;
			}
			
			g.clearRect(0, 0, backbuffer.getWidth(), backbuffer.getHeight());
			
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
						Bitmap bmp = sprites.get(cell.getSpriteId());

						if (bmp == null) {
							System.out.printf("null: %d\n", cell.getSpriteId());
							continue;
						}

						int tx, ty;
						int x, y, z;
						int w, h;
						w = bmp.getWidth();
						h = bmp.getHeight();
						x = cell.getX();
						y = cell.getY();
						z = cell.getZ();

						tx = (x - y) * 22 - w / 2;
						ty = row * 22 - z * 4 - h;

						Utilities.paint(g, bmp, Utilities.getImage(bmp, 1), cx + tx, cy + ty);
					}
				}
			}

			/*
			g.setColor(Color.RED);
			
			x = Math.round((cur.x - cur.y) * 22 - 44/2);
			y = Math.round((cur.x + cur.y) * 22 - 44);
			
			g.fillRect(x, y, 44, 44);
			*/
		}

		Point2D.Float cur = new Point2D.Float();

		public void mouseMoved(MouseEvent event) {
			int cx = getWidth() / 2;
			int cy = getHeight() / 2;

			cur.x = event.getX() - (cx - 11);
			cur.y = event.getY() - (cy - 22);

			niso.transform(cur, cur);

			cur.x = Math.round(cur.x);
			cur.y = Math.round(cur.y);

			repaint();
		}

		public void mouseDragged(MouseEvent e) {
		}
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
