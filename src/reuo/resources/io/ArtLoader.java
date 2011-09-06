package reuo.resources.io;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.*;

import reuo.resources.Bitmap;
import reuo.resources.format.Formatter;
import reuo.resources.io.Preparation.None;
import reuo.util.Rect;

public class ArtLoader extends SimpleIndexedLoader<Bitmap> {//implements Preparable<StoredIndexPreparation<Preparation.None>>{
	FileChannel artSource;
	TileFormatter tileFormatter;
	ArtChannel artChannel;
	Formatter pixelFormatter;
	final ByteBuffer artHeader = ByteBuffer.allocate(8);
	final Map<Integer, SoftReference<MappedByteBuffer>> tileBlockBuffers = new HashMap<Integer, SoftReference<MappedByteBuffer>>();
	final private IndexedLoader<Entry, Bitmap> tileArtLoader = new FragmentIndexedLoader<Entry, Bitmap>(
		this,
		0,
		0x4000);
	final private IndexedLoader<Entry, Bitmap> spriteArtLoader = new FragmentIndexedLoader<Entry, Bitmap>(
		this,
		0x4000,
		0x8000);
	final private IndexedLoader<Entry, Bitmap> legacyArtLoader = new FragmentIndexedLoader<Entry, Bitmap>(
		this,
		0x8000);
	
	public ArtLoader(){
		
	}
	
	public void reset() {
		super.reset();
		artSource = null;
		tileFormatter = null;
		artChannel = null;
		pixelFormatter = null;
	}

	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel());
		
		artHeader.order(ByteOrder.LITTLE_ENDIAN);
		
		this.artSource = new FileInputStream(prep.resource).getChannel();
		this.pixelFormatter = prep.formatter;
		this.tileFormatter = new TileFormatter();// artSource);
		this.artChannel = new ArtChannel(artSource);
	}
	
	public IndexedLoader<Entry, Bitmap> getSpriteLoader(){
		return spriteArtLoader;
	}
	
	public IndexedLoader<Entry, Bitmap> getTileArtLoader(){
		return tileArtLoader;
	}
	
	public IndexedLoader<Entry, Bitmap> getLegacyArtLoader(){
		return legacyArtLoader;
	}
	
	@Override
	public Bitmap get(int id) throws IOException{
		return get(id, true);
	}
	
	public Bitmap get(int id, boolean prefetch) throws IOException{
		CacheReference ref = cache.get(id);
		Bitmap bmp;
		
		if(ref != null){
			if((bmp = ref.get()) != null){
				return(bmp);
			}
		}
		
		Entry entry = getEntry(id);
		Buffer data;
		
		if(!entry.isValid()){
			return null;
		}
		
		if(id < 0x4000){
			int base = (int)(entry.offset / (2048 * 64));
			int baseOffset = base * 2048 * 64;
			SoftReference<MappedByteBuffer> mapRef = tileBlockBuffers.get(base);
			MappedByteBuffer map = null;
			
			if(mapRef != null){
				map = mapRef.get();
			}
			
			if(map == null){
				map = artSource.map(MapMode.READ_ONLY, baseOffset, 2048 * 64);
				tileBlockBuffers.put(base, mapRef = new SoftReference<MappedByteBuffer>(map));
			}
			
			int pos = (int)(entry.offset - baseOffset);
			ByteBuffer tile;
			
			if(pos + 2048 > 2048 * 64){
				System.out.println("buf miss");
				tile = artSource.map(MapMode.READ_ONLY, entry.offset, 2048);
				tile.order(ByteOrder.LITTLE_ENDIAN);
			}else{
				map.limit(pos + 2048).position(pos);
				tile = map.slice().order(ByteOrder.LITTLE_ENDIAN);
			}
			
			ByteBuffer unconverted = tileFormatter.format(tile);
			unconverted.rewind();
			data = pixelFormatter.format(unconverted, 44, 44, null);
			
			bmp = new Bitmap(id, 44, 44, data);
		}else{
			synchronized(artSource){
				artSource.position(entry.offset);
				
				artHeader.clear();
				artSource.read(artHeader);
				artHeader.rewind();
				
				artHeader.getInt();
				int width = artHeader.getShort();
				int height = artHeader.getShort();
				
				artChannel.prepare(entry.length, width, height);
				
				ByteBuffer uncropped = ByteBuffer.allocate(width * height * 2);
				uncropped.order(ByteOrder.LITTLE_ENDIAN);
				uncropped.clear();
				artChannel.read(uncropped);
				Rect coords = artChannel.getCoordinates();
				
				if(!artChannel.isEmpty()){
					
					uncropped.clear();
					data = pixelFormatter.format(uncropped, width, height, coords);
				}else{
					data = null;
				}
				
				bmp = new Bitmap(id, coords.getWidth(), coords.getHeight(), data, artChannel
					.getInsets());
			}
		}
		
		new CacheReference(id, bmp);
		
		if(prefetch){
			int max = Math.min(getCapacity(), id + 32);
			
			for(int i = id; i < max; i++){
				get(i, false);
			}
		}
		
		return bmp;
	}
}
