package reuo.resources.view;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.*;

import reuo.util.Configuration;

public class Configurator extends JFrame {
	Configuration config;
	JTabbedPane tabPane;
	//DataLocationPanel dlPanel = new DataLocationPanel();
	
	public Configurator() throws IOException {
		this(new Configuration());
	}
	
	public Configurator(Configuration config){
		super("ResourceViewer Options");
		this.config = config;
		setLayout(new BorderLayout());
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		//tabPane.addTab("File Locations", dlPanel);
		add(tabPane, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) throws IOException {
		Configurator app = new Configurator();
		app.setDefaultCloseOperation(EXIT_ON_CLOSE);
		app.setVisible(true);
	}
}
