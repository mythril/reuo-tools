package reuo.resources;

import java.nio.Buffer;

import reuo.util.Rect;

public class Animation {
	public class Frame extends PalettedBitmap{
		// Stub for later extension
		public Frame(int id, int width, int height, Buffer data, Rect insets) {
			super(id, width, height, data, insets);
		}
	}
	
	public class Sequences {
		private Palette pal;
		
	}
}

/*
package reuo.resources;

import java.nio.*;
import java.util.*;


public class Animation{
	public static class DataLayout{
		private final SortedMap<Integer, Integer> frameHeader, none;
		{
			frameHeader = new TreeMap<Integer, Integer>();
			frameHeader.put(0x100, 4);
			
			none = new TreeMap<Integer, Integer>();
		}
		
		private final EnumSet<Property> properties;
		private int[] table = null;
		
		public DataLayout(EnumSet<Property> properties){
			this.properties = properties;
		}
		
		public void prepare(SortedMap<Integer, ByteBuffer> buffers) throws BufferUnderflowException{
			ByteBuffer headerBuffer, tableBuffer;
			
			headerBuffer = buffers.get(0x100);
			tableBuffer = buffers.get(0x100 + 4);
			
			if(headerBuffer != null){
				table = new int[headerBuffer.getInt()];
			}
			
			if(tableBuffer != null){
				for(int i=0; i < table.length; i++){
					table[i] = tableBuffer.getInt();
				}
			}
		}
		
		public void reset(){
			this.table = null;
		}
		
		public boolean needsMore(){
			return table == null;
		}
		
		public SortedMap<Integer, Integer> requirements(){
			if(properties.contains(Property.FRAMES)){
				return frameHeader;
			}
			
			return none;
		}
	}
	
	public enum Property{
		PALETTE, FRAMES
	}
}
*/
/*
public class Animation extends Resource<Animation.Property>{
	public enum Property implements Resource.Property{
		PALETTE(Palette.class),
		FRAMES(Frame[].class);
		
		private final Class<?> type;
		private Property(Class<?> t){ this.type = t; }
		public Class<?> getType(){ return type; }
	}
	
	private int id;
	private Palette palette;
	private Frame[] frames;
	
	public Animation(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public Frame getFrame(int index) throws IndexOutOfBoundsException{
		return frames[index];
	}
	
	@Override
	public boolean has(Property property) throws IllegalArgumentException{
		switch(property){
		case PALETTE:	return palette != null;
		case FRAMES:	return frames != null;
		}
		
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object get(Property property) throws IllegalArgumentException{
		if(!has(property)) throw new IllegalStateException();
		
		switch(property){
		case PALETTE:	return palette;
		case FRAMES:	return frames;
		}
		
		throw new IllegalArgumentException();
	}
	
	@Override
	public void load(Property property, ByteBuffer data) throws IllegalArgumentException{		
		switch(property){
		case PALETTE:
			int oldLimit = data.limit();
			data.mark();
			data.limit(data.position() + 256 * 2);
			palette = new Palette(data.slice().order(ByteOrder.LITTLE_ENDIAN), 256, null);
			
			data.reset().limit(oldLimit);
			break;
		case FRAMES:
			int count = data.getInt();
			data.limit(data.position() + count * 4);
			ByteBuffer table = data.slice().order(ByteOrder.LITTLE_ENDIAN);
			frames = new Frame[count];
			
			for(int i=0; i < frames.length; i++){
				data.position(table.getInt());
				
				frames[i] = new Frame();
				
			}
			
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void skip(Property property, ByteBuffer buf) throws IllegalArgumentException{
		switch(property){
		case PALETTE:
			buf.position(buf.position() + 256 * 2);
			break;
		case FRAMES:
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static class{//Frame extends Resource<Frame.Property>{
		public enum Property implements Resource.Property{
			POSITION(int[].class), IMAGE(ByteBuffer.class);
			
			private final Class<?> type;
			private Property(Class<?> t){ this.type = t; }
			public Class<?> getType(){ return type; }
		}
		
		private int centerX = Integer.MIN_VALUE, centerY = Integer.MIN_VALUE;
		private Bitmap image;
		
		@Override
		public Object get(Property property) throws IllegalArgumentException{
			if(!has(property)) throw new IllegalStateException();
			
			switch(property){
			case POSITION:	return new int[]{ centerX, centerY };
			case IMAGE:	return image;
			}
			
			throw new IllegalArgumentException();
		}
		
		@Override
		public boolean has(Property property) throws IllegalArgumentException{
			switch(property){
			case POSITION:	return centerX != Integer.MIN_VALUE || centerY != Integer.MIN_VALUE;
			case IMAGE:	return image != null;
			}
			
			throw new IllegalArgumentException();
		}
		
		@Override
		public void load(Property property, ByteBuffer data) throws IllegalArgumentException{
			
			
		}
		@Override
		public void skip(Property property, ByteBuffer buf) throws IllegalArgumentException{
			
			
		}
	}
}

*/