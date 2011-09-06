package reuo.resources.view;

import java.io.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import reuo.resources.Speech;
import reuo.resources.io.*;

public class SpeechViewer extends Viewer<SpeechLoader> {
	SpeechLoader loader;
	AsyncLoaderModel<Speech> loaderModel;
	FieldTableModel model;
	JTable table;
	JScrollPane scrollPane;
	LabeledText transLabel = new LabeledText("Translations: ");

	public SpeechViewer(File dir, String[] fileNames) throws IOException {
		loader = new SpeechLoader();
		prepareLoader(dir, fileNames);

		setupDefaultStatusBar();
		addStatusSection(transLabel);

		loaderModel = new AsyncLoaderModel<Speech>(loader, null);
		model = new FieldTableModel(loaderModel, Speech.class, "Id:getId", "Words:this");
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(32);
		table.getColumnModel().getColumn(1).setPreferredWidth(900);
		
		// SpeechTreeRenderer renderer = new SpeechTreeRenderer();
		// table.getColumnModel().getColumn(1).setCellRenderer(renderer);

		table.getSelectionModel().addListSelectionListener(this);

		scrollPane = new JScrollPane(table);

		add(scrollPane);
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(new StandardPreparation<Preparation.None>(new File(dir, fileNames[0]), null, null));
	}

	public void valueChanged(ListSelectionEvent e) {
		Speech tmpSpeech = (Speech) model.getValueAt(table.getSelectedRow(), 1);
		if (tmpSpeech == null) {
			return;
		}
		int tmp = tmpSpeech.getTranslationCount();
		transLabel.setText(String.valueOf(tmp));
		updateStatusIDs(loaderModel.getId(table.getSelectedRow()));
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
