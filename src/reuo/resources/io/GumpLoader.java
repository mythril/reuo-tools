package reuo.resources.io;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import reuo.resources.Bitmap;
import reuo.resources.format.Formatter;
import reuo.resources.io.Preparation.None;
import reuo.util.Rect;

public class GumpLoader extends StoredIndexedLoader<GumpEntry, Bitmap> implements Preparable<StoredIndexPreparation<Preparation.None>>{
	FileChannel artSource;
	Formatter formatter;
	DecodedChannel decoder;
	AutoCropChannel cropper;
	
	public GumpLoader(){
		
	}
	
	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel(), 12);
		this.formatter = prep.formatter;
		this.artSource = new FileInputStream(prep.resource).getChannel();
		this.decoder = new DecodedChannel(artSource);
		this.cropper = new AutoCropChannel(decoder);
	}

	public void reset() {
		super.reset();
		formatter = null;
		artSource = null;
		decoder = null;
		cropper = null;
	}

	@Override
	protected GumpEntry getEntryFromBuffer(int id, ByteBuffer buffer){
		return new GumpEntry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
	@Override
	synchronized public Bitmap get(int id) throws IOException{
		CacheReference ref = cache.get(id);
		Bitmap bitMap;
		
		if(ref != null){
			if((bitMap = ref.get()) != null){
				return bitMap;
			}
		}
		
		// Get the entry; if it's already loaded return the reference
		GumpEntry entry = getEntry(id);
		
		if(!entry.isValid()){
			return null;
		}
		
		artSource.position(entry.offset);
		artSource.position(artSource.position() + entry.getHeight() * 4);
		
		decoder.prepare(entry.length);
		cropper.prepare(entry.getWidth(), entry.getHeight());
		
		ByteBuffer uncropped = ByteBuffer.allocate(entry.getWidth() * entry.getHeight() * 2);
		uncropped.order(ByteOrder.LITTLE_ENDIAN);
		
		uncropped.clear();
		cropper.read(uncropped);
		uncropped.rewind();
		Rect coords = cropper.getCoordinates();
		
		Buffer data = null;
		
		if(!cropper.isEmpty()){
			data = formatter.format(uncropped, entry.getWidth(), entry.getHeight(), coords);
		}
		
		bitMap = new Bitmap(id, coords.getWidth(), coords.getHeight(), data, cropper.getInsets());
		ref = new CacheReference(id, bitMap);
		return bitMap;
	}

}
