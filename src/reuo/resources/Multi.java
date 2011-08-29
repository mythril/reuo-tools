package reuo.resources;

import java.nio.ByteBuffer;


/**
 * A structural arrangement of bitmaps. Each sprite is a
 * {@link Multi.Cell Cell} that has a location relative to this Structure
 * and an identifier for a sprite.
 * <h3>Dimensions</h3>
 * A structure exists in 3-space. The size along the x-axis is the <i>width</i>
 * @author Kristopher Ives, Lucas Green
 */
public class Multi implements Loadable{	
	final public int id;
	private int top, left, bottom, right;
	private int highest, lowest;
	private Cell[] unsorted;
	
	public Multi(int id){
		this.id = id;
	}
	
	public void load(ByteBuffer src){
		int x, y, z;
		
		top = left = lowest = Integer.MAX_VALUE;
		right = bottom = highest = Integer.MIN_VALUE;
		
		unsorted = new Cell[src.remaining() / 12];
		
		for(int i=0; i < unsorted.length; i++){
			unsorted[i] = new Cell();
			unsorted[i].load(src);
			
			x = unsorted[i].getX();
			y = unsorted[i].getY();
			z = unsorted[i].getZ();
			
			top = Math.min(top, y);
			left = Math.min(left, x);
			lowest = Math.min(lowest, z);
			bottom = Math.max(bottom, y);
			right = Math.max(right, x);
			highest = Math.max(highest, z);
		}
	}
	
	public Cell[] getCells(){
		return unsorted;
	}
	
	public int getId(){
		return id;
	}
	
	public int getLeft(){
		return left;
	}
	
	public int getTop(){
		return top;
	}
	
	public int getWidth(){
		return right - left;
	}
	
	public int getLength(){
		return bottom - top;
	}
	
	public int getHeight(){
		return highest - lowest;
	}
	
	public int getHighest(){
		return highest;
	}
	
	public int getLowest(){
		return lowest;
	}
	
	/**
	 * A component of the {@link Multi} which stores information about the
	 * position, height, and image that should be displayed. <s>This class
	 * implements {@link reuo.resources.Viewable} so that it may be sorted in
	 * drawing order.</s>
	 * 
	 * @author Kristopher Ives
	 */
	public static class Cell implements Loadable{
		private int spriteId;
		private int x, y, z;
		private int flags;
		
		public void load(ByteBuffer in){
			spriteId = in.getShort();
			x = in.getShort();
			y = in.getShort();
			z = in.getShort();
			flags = in.getInt();
		}
		
		public int getFlags(){
			return flags;
		}
		
		public int getSpriteId(){
			return spriteId;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
		
		public int getZ(){
			return z;
		}
	}
}
