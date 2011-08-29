package reuo.resources.view;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;

public class JRotatedLabel extends JLabel{
	public JRotatedLabel(String txt){
		super(txt);
	}
	
	/*
	@Override
	public Dimension getSize(){
		Dimension size = super.getSize();
		
		if(isPainting){
			return size;
		}
		
		return new Dimension(size.height, size.width);
	}
	*/
	
	@Override
	public Dimension getPreferredSize(){
		Dimension size = super.getPreferredSize();
		
		if(isPainting){
			return size;
		}
		
		return new Dimension(size.height, size.width);
	}
	
	/*
	@Override
	public int getWidth(){
		return isPainting ? super.getWidth() : super.getHeight();
	}
	
	@Override
	public int getHeight(){
		return isPainting ? super.getHeight() : super.getWidth();
	}
	*/
	
	private boolean isPainting = false;
	
	@Override
	public void paintComponent(Graphics lg){
		Graphics2D g = (Graphics2D)lg;
		isPainting = true;
		
		AffineTransform old = g.getTransform();
		AffineTransform tform = new AffineTransform(old);
		
		//tform.rotate(Math.PI / 2);
		
		g.setTransform(tform);
		super.paintComponent(g);
		g.setTransform(old);
		
		isPainting = false;
	}
}
