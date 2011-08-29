package reuo.resources;

import java.io.UnsupportedEncodingException;
import java.nio.*;

import reuo.resources.format.*;
import reuo.resources.io.Entry;

public class Hue extends Resource<Hue.Property>{
	public enum Property implements Resource.Property{
		PALETTE(HuePalette.class),
		NAME(String.class);
		
		final private Class<?> type;
		private Property(Class<?> t){ this.type = t; }
		public Class<?> getType(){ return type; }
	}
	
	//private int id = -1;
	private HuePalette palette = null;
	private String name = null;
	private Formatter formatter;
	
	public Hue(int id, Formatter formatter) {
		super(id);
		this.formatter = formatter;
	}
	
	public String getName(){
		return(name);
	}

	public Palette getPalette(){
		return palette;
	}
	
	public int locate(Property property, Entry entry){
		return 0;
	}
	
	public int measure(Property property, Entry entry){
		return 0;
	}
	
	@Override
	public Object get(Property property) throws IllegalArgumentException {
		if(!has(property)){
			throw new IllegalStateException(
					"Property '"+property+"' has not been loaded.");
		}
		
		switch (property) {
		case NAME:
			return name;
		case PALETTE:
			return palette;
		}
		
		throw new IllegalArgumentException("" +
				"Property '"+property+"' is not a valid property.");
	}

	@Override
	public boolean has(Property property) throws IllegalArgumentException {
		switch (property) {
		case NAME:
			return (name != null);
		case PALETTE:
			return (palette != null);
		}
		
		throw new IllegalArgumentException("" +
				"Property '"+property+"' is not a valid property.");
	}

	@Override
	public void load(Property property, ByteBuffer in) 
			throws IllegalArgumentException{
		switch (property) {
		case NAME:
			DataMetrics metrics = Utilities.readArray(in, 20);
			
			try {
				name = new String(metrics.array,metrics.pos,metrics.length, "ascii");
			} catch (UnsupportedEncodingException e) {
				name = null;
			}
			
			final int i = name.indexOf('\0');
			if(i >= 0){
				name = name.substring(0, i);
			}
			return;
		case PALETTE:
			palette = new HuePalette(null, formatter);
			palette.load(in);
			return;
		}
		
		throw new IllegalArgumentException("" +
				"Property '"+property+"' is not a valid property.");
	}

	@Override
	public void skip(Property property, ByteBuffer in)
			throws IllegalArgumentException {
		switch (property) {
		case NAME:
			in.position(in.position()+20);
			return;
		case PALETTE:
			in.position(in.position()+68);
			return;
		}
		
		throw new IllegalArgumentException("" +
				"Property '"+property+"' is not a valid property.");
	}
	
	public class HuePalette extends Palette{
		private int replaceStart;
		private int replaceEnd;
		
		public HuePalette(Buffer data, Formatter formatter) {
			super(data, 32, formatter);
		}
		
		public void load(ByteBuffer in) throws IllegalArgumentException{
			super.load(in);
			replaceStart = in.getShort();
			replaceEnd = in.getShort();
		}
		
		public int getReplaceStart(){
			return replaceStart;
		}

		public int getReplaceEnd(){
			return replaceEnd;
		}
	}
}
