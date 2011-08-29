package reuo.resources.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class SpeechTreeRenderer extends JTree implements TableCellRenderer{
	protected int visibleRow;
	
	public SpeechTreeRenderer(){
		
	}
	
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column){
		
		return this;
	}
}
