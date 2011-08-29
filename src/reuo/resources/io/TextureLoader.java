package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import reuo.resources.Bitmap;
import reuo.resources.format.Formatter;
import reuo.resources.io.Preparation.None;

/**
 * Loads texture data from TEXMAPS.MUL. The TEXIDX.MUL file should be used as
 * the index. Each loaded texture is a ByteBuffer that contains the 16-bit
 * texture data. Each texture has TextureLoader.Entry instance associated with
 * it that describes any meta-information about the texture.
 */
public class TextureLoader extends StoredIndexedLoader<TextureEntry, Bitmap> implements Preparable<StoredIndexPreparation<Preparation.None>>{
	FileChannel texSource;
	Formatter formatter;
	
	public TextureLoader(){
		
	}
	
	public void reset() {
		super.reset();
		texSource = null;
		formatter = null;
	}

	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel(), 12);
		
		this.formatter = prep.formatter;
		this.texSource = new FileInputStream(prep.resource).getChannel();
	}
	/*
	 * Initializes a TextureLoader for a data source using an index.
	 * 
	 * @param idxSource the entry index
	 * @param texSource the texture data source
	 */
	/*public TextureLoader(FileChannel idxSource, FileChannel texSource, Formatter formatter)
		throws IOException{
		prepare(idxSource, texSource, formatter);
	}
	
	public void prepare(FileChannel idxSource, FileChannel texSource, Formatter formatter) throws IOException{
		super.prepare(idxSource, 12);
		
		this.formatter = formatter;// Rgb15To16.getFormatter();
		this.texSource = texSource;
	}*/
	
	@Override
	protected TextureEntry getEntryFromBuffer(int id, ByteBuffer buffer){
		return new TextureEntry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
	@Override
	public Bitmap get(int id) throws IOException{
		return(get(id, true));
	}
	
	synchronized public Bitmap get(int id, boolean prefetch) throws IOException{
		CacheReference ref = cache.get(id);
		Bitmap bitMap;
		
		if(ref != null){
			bitMap = ref.get();
			
			if(bitMap != null){
				return(bitMap);
			}
		}
		
		TextureEntry entry = getEntry(id);
		
		if(!entry.isValid()){
			return(null);
		}
		
		texSource.position(entry.offset);
		// Buffer data = formatter.format(texSource, entry.getWidth(),
		// entry.getHeight());
		
		ByteBuffer mappedBytes = texSource.map(MapMode.READ_ONLY, entry.offset, entry.length);
		mappedBytes.rewind();
		mappedBytes.order(ByteOrder.LITTLE_ENDIAN);
		Buffer data = formatter.format(mappedBytes, entry.getWidth(), entry.getHeight(), null);
		
		bitMap = new Bitmap(id, entry.getWidth(), entry.getHeight(), data);
		ref = new CacheReference(id, bitMap);
		
		if(prefetch){
			int max = Math.min(getCapacity(), id + 10);
			
			for(int i = id - 10; i >= 0 && i < max; i++){
				get(i, false);
			}
		}
		
		return(bitMap);
	}
}