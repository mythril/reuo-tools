package reuo.resources.view;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import reuo.resources.Bitmap;
import reuo.resources.format.*;
import reuo.resources.io.*;
import reuo.resources.view.BitmapRenderer.Metric;

public class GumpViewer extends Viewer<GumpLoader> implements ListSelectionListener, TableModelListener, AdjustmentListener{
	BitmapRenderer<Bitmap> renderer;
	GumpLoader loader;
	JTable table;
	BufferedImage image = null;
	JScrollPane gumpScrollPane, listScrollPane;
	AsyncLoaderModel model;
	JSplitPane splitPane;
	LabeledText widthLabel = new LabeledText("Width: ");
	LabeledText heightLabel = new LabeledText("Height: ");
	
	public GumpViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException{		
		loader = new GumpLoader();
		prepareLoader(dir, fileNames);
/*				new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				new FileInputStream(new File(dir, fileNames[1])).getChannel(),
				Rgb15To16Masked.getFormatter()
			);*/
		
		setupDefaultStatusBar();
		addStatusSection(widthLabel);
		addStatusSection(heightLabel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		renderer = new BitmapRenderer<Bitmap>(1);
		renderer.setChecker(new Checker(this, 16));
		
		gumpScrollPane = new JScrollPane(renderer);
		
		model = new AsyncLoaderModel(loader, null);
		
		table = new JTable(new FieldTableModel(model, Bitmap.class, "Id:getId", "this"));
		table.setRowHeight(32);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		table.getModel().addTableModelListener(this);
		table.setDoubleBuffered(true);
		
		table.getSelectionModel().addListSelectionListener(this);

		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setCellRenderer(cellRenderer);
		
		TableColumn iconColumn = table.getColumnModel().getColumn(1);
		iconColumn.setCellRenderer(new BitmapRenderer<Bitmap>(1, Metric.SCALED));
		
		splitPane.add(listScrollPane = new JScrollPane(table));
		splitPane.add(gumpScrollPane);
		
		listScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		listScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		
		splitPane.setDividerLocation(96);
		splitPane.setResizeWeight(0.0);
		add(splitPane);
	}
	
	private Bitmap getSelectedBitMap(){
		int index = table.getSelectionModel().getMinSelectionIndex();
		Object sel = table.getValueAt(index, table.convertColumnIndexToModel(1));
		
		return((Bitmap)sel);
	}
	
	public void valueChanged(ListSelectionEvent event){
		updateSelection();
	}
	
	public void tableChanged(TableModelEvent event){
		int index = table.getSelectionModel().getMinSelectionIndex();
		
		if(index < event.getFirstRow() || index > event.getLastRow()){
			return;
		}
		
		updateSelection();
	}
	
	private void updateSelection(){
		Bitmap bitMap = getSelectedBitMap();
		
		//System.out.println("fired");
		if(bitMap != null){
			image = Utilities.getImage(bitMap, 1);

			updateStatusIDs(model.getId(table.getSelectedRow()));
			widthLabel.setText(String.valueOf(bitMap.getImageWidth()));
			heightLabel.setText(String.valueOf(bitMap.getImageHeight()));
			
			//renderer.setIcon(new ImageIcon(image));
			renderer.setBitmap(bitMap);
			gumpScrollPane.doLayout();
			gumpScrollPane.repaint();
		}
	}

	public void adjustmentValueChanged(AdjustmentEvent event){
		Utilities.prune(table, model);
	}
	
	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		/*loader.prepare(
				new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				new FileInputStream(new File(dir, fileNames[1])).getChannel(),
				Rgb15To16Masked.getFormatter()
			);*/
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, fileNames[0]),
						new File(dir, fileNames[1]),
						Rgb15To16Masked.getFormatter(),
						null
				)
		);
	}


	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
