package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;

public class Rgb15To16Flipped implements Formatter{
	private static Formatter formatter = new Rgb15To16Flipped();
	ByteBuffer read = ByteBuffer.allocate(2 * 64);
	

	
	public static Formatter getFormatter(){
		return(formatter);
	}
	
	private Rgb15To16Flipped(){
		read.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public Buffer format(ByteBuffer from, int x, int y, int width, int height){
		return null;
	}
	
	public ShortBuffer format(ReadableByteChannel source, int width, int height)
			throws IOException{
		int pixels = width * height;
		int length = pixels * 2;
		
		ShortBuffer converted = ShortBuffer.allocate(pixels);
		read.clear();
		read.flip();
		
		while(converted.hasRemaining()){
			if(read.remaining() < 2){
				read.clear();
				read.limit(Math.min(read.capacity(), length));
				source.read(read);
				read.rewind();
				
				if(read.remaining() < 2)
					break;
			}
			
			converted.put(read.getShort());
		}
		
		return(converted);
	}

	public Buffer format(ByteBuffer from, int width, int height, Rect crop){
		// TODO Do we need flipped loaders?
		return null;
	}
}
