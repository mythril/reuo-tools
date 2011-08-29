package reuo.resources.io;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import reuo.resources.*;

// TODO Finish DecorationLoader implement Preparable

public class DecorationLoader extends SimpleIndexedLoader<DecorationBlock>{
	//private static final int CELL_LENGTH = 7;
	//private static final int BLOCK_LENGTH = 8 * 8 * CELL_LENGTH;
	
	FileChannel channel;
	World world;
	
	public DecorationLoader(FileChannel index, FileChannel channel, World world) throws IOException{
		prepare(index,channel,world);
	}
	
	public void prepare(FileChannel index, FileChannel channel, World world) throws IOException{
		super.prepare(index);
		
		this.channel = channel;
		this.world = world;
	}
	
	public DecorationBlock get(int blockX, int blockY) throws IOException{
		return get(blockY + blockX * world.getBlockHeight());
	}
	
	@Override
	public DecorationBlock get(int blockId) throws IOException{
		CacheReference ref = cache.get(blockId);
		DecorationBlock block;
		
		if(ref != null){
			block = ref.get();
			
			if(block != null){
				return block;
			}
		}
		
		Entry entry = getEntry(blockId);
		ByteBuffer blockData = channel.map(MapMode.READ_ONLY, entry.offset, entry.length);
		int count = entry.length / 7;
		
		blockData.rewind();
		blockData.order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0; i < count; i++){
			
		}
		
		block = null;//new DecorationCell(buf);
		new CacheReference(blockId, block);
		return block;
	}

	@Override
	public int getCapacity(){
		return world.getBlockWidth() * world.getBlockHeight();
	}
}
