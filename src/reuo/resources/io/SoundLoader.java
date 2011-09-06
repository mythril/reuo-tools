package reuo.resources.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.EnumSet;

import reuo.resources.Sound;
import reuo.resources.Sound.Property;

public class SoundLoader extends SimpleIndexedLoader<Sound> {// implements Preparable<StoredIndexPreparation<Sound.Property>>{
	FileChannel src;
	EnumSet<Sound.Property> properties;
	
	public SoundLoader(){
		
	}
	
	public void prepare(StoredIndexPreparation<Property> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel());
		this.properties = prep.properties;
		this.src = new FileInputStream(prep.resource).getChannel();
	}

	public void reset() {
		super.reset();
		src = null;
		properties = null;
	}
	
	@Override
	public Sound get(int id) throws IOException{
		return get(id, properties);
	}
	
	synchronized public Sound get(int id, EnumSet<Sound.Property> properties) throws IOException{
		CacheReference ref = cache.get(id);
		Sound snd = null;
		
		if(ref != null){
			snd = ref.get();
			
			if(snd != null && snd.hasAll(properties)){
				return snd;
			}
		}
		
		Entry entry = getEntry(id);
		
		if(!entry.isValid()){
			return null;
		}
		
		if(snd == null){
			snd = new Sound(id);
		}
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		int[] pos = new int[properties.size()];
		int[] length = new int[properties.size()];
		int i = 0;
		
		for(Sound.Property p : properties){
			if(!snd.has(p)){
				pos[i] = snd.locate(p, entry);
				length[i] = snd.measure(p, entry);
				
				min = Math.min(min, pos[i]);
				max = Math.max(max, pos[i] + length[i]);
				i++;
			}
		}
		
		ByteBuffer buffer;
		
		if(max - min >= (1024 * 1024)){
			buffer = src.map(MapMode.READ_ONLY, entry.offset + min, max - min);
		}else{
			buffer = ByteBuffer.allocate(max - min);
			src.read(buffer, entry.offset + min);
		}
		
		i = 0;
		
		for(Sound.Property p : properties){
			if(!snd.has(p)){
				buffer.limit((pos[i] - min) + length[i]).position(pos[i]);
				
				snd.load(p, buffer);
				i++;
			}
		}
		
		/*
		int offset = -1;
		int required = 0;
		
		if(properties.contains(Sound.Property.NAME)){
			offset = 0;
			required += 20;
		}
		
		if(properties.contains(Sound.Property.DATA)){
			if(offset < 0){
				offset = 20;
			}
			
			required = entry.length;
		}
		
		ByteBuffer data;
		
		data = wavSource.map(MapMode.READ_ONLY, entry.offset, required);
		
		data.order(ByteOrder.LITTLE_ENDIAN);
		data.rewind();
		
		for(Sound.Property p : EnumSet.allOf(Sound.Property.class)){
			if(properties.contains(p) && !snd.has(p)){
				snd.load(p, data);
			}else{
				snd.skip(p, data);
			}
		}
		*/

		if(ref == null){
			ref = new CacheReference(id, snd);
		}
		
		return snd;
	}
}
