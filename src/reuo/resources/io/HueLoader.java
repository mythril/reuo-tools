package reuo.resources.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.EnumSet;

import reuo.resources.Hue;
import reuo.resources.format.Formatter;

public class HueLoader extends FixedLengthLoader<Hue> implements Iterable<Integer> {// ,Preparable<StandardPreparation<Hue.Property>>{
	FileChannel hueSource;
	Formatter formatter;
	EnumSet<Hue.Property> properties;
	long size;
	
	public HueLoader(){
		
	}
	
	public void prepare(StandardPreparation<Hue.Property> prep) throws IOException {
		this.hueSource = new FileInputStream(prep.resource).getChannel();
		this.formatter = prep.formatter;
		this.size = hueSource.size();
		this.properties = prep.properties;
	}
	
	public void reset() {
		super.reset();
		hueSource = null;
		formatter = null;
		properties = null;
		size = 0;
	}
	
	@Override
	public Hue get(int id) throws IOException{
		return get(id, properties);
	}
	
	public Hue get(int id, EnumSet<Hue.Property> properties) throws IOException{		
		CacheReference ref = cache.get(id);
		Hue hue = null;
		
		if(ref != null){
			hue = ref.get();
			
			if(hue != null && hue.hasAll(properties)){
				return hue;
			}
		}
		
		if(hue == null){
			hue = new Hue(id,formatter);
		}
		
		int required = 0;
		
		if(properties.contains(Hue.Property.NAME)){
			required += 20;
		}
		
		if(properties.contains(Hue.Property.PALETTE)){
			required += 68;
		}
		
		ByteBuffer data = hueSource.map(MapMode.READ_ONLY, getOffset(id), required);
		data.rewind();
		
		for(Hue.Property p : EnumSet.allOf(Hue.Property.class)){
			if(properties.contains(p) && !hue.has(p)){
				hue.load(p, data);
			}else{
				hue.skip(p, data);
			}
		}
		
		if(ref == null){
			ref = new CacheReference(id, hue);
		}
		
		return hue;
	}
	
	@Override
	public int getCapacity() {
		return (int) ((size / ((88*8) + 4)) * 8);
	}
	
	protected static long getOffset(int id){
		int group = (id / 8);
		int groupOffset = ((group * 88) * 8) + ((group + 1) * 4);
		int local = id % 8;
		int localOffset = local * 88;
		int offset = groupOffset + localOffset;
		return offset;
	}
}
