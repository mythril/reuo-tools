package reuo.resources.io;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.LightMap;
import reuo.resources.format.Formatter;
//TODO bring LightLoader up to date, implement Preparable
public class LightLoader extends StoredIndexedLoader<LightEntry, LightMap>{
	FileChannel lightSource;
	Formatter formatter;
	
	public LightLoader(FileChannel idxSource, FileChannel lightSource, Formatter formatter)
		throws IOException{
		prepare(idxSource, lightSource, formatter);
	}
	
	public void prepare(FileChannel idxSource, FileChannel lightSource, Formatter formatter) throws IOException{
		super.prepare(idxSource, 12);
		
		this.formatter = formatter;
		this.lightSource = lightSource;
	}
	
	@Override
	protected LightEntry getEntryFromBuffer(int id, ByteBuffer buffer){
		return new LightEntry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
	@Override
	public LightMap get(int id) throws IOException{
		CacheReference ref = cache.get(id);
		LightMap lmp;
		
		if(ref != null){
			if((lmp = ref.get()) != null){
				return lmp;
			}
		}
		
		LightEntry entry = getEntry(id);
		
		if(!entry.isValid()){
			return null;
		}
		
		lightSource.position(entry.offset);
		Buffer lightBuffer;
		// lightBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		/*lightBuffer.clear();
		lightSource.read(lightBuffer);
		lightBuffer.rewind();*/

		lightBuffer = formatter.format(lightSource, entry.getWidth(), entry.getHeight());
		
		lmp = new LightMap(id, entry.getWidth(), entry.getHeight(), lightBuffer);
		
		ref = new CacheReference(id, lmp);
		
		return lmp;
	}
}
