package reuo.resources.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;

import reuo.resources.Bitmap;
import reuo.resources.format.Rgb15To16Masked;
import reuo.resources.io.ArtLoader;
import reuo.resources.io.Preparation;
import reuo.resources.io.StoredIndexPreparation;
import reuo.resources.view.BitmapRenderer.Metric;

public class ArtViewer extends Viewer<ArtLoader>{
	ArtLoader loader;
	//AsyncLoaderModel model;
	JScrollPane scrollPane;
	LabeledText widthLabel = new LabeledText("Width: ");
	LabeledText heightLabel = new LabeledText("Height: ");
	JTabbedPane artTabs;
	BitmapViewer spriteArtViewer, tileArtViewer, legacyArtViewer;
	//Bitmap prototype;
	
	public ArtViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException{		
		loader = new ArtLoader();
		prepareLoader(dir,fileNames);
		
		setupDefaultStatusBar();
		addStatusSection(widthLabel);
		addStatusSection(heightLabel);
		
		//prototype = new Bitmap(-1, 44, 44, null);
		BitmapRenderer<Bitmap> renderer = new BitmapRenderer<Bitmap>(1, Metric.SCALED);
		
		artTabs = new JTabbedPane(JTabbedPane.LEFT);
		
		String[] names = new String[]{ "Tiles", "Sprites", "Legacy" };
		Viewer<?>[] subViewers = new Viewer<?>[]{
			tileArtViewer = new BitmapViewer(loader.getTileArtLoader(), renderer),
			spriteArtViewer = new BitmapViewer(loader.getSpriteLoader(), renderer),
			legacyArtViewer = new BitmapViewer(loader.getLegacyArtLoader(), renderer)
		};
		
		for(int i=0; i < subViewers.length; i++){			
			artTabs.add(names[i], subViewers[i]);
			artTabs.setTabComponentAt(i, new JRotatedLabel(names[i]));
			
			/*JXTransformer t = new JXTransformer();
			t.add((JComponent) artTabs.getTabComponentAt(i));
			t.rotate(-Math.PI/2.0);
			AffineTransform at = t.getTransform();
			at.translate(20, 50);
			t.setTransform(at);
			artTabs.setTabComponentAt(i, t);*/
		}
		
		add(artTabs);
	}
	
	@Override
	public List<JComponent> getStatus(){
		Viewer<?> subViewier = (Viewer<?>)artTabs.getSelectedComponent();
		
		return subViewier.getStatus();
	}
	
	public ArtLoader getLoader(){
		return loader;
	}
	
	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, fileNames[0]),
						new File(dir, fileNames[1]),
						Rgb15To16Masked.getFormatter(),
						null
				)
			);
	}

	public void valueChanged(ListSelectionEvent e) {
		
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}
}
