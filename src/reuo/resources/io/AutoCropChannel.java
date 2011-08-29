package reuo.resources.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import reuo.resources.*;
import reuo.util.Rect;

/**
 * A pass-through {@link java.nio.channels.Channel} that records cropping
 * information about the image as it's being read, treating data as 16-bit
 * pixels with the high-bit being the alpha bit. For example:
 * 
 * <pre>
 * AutoCropChannel cropper = new AutoCropChannel(dataSource);
 * int w = 100, h = 100;
 * ByteBuffer image = ByteBuffer.allocate(w * h * 2);
 * cropper.prepare(w, h);
 * cropper.read(image);
 * System.out.println(cropper.getCoordinates());
 * </pre>
 * 
 * will prepare the cropper and read the entire image and print the coordinates
 * of the cropped portion of the image.
 * 
 * @author Kristopher Ives
 */
public class AutoCropChannel extends Cropper implements ReadableByteChannel{
	private static final int ALPHA_BIT = 1 << 15;
	
	final private ReadableByteChannel in;
	private int x, y;
	private int width, height;
	private int offset = 0;
	
	/**
	 * Initializes the cropper using the underlying input channel.
	 * 
	 * @param in the underlying input channel
	 */
	public AutoCropChannel(ReadableByteChannel in){
		this.in = in;
	}
	
	/**
	 * Prepares the cropper for new input by discarding the previous cropping
	 * coordinates.
	 * 
	 * @param width the uncropped image width
	 * @param height the uncropped image height
	 */
	public void prepare(int width, int height){
		x = y = 0;
		offset = 0;
		
		this.width = width;
		this.height = height;
		resetCropping();
	}
	
	public int read(ByteBuffer dst) throws IOException{
		int rgb;
		int count = in.read(dst);
		dst.rewind();
		
		while(dst.hasRemaining()){
			rgb = dst.getShort() & 0xffff;
			
			if((rgb & ALPHA_BIT) == ALPHA_BIT){
				x = offset % width;
				y = offset / width;
				
				crop.left = Math.min(crop.left, x);
				crop.right = Math.max(crop.right, x);
				crop.top = Math.min(crop.top, y);
				crop.bottom = Math.max(crop.bottom, y);
			}
			
			offset++;
		}
		
		return count;
	}
	
	public Rect getInsets(){
		crop.right = width - crop.right;
		crop.bottom = height - crop.bottom;
		
		return super.getInsets();
	}
	
	public void close() throws IOException{
		in.close();
	}
	
	public boolean isOpen(){
		return in.isOpen();
	}
}
