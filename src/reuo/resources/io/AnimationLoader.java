package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.MapMode;

import javax.imageio.ImageIO;

import reuo.resources.Animation;
import reuo.resources.format.*;
import reuo.resources.io.Preparation.None;

public class AnimationLoader extends StoredIndexedLoader<Entry, Animation>{
	Formatter formatter;
	FileChannel animSource;
	
	public AnimationLoader() {
		
	}
	
	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel(), 12);
		this.formatter = prep.formatter;
		this.animSource = new FileInputStream(prep.resource).getChannel();
	}
	
	public void reset() {
		super.reset();
		formatter = null;
		animSource = null;
	}
	
	@Override
	public Animation get(int id) throws IOException {
		CacheReference ref = cache.get(id);
		Animation anim;
		
		if(ref != null){
			if((anim = ref.get()) != null){
				return(anim);
			}
		}
		
		Entry entry = getEntry(id);
		
		if (entry == null || !entry.isValid()) {
			return null;
		}
		
		MappedByteBuffer data = animSource.map(MapMode.READ_ONLY, entry.offset, entry.length); //0, animSource.size());
		
		anim = new Animation(id, formatter);
		anim.load(data);
		
		new CacheReference(id, anim);
		
		return anim;
	}

	@Override
	protected Entry getEntryFromBuffer(int id, ByteBuffer buffer)
			throws BufferUnderflowException {
		return new Entry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
}
