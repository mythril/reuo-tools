package reuo.resources.view;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class StatusBar extends JPanel {
	MigLayout layout;
	
	public StatusBar(){
		super();
		layout = new MigLayout(
			"",
			"0[fill, 50%]0",
			"0[fill, pref!]0"
		);

		setLayout(layout);
	}
	
	public void setColumnConstraints(String constraints){
		layout.setColumnConstraints(constraints);
		doLayout();
	}
}
