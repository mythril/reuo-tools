package reuo.resources.view;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;

import org.json.*;

public class DataLocationPanel extends JPanel{
	JSONObject dataConf;
	JSONObject fileLocations;
	Map<String, LabelTextButton> fileButtons = new HashMap<String, LabelTextButton>();
	JScrollPane scrollPane = new JScrollPane();
	File basePath;
	private final static String BROWSE = "Browse";
	
	public DataLocationPanel(JSONObject dataConf){
		super(new BorderLayout());
		this.dataConf = dataConf;
		
		try {
			fileLocations = dataConf.getJSONObject("fileLocations");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JPanel top = new JPanel(new GridLayout(3,0));
		top.add(new JLabel(
				"Set this directory to the location of the .mul files."
				, JLabel.CENTER
				));
		
		
		LabelTextButton basePathSelector;
		try {
			basePathSelector = new LabelTextButton("Default Path",(String) fileLocations.get("basePath"),BROWSE);
		} catch (JSONException e) {
			e.printStackTrace();
			
			basePathSelector = null;
		}
		
		top.add(basePathSelector);
		
		JPanel subTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JCheckBox useDefaults = new JCheckBox("Use the default path?");
		subTop.add(useDefaults);
		JButton resetPaths = new JButton("Reset paths");
		subTop.add(resetPaths);
		JButton resetFiles = new JButton("Reset file names");
		subTop.add(resetFiles);
		
		top.add(subTop);
		add(top, BorderLayout.NORTH);
		
		JPanel bottom = new JPanel(new GridLayout(0,1));
		
		String[] fileNames = {
				"anim.idx",		"anim.mul",		"artidx.mul",	"art.mul",
				"fonts.mul",	"gumpart.mul",	"gumpidx.mul",	"hues.mul",
				"multi.idx",	"multi.mul",	"skills.idx",	"skills.mul",
				"sound.mul",	"soundidx.mul",	"speech.mul",	"texidx.mul",
				"texmaps.mul"
		};
		
		for (int i = 0; i < fileNames.length; i++) {
			try {
				fileButtons.put(
					fileNames[i], 
					new LabelTextButton(fileNames[i],(String) fileLocations.get(fileNames[i]),BROWSE)
				);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bottom.add(fileButtons.get(fileNames[i]));
		}
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setAutoscrolls(true);
		scrollPane.setViewportView(bottom);
		add(scrollPane/*bottom*/, BorderLayout.CENTER);
	}

	public static class FileDialogOpener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	public static void main(String[] args) throws JSONException {
		JFrame app = new JFrame();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSONObject json = new JSONObject();
		JSONObject fileLoc = new JSONObject();
		fileLoc.put("basePath","/");

		String[] fileNames = {
				"anim.idx",		"anim.mul",		"artidx.mul",	"art.mul",
				"fonts.mul",	"gumpart.mul",	"gumpidx.mul",	"hues.mul",
				"multi.idx",	"multi.mul",	"skills.idx",	"skills.mul",
				"sound.mul",	"soundidx.mul",	"speech.mul",	"texidx.mul",
				"texmaps.mul"
		};
		
		for (int i = 0; i < fileNames.length; i++) {
			fileLoc.put(fileNames[i], fileNames[i]);
		}
		
		json.put("fileLocations", fileLoc);
		
		app.setContentPane(new DataLocationPanel(json));
		app.setVisible(true);
		
	}
}
