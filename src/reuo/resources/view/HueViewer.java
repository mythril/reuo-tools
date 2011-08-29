package reuo.resources.view;

import java.io.*;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import reuo.resources.Hue;
import reuo.resources.format.Rgb15To16;
import reuo.resources.io.*;

public class HueViewer extends Viewer<HueLoader>{
	JTable table;
	HueLoader loader;
	HueRenderer hueRenderer;
	HueRenderer selectedHue;
	LoaderModel model;
	
	public HueViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException{
		loader = new HueLoader();
		prepareLoader(dir, fileNames);

		//removeStatusSection(decID);

		model = new LoaderModel(loader, loader);
		table = new JTable(new FieldTableModel(model, Hue.class, "ID:getId","Name:getName", "Hue:this"));
		hueRenderer = new HueRenderer();
		table.getColumnModel().getColumn(2).setCellRenderer(hueRenderer);
		
		selectedHue = new HueRenderer();
		setupDefaultStatusBar();
		addStatusSection(selectedHue);
		
		addImpl(new JScrollPane(table),JSplitPane.TOP,0);
		
		table.getSelectionModel().addListSelectionListener(this);
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(
			new StandardPreparation<Hue.Property>(
					new File(dir, fileNames[0]),
					Rgb15To16.getFormatter(), 
					EnumSet.allOf(Hue.Property.class)
				)
		);
	}

	public void valueChanged(ListSelectionEvent e) {
		int row = table.getSelectedRow();
		Hue hue = (Hue)table.getValueAt(row, 2);
		
		//selectedHue.setPreferredSize(new Dimension(64, 64));
		selectedHue.setHue(hue);
		updateStatusIDs(model.getId(table.getSelectedRow()));
		selectedHue.repaint();
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 25%]0[fill, 25%]0[fill, 50%]0";
	}
}
