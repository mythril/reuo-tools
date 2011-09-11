package reuo.resources.view;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import reuo.resources.Animation;
import reuo.resources.format.*;
import reuo.resources.io.*;
import reuo.resources.view.BitmapRenderer.Metric;

public class SimpleAnimViewer extends Viewer<AnimationLoader> implements ListSelectionListener, TableModelListener, AdjustmentListener{
	AnimationLoader loader;
	Formatter formatter = Rgb15To16.getFormatter();
	BitmapRenderer<Animation.Frame> renderer;
	AsyncLoaderModel<Animation> model;
	JScrollPane animListPane, animFocusPane;
	Animation anim;
	JTable table;
	JSplitPane splitPane;
	
	public SimpleAnimViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader = new AnimationLoader();
		prepareLoader(dir, fileNames);
		
		splitPane = hsplit();
		
		renderer = new BitmapRenderer<Animation.Frame>(1);
		model = new AsyncLoaderModel<Animation>(loader, null);
		animFocusPane = new JScrollPane(renderer);
		//animListPane = new JScrollPane(renderer);
		
		table = new JTable(new FieldTableModel(model, Animation.Frame.class, "Id:getId", "this"));
		table.setRowHeight(64);
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
		iconColumn.setCellRenderer(new BitmapRenderer<Animation.Frame>(1, Metric.SCALED));
		
		splitPane.add(animListPane = new JScrollPane(table));
		splitPane.add(animFocusPane);
		
		animListPane.getHorizontalScrollBar().addAdjustmentListener(this);
		animListPane.getVerticalScrollBar().addAdjustmentListener(this);
		
		splitPane.setDividerLocation(96);
		splitPane.setResizeWeight(0.0);
		add(splitPane);
	}
	
	public int getSelectedId() {
		return table.getSelectionModel().getMinSelectionIndex();
	}
	
	public void updateSelection () {
		try {
			int animId = getSelectedId();
			anim = loader.get(animId);
			
			if (anim != null) {
				updateStatusIDs(model.getId(table.getSelectedRow()));
				renderer.setBitmap(anim.getFrame(0));
				animFocusPane.doLayout();
				animFocusPane.repaint();
			}
		} catch (IOException e) {
			//TODO something legit
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		updateSelection();
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames)
			throws FileNotFoundException, IOException {
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, fileNames[0]),
						new File(dir, fileNames[1]),
						formatter,
						null
				)
		);
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}

	@Override
	public void tableChanged(TableModelEvent event) {
		int index = table.getSelectionModel().getMinSelectionIndex();
		
		if(index < event.getFirstRow() || index > event.getLastRow()){
			return;
		}
		
		updateSelection();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		Utilities.prune(table, model);
	}

}
