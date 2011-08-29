package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.SpriteData;
import reuo.resources.io.Preparation.None;

public class SpriteDataLoader extends Loader<SpriteData> implements Preparable<StandardPreparation<Preparation.None>>{
	FileChannel dataSource;
	int size;
	
	public SpriteDataLoader(){
		
	}
	
	public void prepare(StandardPreparation<None> prep) throws IOException {
		this.dataSource = new FileInputStream(prep.resource).getChannel();
		size = (int)dataSource.size();
	}

	public void reset() {
		super.reset();
		dataSource = null;
		size = 0;
	}
	
	@Override
	public SpriteData get(int id) throws IOException{
		CacheReference ref = cache.get(id);
		SpriteData data = null;
		
		if(ref != null){
			if((data = ref.get()) != null){
				return(data);
			}
		}
		
		ByteBuffer dataBuffer = ByteBuffer.allocate(37);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		if(getOffset(id) > dataSource.size()){
			return null;
		}
		
		dataSource.position(getOffset(id));
		
		dataBuffer.clear();
		dataSource.read(dataBuffer);
		dataBuffer.rewind();
		
		data = new SpriteData();
		data.load(dataBuffer);
		
		new CacheReference(id, data);
		return data;
	}
	
	private static long getOffset(int id){
		return (id * 37) + (((id / 32) + 1) * 4) + TILE_SECTION_LENGTH;
	}
	
	@Override
	public int getCapacity(){
		return (int)(size - TILE_SECTION_LENGTH) / 37;
	}
	
	private static final int TILE_GROUPS = 512;
	private static final int TILE_GROUP_ENTRIES = 32;
	private static final int TILE_ENTRY_SIZE = 26;
	private static final int TILE_SECTION_LENGTH = TILE_GROUPS * TILE_GROUP_ENTRIES * TILE_ENTRY_SIZE + TILE_GROUPS * 4;

	/*public static void main(String[] args) throws FileNotFoundException, IOException {
		SpriteDataLoader sdl = new SpriteDataLoader(new FileInputStream(new File(args[0])).getChannel());
		SpriteData sd = sdl.get(2470);
		System.out.println(sd.height);
		System.out.println(sd.name);
	}*/
}
