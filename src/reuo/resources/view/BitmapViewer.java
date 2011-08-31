package reuo.resources.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import reuo.resources.Bitmap;
import reuo.resources.io.Entry;
import reuo.resources.io.IndexedLoader;

public class BitmapViewer extends Viewer<IndexedLoader<Entry, Bitmap>> implements AdjustmentListener, ChangeListener {
	IndexedLoader<Entry, Bitmap> loader;
	AsyncLoaderModel<Bitmap> model;
	JBitmapList list;
	BitmapRenderer<Bitmap> renderer;
	JScrollPane scrollPane;
	JSlider prototypeSize;

	Bitmap prototype = new Bitmap(-1, 0, 0) {
		@Override
		public int getImageWidth() {
			return prototypeSize.getValue();
		}

		@Override
		public int getImageHeight() {
			return prototypeSize.getValue();
		}
	};

	private class JBitmapList extends JList<Bitmap> {

		public JBitmapList(ListModel<Bitmap> model, Bitmap prototype) {
			super(model);

			setLayoutOrientation(JList.HORIZONTAL_WRAP);
			setVisibleRowCount(-1);
			setPrototypeCellValue(prototype);
			setCellRenderer(renderer);
			//setDoubleBuffered(true);

			super.setOpaque(false);
		}

		@Override
		public void paintComponent(final Graphics g) {
			//checker.paint((Graphics2D) g);
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
		}
	}

	public BitmapViewer(IndexedLoader<Entry, Bitmap> loader, BitmapRenderer<Bitmap> renderer) {
		this.renderer = renderer;
		this.loader = loader;

		this.setDoubleBuffered(false);

		prototypeSize = new JSlider(16, 128, 44);
		prototypeSize.setPaintTicks(true);
		prototypeSize.setSnapToTicks(false);
		prototypeSize.setMinorTickSpacing(8);
		prototypeSize.setMajorTickSpacing(16);
		prototypeSize.addChangeListener(this);

		model = new AsyncLoaderModel<Bitmap>(loader, null);

		list = new JBitmapList(model, prototype);
		list.addListSelectionListener(this);

		scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		//scrollPane.setDoubleBuffered(false);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(prototypeSize, BorderLayout.SOUTH);
		setupDefaultStatusBar();
	}

	public BitmapRenderer<Bitmap> getRenderer() {
		return renderer;
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) {
		// TODO figure out preparing loaders
	}

	public void valueChanged(final ListSelectionEvent e) {
		updateStatusIDs(model.getId(list.getSelectedIndex()));
	}

	public void adjustmentValueChanged(final AdjustmentEvent e) {
		int from = list.getFirstVisibleIndex();
		int to = list.getLastVisibleIndex();

		model.pruneTo(from, to);
	}

	public void stateChanged(final ChangeEvent e) {
		list.setFixedCellWidth(prototype.getImageWidth());
		list.setFixedCellHeight(prototype.getImageHeight());
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}