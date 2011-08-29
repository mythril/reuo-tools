package reuo.resources.view;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class LabeledText extends JPanel implements MouseListener, ClipboardOwner{
	private Border hoverBorder = BorderFactory.createLineBorder(SystemColor.inactiveCaptionBorder);
	private Border normalBorder = BorderFactory.createEmptyBorder(1,1,1,1);
	private JLabel label;
	private JTextArea text;
	private static FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
	
	LabeledText(String label){
		this(label,"");
	}
	
	LabeledText(String label, String text){
		super(layout);
		layout.setHgap(0);
		layout.setVgap(0);
		this.label = new JLabel(label);
		this.text = new JTextArea(text);
		this.text.setEditable(false);
		this.text.setBorder(normalBorder);
		this.text.setLineWrap(false);
		this.text.setOpaque(false);
		this.text.addMouseListener(this);
		this.text.setToolTipText("Click copies to clipboard.");
		add(this.label);
		add(this.text);
	}
	
	public void setText(String text){
		this.text.setText(text);
	}
	
	public void setLabelText(String label){
		this.label.setText(label);
	}

	public void mouseClicked(MouseEvent e) {
		StringSelection selection =
			new StringSelection(this.text.getText());
		Toolkit.getDefaultToolkit()
			.getSystemClipboard()
			.setContents(selection, this);
	}
	
	public void mouseEntered(MouseEvent e) {
		this.text.setBorder(hoverBorder);
	}
	
	public void mouseExited(MouseEvent e) {
		this.text.setBorder(normalBorder);
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
