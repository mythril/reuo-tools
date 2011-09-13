package reuo.resources.io;

import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.MapMode;
import java.util.Iterator;

import javax.imageio.ImageIO;

import reuo.resources.Animation;
import reuo.resources.format.*;
import reuo.resources.io.Preparation.None;

public class AnimationLoader extends StoredIndexedLoader<Entry, Animation>{
	Formatter formatter;
	FileChannel animSource;
	
	public AnimationLoader() {
		
	}
	
	public void prepare(StoredIndexPreparation<None> prep) throws IOException {
		super.prepare(new FileInputStream(prep.index).getChannel(), 12);
		this.formatter = prep.formatter;
		this.animSource = new FileInputStream(prep.resource).getChannel();
	}
	
	public void reset() {
		super.reset();
		formatter = null;
		animSource = null;
	}
	
	@Override
	public Animation get(int id) throws IOException {
		CacheReference ref = cache.get(id);
		Animation anim;
		
		if(ref != null){
			if((anim = ref.get()) != null){
				return(anim);
			}
		}
		
		Entry entry = getEntry(id);
		
		if (entry == null || !entry.isValid()) {
			return null;
		}
		
		MappedByteBuffer data = animSource.map(MapMode.READ_ONLY, entry.offset, entry.length); //0, animSource.size());
		
		anim = new Animation(id, formatter);
		anim.load(data);
		
		new CacheReference(id, anim);
		
		return anim;
	}

	@Override
	protected Entry getEntryFromBuffer(int id, ByteBuffer buffer)
			throws BufferUnderflowException {
		return new Entry(id, buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
	
	public static void main(String[] args) throws IOException{
		AnimationLoader loader = new AnimationLoader();
		Formatter frmtr = Rgb15To16.getFormatter();
		File dir = new  File("C:\\Program Files (x86)\\EA Games\\Ultima Online Mondain's Legacy");
		loader.prepare(
				new StoredIndexPreparation<Preparation.None>(
						new File(dir, "anim.idx"),
						new File(dir, "anim.mul"),
						frmtr,
						null
				)
		);
		
		Animation anim = loader.get(110);
		
		int i = 0;
		
		for (Animation.Frame frame: anim.getFrames()) {
			File writable = new File("test" + i + ".png");
			File palFile = new File("pal.png");
			
			ImageIO.write(Utilities.getImage(frame, 1), "png", writable);
			ImageIO.write(Utilities.getImage(
					Utilities.paletteToBitmap(frame.getPalette()), 1),
					"png", palFile);
			i += 1;
		}
		
	}
}
