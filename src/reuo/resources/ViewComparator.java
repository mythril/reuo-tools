package reuo.resources;

import java.util.Comparator;

public class ViewComparator<T extends Viewable> implements Comparator<T>{
	public int compare(T a, T b){
		return key(a) - key(b);
	}
	
	public static int key(Viewable v){
		int a = (v.getX() + v.getY() - 0x7FFF) & 0xFFFF;
		int b = (v.getZ() + 0x7F) & 0xFF;
		int c = (v.getHeight() - 0x7F) & 0xFF;
		
		return (a << 18) | (b << 10) | c;
	}
}
