package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import reuo.resources.Font;
import reuo.resources.format.Formatter;

public class FontLoader extends MemoryIndexedLoader<FontEntry, Font> { //implements Preparable<StandardPreparation<?>>{
	FileChannel fontSource;
	Formatter formatter;
	
	public FontLoader(){
		
	}
	
	public void prepare(StandardPreparation<?> prep) throws IOException {
		fontSource = new FileInputStream(prep.resource).getChannel();
		formatter = prep.formatter;
		generateIndex();
	}
	
	public void reset() {
		super.reset();
		fontSource = null;
		formatter = null;
	}

	@Override
	protected void generateIndex() throws IOException{
		int extra;
		long offset;
		ByteBuffer fontBuffer = ByteBuffer.allocate(1);
		ByteBuffer glyphBuffer = ByteBuffer.allocate(3);
		fontBuffer.order(ByteOrder.LITTLE_ENDIAN);
		glyphBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int id = 0;
		
		while( fontSource.position() <= fontSource.size()){
			fontBuffer.clear();
			fontSource.read(fontBuffer);
			fontBuffer.rewind();
			
			extra = fontBuffer.get() & 0xFF;
			int imageSize;
			int width, height;
			offset = fontSource.position();
			
			for(int i = 0; i < 224; i++) {
				glyphBuffer.clear();
				fontSource.read(glyphBuffer);
				glyphBuffer.rewind();
				
				width = glyphBuffer.get() & 0xFF;
				height = glyphBuffer.get() & 0xFF;
				
				imageSize = width * height * 2;
				glyphBuffer.get();
				
				fontSource.position(fontSource.position() + imageSize);
			}
			
			FontEntry entry = new FontEntry(
				id,
				offset,
				(int)(fontSource.position() - offset),
				extra);

			if(entry.isValid()){
				entries.put(id, entry);
				id++;
			}
		}
	}

	@Override
	public Font get(int id) throws IOException{
		FontEntry entry = getEntry(id);
		
		if(!entry.isValid()){
			return(null);
		}
		
		CacheReference ref = cache.get(id);
		Font font = null;
		
		if(ref != null){
			font = ref.get();
		}
		
		if(font == null){
			font = new Font(id,formatter);
		}else{
			return font;
		}
		
		ByteBuffer data = fontSource.map(MapMode.READ_ONLY, entry.offset, entry.length);
		data.order(ByteOrder.LITTLE_ENDIAN);
		data.rewind();
		
		font.load(data);
		
		if(ref == null){
			ref = new CacheReference(id, font);
		}
		
		return font;
	}

}
