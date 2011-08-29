package reuo.resources;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import reuo.resources.format.DataMetrics;
import reuo.resources.format.Utilities;
import reuo.resources.io.Entry;

/**
 * A resource that has the waveform needed to play the audio. This is stored as
 * 22Khz 16-bit Mono in a Buffer.
 * 
 * @author Kristopher Ives
 */
public class Sound extends Resource<Sound.Property>{
	public enum Property implements Resource.Property{
		NAME(String.class),
		HEADER(byte[].class),
		DATA(ByteBuffer.class);
		
		final private Class<?> type;
		
		private Property(Class<?> t){
			this.type = t;
		}
		
		public Class<?> getType(){
			return type;
		}
	}
	
	protected String name = null;
	protected ByteBuffer header = null;
	protected ByteBuffer data = null;
	
	public Sound(int id){
		super(id);
	}
	/*
	public EnumMap<Property, Range> map(EnumSet<Property> properties, int length){
		EnumMap<Property, Range> maps = new EnumMap<Property, Range>(Property.class);
		
		for(Property p : properties){
			Range range;
			
			switch(p){
			case NAME:
				range = new Range(0, 20);
				break;
			case HEADER:
				range = new Range(20, 40);
				break;
			case DATA:
				range = new Range(40, length);
				break;
			default:
				throw new IllegalArgumentException();
			}
			
			maps.put(p, range);
		}
		
		return maps;
	}
	*/
	
	public int locate(Property property, Entry entry){
		switch(property){
		case NAME:
			return 0;
		case HEADER:
			return 20;
		case DATA:
			return 40;
		}
		
		throw new IllegalArgumentException();
	}
	
	public int measure(Property property, Entry entry){
		switch(property){
		case NAME:
			return 20;
		case HEADER:
			return 20;
		case DATA:
			return entry.length - 40;
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Gets the name of this sound.
	 * 
	 * @see Property.#NAME
	 * @return the name
	 */
	public String getName(){
		return(name);
	}
	
	public ByteBuffer getData(){
		return(data);
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	@Override
	public void skip(Property property, ByteBuffer in){
		switch(property){
		case NAME:
			in.position(in.position() + 20);
			break;
		case DATA:
			// in.position(in.position() + entry.getLength() - 20);
			in.position(in.limit());
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void load(Property property, ByteBuffer in){
		switch(property){
		case NAME:
			in.order(ByteOrder.LITTLE_ENDIAN);
			
			try{
				DataMetrics metrics = Utilities.readArray(in, 20);
				
				name = new String(metrics.array, metrics.pos, metrics.length, "ASCII");
				int offset = name.indexOf('\0');
				
				if(offset > 0){
					name = name.substring(0, offset);
				}
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
			
			break;
		case HEADER:
			header = in.duplicate();
			header.limit(header.position() + 20);
			header = header.slice();
			break;
		case DATA:
			data = in.slice().order(ByteOrder.LITTLE_ENDIAN);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public boolean has(Property prop){
		switch(prop){
		case NAME:
			return name != null;
		case DATA:
			return data != null;
		}
		
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object get(Property p){
		if(!has(p))
			throw new IllegalStateException();
		
		switch(p){
		case NAME:
			return name;
		case DATA:
			return data;
		}
		
		throw new IllegalArgumentException();
	}
}
