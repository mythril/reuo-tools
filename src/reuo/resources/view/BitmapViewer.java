package reuo.resources.view;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;

import reuo.resources.Bitmap;
import reuo.resources.io.*;

public class BitmapViewer extends Viewer<IndexedLoader<Entry, Bitmap>> implements AdjustmentListener, ChangeListener{
	IndexedLoader<Entry, Bitmap> loader;
	AsyncLoaderModel model;
	JBitmapList list;
	BitmapRenderer renderer;
	JScrollPane scrollPane;
	JSlider prototypeSize;
	
	Bitmap prototype = new Bitmap(-1, 0, 0){
		@Override
		public int getImageWidth(){
			return prototypeSize.getValue();
		}
		
		@Override
		public int getImageHeight(){
			return prototypeSize.getValue();
		}
	};
	
	private class JBitmapList extends JList{
		Checker checker = new Checker(this, 16);
		
		public JBitmapList(ListModel model, Bitmap prototype){
			super(model);
			
			setLayoutOrientation(JList.HORIZONTAL_WRAP);
			setVisibleRowCount(-1);
			setPrototypeCellValue(prototype);
			setCellRenderer(renderer);
			//setDoubleBuffered(true);
			
			super.setOpaque(false);
		}
		
		@Override
		public void paintComponent(final Graphics g){
			checker.paint((Graphics2D)g);
			
			super.paintComponent(g);
		}
	}
	
	public BitmapViewer(
		IndexedLoader<Entry, Bitmap> loader,
		BitmapRenderer renderer
	){		
		this.renderer = renderer;
		this.loader = loader;
		
		this.setDoubleBuffered(false);
		
		prototypeSize = new JSlider(16, 128, 44);
		prototypeSize.setPaintTicks(true);
		prototypeSize.setSnapToTicks(false);
		prototypeSize.setMinorTickSpacing(8);
		prototypeSize.setMajorTickSpacing(16);
		prototypeSize.addChangeListener(this);
		
		model = new AsyncLoaderModel(loader, null);
		
		list = new JBitmapList(model, prototype);
		list.addListSelectionListener(this);
		
		scrollPane = new JScrollPane(list,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		//scrollPane.setDoubleBuffered(false);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(prototypeSize, BorderLayout.SOUTH);
		setupDefaultStatusBar();
	}
	
	public BitmapRenderer getRenderer(){
		return renderer;
	}
	
	@Override
	public void prepareLoader(File dir, String[] fileNames){
		// TODO figure out preparing loaders
	}
	
	public void valueChanged(final ListSelectionEvent e){
		updateStatusIDs(model.getId(list.getSelectedIndex()));
	}
	
	public void adjustmentValueChanged(final AdjustmentEvent e){
		int from = list.getFirstVisibleIndex();
		int to = list.getLastVisibleIndex();
		
		model.pruneTo(from, to);
	}

	public void stateChanged(final ChangeEvent e){
		list.setFixedCellWidth(prototype.getImageWidth());
		list.setFixedCellHeight(prototype.getImageHeight());
	}
	
	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}