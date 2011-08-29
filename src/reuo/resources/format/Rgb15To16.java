package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;


public class Rgb15To16 implements Formatter{
	final private static Rgb15To16 formatter = new Rgb15To16();
	final ByteBuffer read = ByteBuffer.allocate(2 * 64 * 64);
	
	public static Rgb15To16 getFormatter(){
		return formatter;
	}
	
	private Rgb15To16(){
		read.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public Buffer format(
		ByteBuffer in,
		int width,
		int height,
		Rect crop
	){
		int croppedWidth, croppedHeight;
		
		if(crop != null){
			croppedWidth = crop.getWidth();
			croppedHeight = crop.getHeight();
		}else{
			crop = new Rect(0, 0, croppedWidth = width, croppedHeight = height);
		}
		
		ShortBuffer out = ShortBuffer.allocate(croppedWidth * croppedHeight);
		out.clear();
		
		int scanline = croppedWidth * 2;
		int stride = (width - croppedWidth) * 2;
		int pos = in.position() + (crop.left + crop.top * width) * 2;
		
		if(stride > 0){
			in.limit(pos + scanline);
		}else{
			in.limit(pos + scanline * croppedHeight);
		}
		
		in.position(pos);
		
		int pixel;
		while(out.hasRemaining()){				
			while(in.hasRemaining()){
				pixel = in.getShort();
				pixel = (1<<15) | pixel;
				out.put((short) pixel);
			}
			
			if(out.hasRemaining() && stride > 0){
				pos = in.position() + stride;
				in.limit(pos + scanline);
				in.position(pos);
			}
		}
		
		return out;
	}
	
	public ShortBuffer format(
			ReadableByteChannel src,
			int width,
			int height
	) throws IOException {
		int pixels = width * height;
		int length = pixels * 2;
		
		ShortBuffer out = ShortBuffer.allocate(pixels);
		read.clear();
		read.flip();
		
		while(out.hasRemaining() && pixels-- > 0){
			if(read.remaining() < 2){
				read.clear();
				read.limit(Math.min(read.capacity(), length));
				if(read.remaining() < 2) break;
				
				src.read(read);
				read.rewind();
			}
			
			out.put(read.getShort());
		}
		
		return out;
	}
}
