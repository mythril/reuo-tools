package reuo.resources.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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

public class MultiViewer extends Viewer<StructureLoader> implements ListSelectionListener, ChangeListener {
	SpriteDataLoader spriteDataLoader;
	StructureLoader loader;
	IndexedLoader<Entry, Bitmap> spriteLoader;
	Multi multi;
	JTable table;
	AsyncLoaderModel listModel;
	FieldTableModel tableModel;
	JScrollPane listScrollPane, multiScrollPane;
	DrawArea drawArea;
	JPanel mainPanel;
	JSplitPane splitPane;
	JSlider floorSlider;
	JProgressBar progressBar;// = new JProgressBar();

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

		splitPane.setResizeWeight(0.0);
		splitPane.add(listScrollPane = new JScrollPane(table));
		splitPane.add(mainPanel);

		progressBar = new JProgressBar();
		progressBar.setValue(50);

		addStatusSection(progressBar);
		setupDefaultStatusBar();

		add(splitPane);
	}

	/*
	final public class Floor extends LinkedList<Multi.Cell> implements Comparator<Multi.Cell>{
		private VolatileImage image = null;
		private boolean isDirty = false;
		private Rect bounds;
		
		@Override
		public boolean add(Cell e){
			isDirty = true;
			
			return super.add(e);
		}
		
		public Image getImage(){
			if(!isDirty && image != null && !image.contentsLost()){
				return image;
			}
			
			//if(isDirty){
				Collections.sort(this, this);
			//}
				
			//int cx = multi.getWidth() / 2;
			//int cy = multi.getHeight() / 2;
			int x = 0, y = 0;
			bounds = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
			
			for(Multi.Cell cell : this){
				Bitmap bmp = sprites.get(cell.getSpriteId());
				
				if(bmp == null){
					try{
						bmp = spriteLoader.get(cell.getSpriteId());
					}catch(IOException e){
						bmp = null;
					}
					
					sprites.put(cell.getSpriteId(), bmp);
				}
				
				if(bmp != null){
					x = Math.round((cell.getX() - cell.getY()) * 22.0f) - bmp.getWidth() / 2;
					y = Math.round((cell.getX() + cell.getY()) * 22.0f - cell.getZ() * 4.0f) - bmp.getHeight();
					
					bounds.left = Math.min(bounds.left, x);
					bounds.right = Math.max(bounds.right, x + bmp.getWidth());
					bounds.top = Math.min(bounds.top, y);
					bounds.bottom = Math.max(bounds.bottom, y + bmp.getHeight());
				}
			}
			
			System.out.println(bounds);
			
			image = createVolatileImage(bounds.getWidth(), bounds.getHeight());
			Graphics2D g = image.createGraphics();
			
			for(Multi.Cell cell : this){
				Bitmap bmp = sprites.get(cell.getSpriteId());
				
				if(bmp == null){
					System.out.printf("null: %d\n", cell.getSpriteId());
					continue;
				}
				
				x = Math.round((cell.getX() - cell.getY()) * 22.0f) - bmp.getWidth() / 2;
				y = Math.round((cell.getX() + cell.getY()) * 22.0f - cell.getZ() * 4.0f) - bmp.getHeight();
				
				Utilities.paint(g, bmp, Utilities.getImage(bmp, 1), x - bounds.left, y - bounds.top);
			}
			
			isDirty = false;
			return image;
		}

		public int compare(Cell a, Cell b){
			int row, altitude, height;
			
			row =  (a.getX() + a.getY()) - (b.getX() + b.getY());
			altitude = a.getZ() - b.getZ();
			height = 0;
			
			//row = (row + 0x7ff) & 0x1000;
			//altitude = (altitude + 0x7f) & 0xff;
			height = (height + 0x7ff) & 0x1000;
			
			return (row << 20) | (altitude << 12) | height;
		}
	}
	*/
	/*
		private class Daemon extends Thread {
			private boolean isRunning = true;
			final private Set<Integer> queue = new HashSet<Integer>();

			public void enque(int id) {
				synchronized (queue) {
					if (!queue.contains(id)) {
						queue.add(id);
						queue.notifyAll();
					}
				}
			}

			public void run() {
				while (isRunning) {
					Bitmap bmp;
					int id;

					while (isRunning) {
						synchronized (queue) {
							if (queue.isEmpty()) {
								break;
							}

							id = queue.iterator().next();
							queue.remove(id);
						}

						synchronized (sprites) {
							bmp = sprites.get(id);

							if (bmp == null) {
								try {
									bmp = spriteLoader.get(id);
								} catch (IOException e) {
									bmp = null;
								}
							}
						}
					}

					synchronized (queue) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							continue;
						}
					}
				}
			}
		}
		*/
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

		rows.clear();

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
			Graphics2D g = (Graphics2D) lg;

			if (multi == null) {
				return;
			}

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
