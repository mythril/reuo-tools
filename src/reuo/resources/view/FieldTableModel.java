package reuo.resources.view;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.*;
import javax.swing.table.TableModel;

public class FieldTableModel implements TableModel{
	private static Object[] nothing = new Object[0];
	ListModel<?> rows;
	Object[] columns;
	Class<?> rowType;
	String[] titles;
	List<TableModelListener> listeners = new ArrayList<TableModelListener>();
	
	public FieldTableModel(ListModel<?> rows, Class<?> c, String... fieldNames) throws IllegalArgumentException{
		this.rows = rows;
		this.rowType = c;
		
		rows.addListDataListener(new ListDataObserver());
		ArrayList<Object> members = new ArrayList<Object>();
		ArrayList<String> titleList = new ArrayList<String>();
		
		for(String name : fieldNames){
			int pos = name.indexOf(":");
			
			if(pos > 0){
				titleList.add(name.substring(0, pos));
				name = name.substring(pos+1);
			}else{
				titleList.add("");
			}
			
			if(name.equals("this") || name.equals("row") || name.equals("column")){
				members.add(name);
				continue;
			}
			
			try{
				members.add(c.getField(name));
				continue;
			}catch(SecurityException e){
				
			}catch(NoSuchFieldException e){
				
			}
			
			try{
				members.add(c.getMethod(name));
			}catch(NoSuchMethodException e){
				throw(new IllegalArgumentException());
			}
			
		}
		
		titles = new String[titleList.size()];
		titles = titleList.toArray(titles);
		columns = members.toArray();
	}
	
	private Class<?> translateClass(Class<?> type){
		if(type == int.class) return(Integer.class);
		if(type == float.class) return(Float.class);
		if(type == double.class) return(Double.class);
		if(type == char.class) return(Character.class);
		if(type == byte.class) return(Byte.class);
		if(type == long.class) return(Long.class);
		if(type == boolean.class) return(Boolean.class);
		
		return(type);
	}
	
	public Class<?> getColumnClass(int column){
		if(columns[column] instanceof Field){
			Field field = (Field)columns[column];
			
			return(translateClass(field.getType()));
		}else if(columns[column] instanceof Method){
			Method method = (Method)columns[column];
			
			return(translateClass(method.getReturnType()));
		}
		
		if(columns[column].equals("row") || columns[column].equals("column")){
			return(Integer.class);
		}
		
		return(rowType);
	}
	
	public int getColumnCount(){
		return columns.length;
	}
	
	public String getColumnName(int column){
		return titles[column];
	}
	
	public int getRowCount(){
		return rows.getSize();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex){
		Object row = rows.getElementAt(rowIndex);
		
		if(columns[columnIndex].equals("this")){
			return(row);
		}else if(columns[columnIndex].equals("row")){
			return(rowIndex);
		}else if(columns[columnIndex].equals("column")){
			return(columnIndex);
		}
		
		if(columns[columnIndex] instanceof Field){
			Field field = (Field)columns[columnIndex];
			Class<?> type = field.getType();
			
			try{
				if(type == int.class){
					return field.getInt(row);
				}else if(type == float.class){
					return field.getFloat(row);
				}else if(type == double.class){
					return field.getDouble(row);
				}else if(type == char.class){
					return field.getChar(row);
				}else if(type == byte.class){
					return field.getByte(row);
				}else if(type == long.class){
					return field.getLong(row);
				}else if(type == boolean.class){
					return field.getBoolean(row);
				}else{
					return field.get(row);
				}
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}else if(columns[columnIndex] instanceof Method){
			Method method = (Method)columns[columnIndex];
			
			try{
				return method.invoke(row, nothing);
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return(false);
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		
	}
	
	public void addTableModelListener(TableModelListener l){
		if(!listeners.contains(l)){
			listeners.add(l);
		}
	}
	
	public void removeTableModelListener(TableModelListener l){
		listeners.remove(l);
	}
	
	private class ListDataObserver implements ListDataListener{
		public void contentsChanged(ListDataEvent e){
			TableModelEvent event = new TableModelEvent(
				FieldTableModel.this,
				e.getIndex0(), e.getIndex1()
			);
			
			for(TableModelListener l : listeners){
				l.tableChanged(event);
			}
		}

		public void intervalAdded(ListDataEvent e){
			System.out.println(e);
		}

		public void intervalRemoved(ListDataEvent e){
			System.out.println(e);
		}
		
	}
}
