package reuo.resources.io;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.FileChannel.MapMode;

/**
 * Decodes Run Length Encoding (RLE) from the underlying channel. Due to legacy
 * format each run entry is 4 bytes, with 2 bytes for the run-length and 2 bytes
 * for the 16-bit pixel. Because runs would never approach 2<span
 * style="vertical-align:super;font-size:smaller">16</span> this effectively
 * wastes 8-bits per entry.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class DecodedChannel implements ReadableByteChannel{
	private final FileChannel in;
	private ByteBuffer entries;
	private int run = 0;
	private short value = 0;
	private long size;
	
	/**
	 * Wraps the input channel as the underlying source for the encoded data.
	 * 
	 * @param in the channel for encoded data
	 * @throws IOException if the <code>size</code> of the encoded channel
	 *             cannot be determined
	 */
	public DecodedChannel(FileChannel in) throws IOException{
		this.in = in;
		this.size = in.size();
	}
	
	/**
	 * Prepares the decoder for new input by clearing the state of any remaining
	 * pixels.
	 * 
	 * @param length the new length of the input
	 * @throws IOException if the underlying channel cannot be prepared
	 */
	public void prepare(int length) throws IOException{
		this.run = 0;
		long pos = in.position();
		
		if(pos + length >= size){
			length -= (pos + length) - size;
		}
		
		entries = in.map(MapMode.READ_ONLY, pos, length);
		entries.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public int read(ByteBuffer dst) throws IOException{
		int bytes = dst.remaining();
		
		while(run > 0 && dst.remaining() >= 2){
			dst.putShort(value);
			run--;
		}
		
		while(entries.remaining() >= 4 && dst.remaining() >= 2){
			value = entries.getShort();
			value = (short)(value | (value == 0 ? 0 : 1) << 15);
			run = entries.getShort() & 0xffff;
			
			while(run > 0 && dst.remaining() >= 2){
				dst.putShort(value);
				run--;
			}
		}
		
		return bytes;
	}
	
	public void close() throws IOException{
		in.close();
	}
	
	public boolean isOpen(){
		return in.isOpen();
	}
}
