package reuo.resources.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import reuo.resources.Animation;
import reuo.resources.PalettedBitmap;
import reuo.resources.format.Formatter;
import reuo.resources.format.Rgb15To16;
import reuo.resources.format.Utilities;
import reuo.resources.io.AnimationLoader;
import reuo.resources.io.Preparation;
import reuo.resources.io.StoredIndexPreparation;

public class SimpleAnimViewer extends Viewer<AnimationLoader> implements ListSelectionListener, AdjustmentListener{
	AnimationLoader loader;
	Formatter formatter = Rgb15To16.getFormatter();
	AsyncLoaderModel<Animation> model;
	JScrollPane animListPane;
	AnimPane animFocusPane = new AnimPane();
	Animation anim;
	JList<Animation> list;
	//JSplitPane splitPane;
	
	public SimpleAnimViewer(File dir, String[] fileNames) throws FileNotFoundException, IOException {
		loader = new AnimationLoader();
		prepareLoader(dir, fileNames);
		
		//splitPane = hsplit();
		
		model = new AsyncLoaderModel<Animation>(loader, null);
		//animFocusPane = new JScrollPane();
		//animListPane = new JScrollPane(renderer);
		
		list = new JList<Animation>(model);
		
		list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel().addListSelectionListener(this);
		
		add(animListPane = new JScrollPane(list));
		add(animFocusPane);
		
		animListPane.getHorizontalScrollBar().addAdjustmentListener(this);
		animListPane.getVerticalScrollBar().addAdjustmentListener(this);
		
		setDividerLocation(96);
		setResizeWeight(0.0);
		//add(splitPane);
		list.setSelectedIndex(0);
		updateSelection();
	}
	
	public void updateSelection () {
		//try {
			//int animId = getSelectedId();
			//anim = loader.get(animId);
			anim = list.getSelectedValue();
			
			if (anim != null) {
				updateStatusIDs(Integer.valueOf(anim.toString()));
				animFocusPane.doLayout();
				animFocusPane.repaint();
			}
//		} catch (IOException e) {
//			//TODO something legit
//			e.printStackTrace();
//			return;
//		}
	}
	
	public class AnimPane extends JPanel{
		int frameIndex = 0;
		Timer timer;
		
		public AnimPane() {
			timer = new Timer();
			timer.schedule(new Animator(), 50, 50);
		}
		
		@Override
		protected void paintComponent(Graphics legacy) {
			super.paintComponent(legacy);
			Graphics2D g = (Graphics2D) legacy; 
			
			
			if (anim != null) {
				frameIndex = (int) ((System.currentTimeMillis() / 100) % anim.getFrames().size());
				Animation.Frame frame = anim.getFrame(frameIndex);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				
				PalettedBitmap img = (PalettedBitmap) frame;
				g.drawImage(Utilities.getImage(img, 1), 0, 0, img.getWidth()*2, img.getHeight()*2, null);
			}
		}
		
		private class Animator extends TimerTask {

			@Override
			public void run() {
				repaint();
			}
			
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		updateSelection();
	}

	@Override
	public void prepareLoader(File dir, String[] fileNames)
			throws FileNotFoundException, IOException {
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, fileNames[0]),
						new File(dir, fileNames[1]),
						formatter,
						null
				)
		);
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 50%]0";
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		repaint();
	}

}
