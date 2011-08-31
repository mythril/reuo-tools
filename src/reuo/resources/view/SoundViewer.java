package reuo.resources.view;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.EnumSet;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import reuo.resources.Sound;
import reuo.resources.format.*;
import reuo.resources.io.*;

public class SoundViewer extends Viewer<SoundLoader> implements DocumentListener, MouseListener, AdjustmentListener {
	SoundLoader loader;
	AsyncLoaderModel<Sound> model;
	JTable table;
	JTextField filter;
	Clip clip = null;
	AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);

	public SoundViewer(File dir, String[] fileNames) throws IOException {
		//removeStatusSection(decID);

		setLayout(new BorderLayout());

		setupDefaultStatusBar();

		loader = new SoundLoader();
		prepareLoader(dir, fileNames);
		// this.nameLoader = new SoundLoader(loader,
		// EnumSet.of(SoundLoader.Property.NAME));
		// this.dataLoader = new SoundLoader(loader,
		// EnumSet.of(SoundLoader.Property.DATA));

		//Sound prototype = new Sound(-1);// , "loading...", null);
		// loaderModel = new AsyncLoaderModel(nameLoader, prototype);
		model = new AsyncLoaderModel<Sound>(loader, null);

		table = new JTable(new FieldTableModel(model, Sound.class, "Id:getId", "Filename:getName"));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// table.setAutoCreateRowSorter(true);
		table.addMouseListener(this);
		table.getSelectionModel().addListSelectionListener(this);

		SimpleCell nameCellRenderer = new SimpleCell();
		// nameCellRenderer.setIcon(new ImageIcon("icons/sound.png"));
		nameCellRenderer.setHorizontalTextPosition(JLabel.RIGHT);

		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setPreferredWidth(48);

		TableColumn nameColumn = table.getColumnModel().getColumn(1);
		nameColumn.setPreferredWidth(128);
		nameColumn.setCellRenderer(nameCellRenderer);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(new JLabel("Filter: "), BorderLayout.WEST);
		topPanel.add(filter = new JTextField(), BorderLayout.CENTER);

		filter.getDocument().addDocumentListener(this);

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
	}

	private void filter() {
		// TableRowSorter sorter = (TableRowSorter)table.getRowSorter();
		// sorter.setRowFilter(RowFilter.regexFilter(filter.getText(), new
		// int[0]));
	}

	public void changedUpdate(DocumentEvent arg0) {
		filter();
	}

	public void insertUpdate(DocumentEvent arg0) {
		filter();
	}

	public void removeUpdate(DocumentEvent arg0) {
		filter();
	}

	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() > 1) {
			int column = table.convertColumnIndexToModel(0);

			int row = table.getSelectionModel().getMinSelectionIndex();
			int id = (Integer) table.getValueAt(row, column);

			if (id < 0) {
				return;
			}

			try {
				Sound snd = loader.get(id, EnumSet.of(Sound.Property.DATA));

				DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
				clip = (Clip) AudioSystem.getLine(info);

				ByteBuffer data = snd.getData();
				data.rewind();
				DataMetrics metrics = Utilities.readArray(data);

				clip.open(audioFormat, metrics.array, metrics.pos, metrics.length);
				clip.setFramePosition(0);
				clip.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	class SimpleCell extends DefaultTableCellRenderer {
		public SimpleCell() {
			setHorizontalTextPosition(LEFT);
		}
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(new StoredIndexPreparation<Sound.Property>(new File(dir, fileNames[0]), new File(dir, fileNames[1]), null, EnumSet
				.of(Sound.Property.NAME)));
	}

	public void valueChanged(ListSelectionEvent e) {
		updateStatusIDs(model.getId(table.getSelectedRow()));
	}

	public void adjustmentValueChanged(AdjustmentEvent event) {
		Utilities.prune(table, model);
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
