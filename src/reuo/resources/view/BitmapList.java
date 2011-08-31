package reuo.resources.view;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JList;
import javax.swing.JScrollPane;

import reuo.resources.Bitmap;

public class BitmapList extends JScrollPane implements AdjustmentListener {
	AsyncLoaderModel<Bitmap> model;
	JList<Bitmap> list;
	Bitmap prototype;
	BitmapRenderer<Bitmap> renderer;

	public BitmapList(AsyncLoaderModel<Bitmap> model, BitmapRenderer<Bitmap> renderer, Bitmap prototype) {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.model = model;
		this.prototype = prototype;
		this.renderer = renderer;

		list = new JList<Bitmap>(model);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.setPrototypeCellValue(prototype);
		list.setCellRenderer(renderer);
		list.setDoubleBuffered(true);

		getVerticalScrollBar().addAdjustmentListener(this);

		setViewportView(list);
	}

	public JList<Bitmap> getList() {
		return list;
	}

	public Bitmap getSelected() {
		return (Bitmap) list.getSelectedValue();
	}

	public void adjustmentValueChanged(AdjustmentEvent event) {
		int from = list.getFirstVisibleIndex();
		int to = list.getLastVisibleIndex();

		model.pruneTo(from, to);
	}

}
