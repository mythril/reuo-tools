package reuo.resources.format;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

import reuo.util.Rect;

/**
 * A pixel formatter. Both the input and output format are dependent on the
 * implementing class. For example, {@link Rgb15To16Masked} formats pixels from 15-bit
 * color without alpha to 16-bit pixels with alpha using a color key.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public interface Formatter{
	/**
	 * Formats pixels into a <code>Buffer</code> from the provided
	 * <code>ByteBuffer</code>. The resulting buffer will represent the
	 * formatted image within the cropping coordinates <code>coords</code>;
	 * or if <code>coords</code> was <code>null</code> the entire source
	 * image with the dimensions <code>width</code> and <code>height</code>.
	 * <p>
	 * The <code>position</code> of the <code>src</code> <code>Buffer</code>
	 * should be respected as well as the limit. The <code>limit</code> of the
	 * returned <code>Buffer</code> should also be respected, and the
	 * <code>position</code> will be undefined.
	 * <p>
	 * The {@link java.nio.ByteOrder} of <code>src</code> should be respected
	 * and used for the resulting <code>Buffer</code>
	 * @param src the data for the source image
	 * @param width the total width of <code>src</code>
	 * @param height the total height of <code>src</code>
	 * @param coords the coordinates of the image; or <code>null</code> if no
	 *            cropping occurs
	 * @return the formatted pixels
	 * @throws BufferUnderflowException if there was not enough data in
	 *             <code>src</code>
	 */
	public Buffer format(ByteBuffer src, int width, int height, Rect coords) throws BufferUnderflowException;
	
	/**
	 * Formats pixels into a <code>Buffer</code> from the provided input
	 * channel. The <code>position</code> of the returned <code>Buffer</code>
	 * will be undefined, but the <code>limit</code> should be respected.
	 * 
	 * @param channel the source of pixels.
	 * @param width the width of the source image
	 * @param height the height of the source image
	 * @return the formatted pixel data with the constrained limit.
	 * @throws IOException if any IO errors occured
	 * @deprecated we should be using all <code>ByteBuffer</code> logic
	 */
	public Buffer format(ReadableByteChannel channel, int width, int height) throws IOException;
}
