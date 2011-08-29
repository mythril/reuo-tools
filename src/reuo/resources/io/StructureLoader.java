package reuo.resources.io;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import reuo.resources.*;
//TODO StructureLoader implement Preparable
public class StructureLoader extends SimpleIndexedLoader<Multi>{
	FileChannel multiSource;
	
	public StructureLoader(FileChannel idxSource, FileChannel entrySource, 
		SpriteDataLoader spriteDataLoader) throws IOException{
		prepare(idxSource, entrySource);
	}
	
	public void prepare(
		FileChannel idxSource,
		FileChannel multiSource) throws IOException{
		super.prepare(idxSource);
		
		this.multiSource = multiSource;
	}
	
	@Override
	synchronized public Multi get(int id) throws IOException{
		CacheReference ref = cache.get(id);
		Multi multi;
		
		if(ref != null){
			multi = ref.get();
			
			if(multi != null){
				return multi;
			}
		}
		
		Entry entry = getEntry(id);
		
		if(entry == null || !entry.isValid()){
			return null;
		}
		
		ByteBuffer data = multiSource.map(MapMode.READ_ONLY, entry.offset, entry.length);
		data.order(ByteOrder.LITTLE_ENDIAN);
		data.clear();
		
		multi = new Multi(id);
		multi.load(data);
		/*
		int size = entry.length / 12;
		Structure.Cell[] cells = new Structure.Cell[size];
		
		for(int i=0; i < cells.length; i++){
			int spriteId = data.getShort();
			SpriteData spriteData = spriteDataLoader.get(spriteId);
			
			cells[i] = new Structure.Cell(
				spriteId,
				data.getShort(),
				data.getShort(),
				data.getShort(),
				data.getInt(),
				spriteData.height
			);
		}
		
		multi = new Structure(id, cells);
		*/
		
		ref = new CacheReference(id, multi);
		return multi;
	}
}
