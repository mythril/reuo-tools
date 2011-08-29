package reuo.resources.view;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class LabelTextButton extends JPanel {
	protected JLabel label;
	protected JTextField text;
	protected JButton button;
	
	public LabelTextButton(String label, String text, String button) {
		MigLayout layout = new MigLayout(
				"wrap 3",
				"[fill, 20%]0[fill, 55%]0[fill, 20%]",
				"0[fill, pref!]0"
				);
		setLayout(layout);
		add(this.label = new JLabel(label, JLabel.RIGHT));
		add(this.text = new JTextField(text));
		add(this.button = new JButton(button));
	}
	
	public JLabel getLabel(){return label;}
	public JTextField getTextField(){return text;}
	public JButton getButton(){return button;}
	public void setLabel(JLabel jl){label = jl;}
	public void setTextField(JTextField jt){text = jt;}
	public void setButton(JButton jb){button = jb;}
}
