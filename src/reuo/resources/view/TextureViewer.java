package reuo.resources.view;

import java.io.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import reuo.resources.Bitmap;
import reuo.resources.format.Rgb15To16;
import reuo.resources.io.*;

public class TextureViewer extends Viewer<TextureLoader> {
	TextureLoader loader;
	BitmapList list;
	JPanel detailsPanel;
	JPopupMenu menu;
	AsyncLoaderModel<Bitmap> model;
	JMenuItem exportItem;
	JSplitPane splitPane;

	public TextureViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader = new TextureLoader();
		prepareLoader(dir, fileNames);
		/*
				new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				new FileInputStream(new File(dir, fileNames[1])).getChannel(),
				Rgb15To16.getFormatter());*/

		setupDefaultStatusBar();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		menu = new JPopupMenu("Texture");
		menu.add(exportItem = new JMenuItem("Export..."));

		Bitmap prototype = new Bitmap(-1, 64, 64, null);
		model = new AsyncLoaderModel<Bitmap>(loader, prototype);

		BitmapRenderer<Bitmap> renderer = new BitmapRenderer<Bitmap>(0);
		list = new BitmapList(model, renderer, prototype);

		detailsPanel = new JPanel();
		detailsPanel.setBorder(new TitledBorder("Texture Information"));

		add(splitPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.add(list);
		splitPane.add(detailsPanel);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				splitPane.setDividerLocation(0.75);
				splitPane.setResizeWeight(1.0);
			}
		});

	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(new StoredIndexPreparation<Preparation.None>(new File(dir, fileNames[0]), new File(dir, fileNames[1]), Rgb15To16
				.getFormatter(), null));
	}

	public void valueChanged(ListSelectionEvent e) {
		Bitmap bitMap = list.getSelected();

		if (bitMap != null) {
			updateStatusIDs(bitMap.getId());
		} else {
			System.out.println("null");
		}
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
