package reuo.resources.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import org.json.JSONException;

import reuo.resources.io.*;
import reuo.tools.AboutDialog;
import reuo.util.Configuration;


/** Main tool */
public class ResourceViewer extends JFrame implements ChangeListener {
	public final static String APPNAME = "vUO";
	public final static String CONFFILE = "vUO.json";
	
	protected File dir;
	protected Configuration configuration;
	protected JTabbedPane viewerTabs;
	protected Menu mainMenu = new Menu();
	protected FontViewer fontViewer;
	protected TextureViewer textureViewer;
	protected GumpViewer gumpViewer;
	protected SoundViewer soundViewer;
	protected SkillViewer skillViewer;
	protected ArtViewer artViewer;
	protected MultiViewer multiViewer;
	protected HueViewer hueViewer;
	protected JFileChooser dirChooser = new JFileChooser();
	protected StatusBar statusBar;
	protected SpeechViewer speechViewer;
	protected AboutDialog aboutDialog;

	public ResourceViewer() throws IOException {
		super(APPNAME);
		
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setDialogTitle("Select Your UO directory.");
		configuration = new Configuration();

		ImageIcon appIcon = new ImageIcon("binoculars.png");
		Image appImage = appIcon != null ? appIcon.getImage() : null;
		doConfigure();
		
		if (appImage != null) {
			setIconImage(appImage);
		}

		//setupMenu();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(1000, 700));
		setVisible(true);

		viewerTabs = new JTabbedPane();
		setJMenuBar(mainMenu);

		// FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		//GridLayout layout = new GridLayout(1, 0);
		statusBar = new StatusBar();
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(viewerTabs, BorderLayout.CENTER);
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		try {
			setupViewers();
		} catch (FileNotFoundException e2) {
			JOptionPane.showMessageDialog(this, "The .mul files were not found, either manually specify the\n" + "location in " + CONFFILE
					+ " (By setting the baseDir option) or\n" + "delete " + CONFFILE + " and use the file picker to fix the problem.",
					"Muls not found", JOptionPane.ERROR_MESSAGE);
			exitGracefully(1);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		viewerTabs.add("Textures", textureViewer);
		viewerTabs.add("Multis", multiViewer);
		viewerTabs.add("Art", artViewer);
		viewerTabs.add("Gumps", gumpViewer);
		viewerTabs.add("Skills", skillViewer);
		viewerTabs.add("Hues", hueViewer);
		viewerTabs.add("Speech", speechViewer);
		viewerTabs.add("Sounds", soundViewer);
		viewerTabs.add("Fonts", fontViewer);
		viewerTabs.addChangeListener(this);

		//shows the status bar
		stateChanged(null);

		saveConfig();
	}
	
	private void doConfigure() throws IOException {
		try {
			configuration.load(CONFFILE);
			dir = new File((String) configuration.get("baseDir"));
		} catch (JSONException e1) {
			System.out.println("Your " + CONFFILE + " file may be corrupt.");
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			int result = dirChooser.showOpenDialog(ResourceViewer.this);
			if (result == JFileChooser.CANCEL_OPTION) {
				exitGracefully();
			}
			dir = dirChooser.getSelectedFile();
			try {
				configuration.set("baseDir", dir.getAbsoluteFile());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void setupViewers() throws FileNotFoundException, IOException {
		String[] wordFiles = { "speech.mul" };
		String[] hueFiles = { "hues.mul" };
		String[] fontFiles = { "fonts.mul" };
		String[] textureFiles = { "texidx.mul", "texmaps.mul" };
		String[] skillFiles = { "skills.idx", "skills.mul" };
		String[] gumpFiles = { "gumpidx.mul", "gumpart.mul" };
		String[] soundFiles = { "soundidx.mul", "sound.mul" };
		String[] artFiles = { "artidx.mul", "art.mul" };
		String[] multiFiles = { "multi.idx", "multi.mul" };
		//String[] tileDataFiles = {"tiledata.mul" };

		SpriteDataLoader spriteDataLoader = new SpriteDataLoader();
		spriteDataLoader.prepare(new StandardPreparation<Preparation.None>(new File(dir, "tiledata.mul"), null, null)
		//new FileInputStream(new File(dir, "tiledata.mul")).getChannel()
				);

		hueViewer = new HueViewer(dir, hueFiles);
		speechViewer = new SpeechViewer(dir, wordFiles);
		fontViewer = new FontViewer(dir, fontFiles);
		textureViewer = new TextureViewer(dir, textureFiles);
		skillViewer = new SkillViewer(dir, skillFiles);
		gumpViewer = new GumpViewer(dir, gumpFiles);
		soundViewer = new SoundViewer(dir, soundFiles);
		artViewer = new ArtViewer(dir, artFiles);
		multiViewer = new MultiViewer(dir, multiFiles, artViewer.getLoader().getSpriteLoader(), spriteDataLoader);
	}

	/*
	private CleanUpThread cleanUp = new CleanUpThread(this);
	private void clearLoaders(){
		Viewer<?>[] viewers = (Viewer[]) viewerTabs.getComponents();
		for (int i = 0; i < viewers.length; i++) {
			viewers[i].clearLoaderCache();
		}
		
		synchronized(cleanUp){
			if(!cleanUp.isAlive()){cleanUp.start();}
			cleanUp.notifyAll();
		}
	}
	*/

	public void stateChanged(ChangeEvent event) {
		Component selected = viewerTabs.getSelectedComponent();

		if (selected instanceof Viewer) {
			Viewer<?> viewer = (Viewer<?>) selected;
			statusBar.removeAll();
			
			for (JComponent element : viewer.getStatus()) {
				statusBar.add(element);
			}
			
			viewer.restore();
			
			statusBar.setColumnConstraints(viewer.getStatusConstraints());
			statusBar.repaint();
		}
	}

	private static class Instance implements Runnable {
		//String[] args; FIXME dead code

		public Instance(String[] args) {
			//this.args = args;
		}

		public void run() {
			try {
				new ResourceViewer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void exitGracefully() {
		exitGracefully(0);
	}

	private void exitGracefully(int errorCode) {
		// TODO exit gracefully :)
		System.exit(errorCode);
	}

	private void saveConfig() throws IOException {
		try {
			configuration.save(CONFFILE);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		SwingUtilities.invokeLater(new Instance(args));
	}
	
	class Menu extends JMenuBar {
		public Menu() {
			add(createFileMenu());
			add(createHelpMenu());
		}
		
		public JMenu createFileMenu() {
			JMenu fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);

			JMenuItem setDir = new JMenuItem("Set Directory...");
			setDir.setMnemonic(KeyEvent.VK_D);

			fileMenu.add(setDir);
			setDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dirChooser.showOpenDialog(ResourceViewer.this);
				}
			});

			fileMenu.addSeparator();

			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);

			fileMenu.add(exitMenuItem);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exitGracefully();
				}
			});
			
			return fileMenu;
		}
		
		public JMenu createHelpMenu() {
			JMenu helpMenu = new JMenu("Help");
			
			
			helpMenu.add(new JMenuItem("Goto Website"));
			helpMenu.addSeparator();
			helpMenu.add(new JMenuItem(new AbstractAction("About") {
				public void actionPerformed(ActionEvent e) {
					if (aboutDialog == null) {
						aboutDialog = new AboutDialog(ResourceViewer.this);
					}
					
					aboutDialog.setVisible(true);
				}
			}));
			
			return helpMenu;
		}
	}
}
