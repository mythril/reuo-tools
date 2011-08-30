package reuo.resources.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import reuo.resources.Bitmap;
import reuo.resources.Font;
import reuo.resources.format.Rgb15To16Masked;
import reuo.resources.io.FontLoader;
import reuo.resources.io.Preparation;
import reuo.resources.io.StandardPreparation;

public class FontViewer extends Viewer<FontLoader> implements ListSelectionListener{
	FontLoader loader;
	LoaderModel loaderModel;
	ArrayModel arrayModel;
	JList index, glyphs;
	JSplitPane splitPane;
	LabeledText selectedChar = new LabeledText("Character selected: ");
	
	public FontViewer(File dir, String fileNames[]) throws FileNotFoundException, IOException{
		loader = new FontLoader();
				/*new FileInputStream(new File(dir, fileNames[0])).getChannel(),
				Rgb15To16Masked.getFormatter());*/
		prepareLoader(dir, fileNames);
		
		addStatusSection(selectedChar);
		
		loaderModel = new LoaderModel(loader);
		arrayModel = new ArrayModel(new Object[224]);
		
		BitmapRenderer<Bitmap> bitMapRenderer = new BitmapRenderer<Bitmap>(1);
		
		glyphs = new JList(arrayModel);
		glyphs.setCellRenderer(bitMapRenderer);
		glyphs.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		glyphs.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		glyphs.setVisibleRowCount(-1);
		glyphs.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				char selected =(char) (glyphs.getSelectedIndex() + 32); 
				
				selectedChar.setText(String.valueOf(selected));
			}
		});


		index = new JList(loaderModel);
		index.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		index.setLayoutOrientation(JList.VERTICAL);
		index.setVisibleRowCount(-1);
		index.addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(index);
		JScrollPane glyphScroll = new JScrollPane(glyphs);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, glyphScroll);
		splitPane.setOneTouchExpandable(true);
		//setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
	}
	
	public void valueChanged(ListSelectionEvent event){
		Font font = (Font)index.getSelectedValue();
		if(font == null){
			return;
		}
		glyphs.setModel(new ArrayModel(font.getGlyphs()));
		glyphs.repaint();
	}
	
	
	@Override
	public void prepareLoader(File dir, String[] fileNames) throws FileNotFoundException, IOException{
		loader.prepare(
				new StandardPreparation<Preparation.None>(
						new File(dir, fileNames[0]),
						Rgb15To16Masked.getFormatter(),
						null
				)
		);
	}

	@Override
	public String getStatusConstraints() {
		return "0[fill, 100%]0";
	}
}
