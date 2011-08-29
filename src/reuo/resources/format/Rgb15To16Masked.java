package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;

/**
 * Formats pixels from 15-bit color with 5-bits per component to 16-bit color
 * with 5-bits per component and 1-bit for alpha. The alpha bit of the source
 * will always be discarded and instead the pixel is compared to the color key.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Rgb15To16Masked implements Formatter{
	final private static Formatter formatter = new Rgb15To16Masked();
	
	public static Formatter getFormatter(){
		return(formatter);
	}
	
	private Rgb15To16Masked(){
		read.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public Buffer format(ByteBuffer in, int width, int height, Rect crop){
		int croppedWidth, croppedHeight;
		
		if(crop != null){
			croppedWidth = crop.getWidth();
			croppedHeight = crop.getHeight();
		}else{
			crop = new Rect(0, 0, croppedHeight = height, croppedWidth = width);
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
				if(pixel != 0){
					pixel = pixel | (1 << 15);
				}
				out.put((short)pixel);
			}
			
			if(out.hasRemaining() && stride > 0){
				pos = in.position() + stride;
				in.limit(pos + scanline);
				in.position(pos);
			}
		}
		
		return out;
	}
	
	final private ByteBuffer read = ByteBuffer.allocate(2 * 64 * 64);
	
	public ShortBuffer format(ReadableByteChannel source, int width, int height) throws IOException{
		int pixels = width * height;
		int length = pixels * 2;
		
		int rgb;
		ShortBuffer converted = ShortBuffer.allocate(pixels);
		
		read.clear();
		read.flip();
		
		while(length > 0){
			if(read.remaining() < 2){
				read.compact().limit(Math.min(read.capacity(), length));
				source.read(read);
				read.rewind();
				
				if(read.remaining() < 2)
					break;
			}
			
			rgb = read.getShort() & 0xffff;
			rgb |= (rgb == 0 ? 0 : 1) << 15;
			
			converted.put((short)rgb);
			length -= 2;
		}
		
		return(converted);
	}
}
