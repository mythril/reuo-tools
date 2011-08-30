package reuo.resources.view;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;

import reuo.resources.io.Loader;

/**
 * Abstract resource viewer of data from a {@link Loader}
 * 
 * @param <T> Loader type
 */
public abstract class Viewer<T extends Loader<?>> extends JSplitPane implements ListSelectionListener {
	protected String tabName;
	protected LabeledText hexID = new LabeledText("ID (Hex): ");
	protected LabeledText decID = new LabeledText("ID :");
	protected Border statusBorder = BorderFactory.createLineBorder(SystemColor.inactiveCaptionBorder);
	protected Loader<?> loader;
	private List<JComponent> status = new ArrayList<JComponent>();

	public Viewer(int orientation) {
		super(orientation);
	}

	public Viewer() {
		super(JSplitPane.HORIZONTAL_SPLIT);
	}

	public void setupDefaultStatusBar() {
		addStatusSection(hexID);
		addStatusSection(decID);
	}

	public abstract void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException;

	public List<JComponent> getStatus() {
		return status;
	}

	public void updateStatusIDs(int id) {
		hexID.setText(String.format("0x%X", id));
		decID.setText(String.format("%d", id));
	}

	public void addStatusSection(JComponent jc) {
		jc.setBorder(statusBorder);
		status.add(jc);
	}

	public void removeStatusSection(JComponent jc) {
		status.remove(jc);
	}

	public abstract String getStatusConstraints();
}
