package reuo.resources.view;

import java.awt.Component;

import javax.swing.*;

import reuo.resources.*;
import reuo.resources.format.Utilities;

public class HueRenderer extends BitmapRenderer<Bitmap> {
	public HueRenderer(){
		super(1, Metric.STRETCHED);
	}
	
	public void setHue(Hue hue){
		setBitmap(Utilities.paletteToBitmap(hue.getPalette()));
	}
	
	@Override
	public void setBitmap(Bitmap bmp){
		super.setBitmap(bmp);
		
		setPreferredSize(null);
	}

	public Component getListCellRendererComponent(JList<Bitmap> list, Hue value, int index, boolean isSelected, boolean cellHasFocus){
		Bitmap hue = Utilities.paletteToBitmap(((Hue) value).getPalette());
		return super.getListCellRendererComponent(list, hue, index, isSelected, cellHasFocus);
		//return super.getListCellRendererComponent(list,(Object)hue,index,isSelected,cellHasFocus);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Bitmap hue = Utilities.paletteToBitmap(((Hue) value).getPalette());
		return super.getTableCellRendererComponent(table,(Object)hue,isSelected,hasFocus,row, column);
	}
}
