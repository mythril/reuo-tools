package reuo.resources.view;

import java.awt.event.*;

import javax.swing.*;

import reuo.resources.Bitmap;

public class BitmapList extends JScrollPane implements AdjustmentListener {
	AsyncLoaderModel model;
	JList list;
	Bitmap prototype;
	BitmapRenderer renderer;

	public BitmapList(AsyncLoaderModel model, BitmapRenderer renderer, Bitmap prototype) {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.model = model;
		this.prototype = prototype;
		this.renderer = renderer;

		list = new JList(model);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.setPrototypeCellValue(prototype);
		list.setCellRenderer(renderer);
		list.setDoubleBuffered(true);

		getVerticalScrollBar().addAdjustmentListener(this);

		setViewportView(list);
	}

	public JList getList() {
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
