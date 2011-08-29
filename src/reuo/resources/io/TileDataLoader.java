package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.TileData;
import reuo.resources.io.Preparation.None;

public class TileDataLoader extends Loader<TileData> implements Preparable<StandardPreparation<Preparation.None>>{
	FileChannel dataSource;
	int size;

	public TileDataLoader(){
		
	}
	
	public void prepare(StandardPreparation<None> prep) throws IOException {
		this.dataSource = new FileInputStream(prep.resource).getChannel();
		size = (int) dataSource.size();
	}

	public void reset() {
		super.reset();
		size = 0;
	}
	
	@Override
	public TileData get(int id) throws IOException {
		CacheReference ref = cache.get(id);
		TileData data = null;

		if (ref != null) {
			if ((data = ref.get()) != null) {
				return (data);
			}
		}

		ByteBuffer dataBuffer = ByteBuffer.allocate(26);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);

		if (getOffset(id) > dataSource.size()) {
			return null;
		}

		dataSource.position(getOffset(id));

		dataBuffer.clear();
		dataSource.read(dataBuffer);
		dataBuffer.rewind();

		data = new TileData(dataBuffer);

		new CacheReference(id, data);
		return data;
	}

	private static long getOffset(int id) {
		return (id * 26) + (((id / 32) + 1) * 4);
	}

	@Override
	public int getCapacity() {
		return (512*32);
	}
}
