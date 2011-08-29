package reuo.resources.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class SimpleIndexedLoader<R> extends StoredIndexedLoader<Entry, R>{
	protected void prepare(FileChannel entrySource) throws IOException{
		super.prepare(entrySource, 12);
	}
	
	@Override
	protected Entry getEntryFromBuffer(int id, ByteBuffer buffer){
		return new Entry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
}
