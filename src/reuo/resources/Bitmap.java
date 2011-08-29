package reuo.resources;

import java.nio.Buffer;

import reuo.util.Rect;

/**
 * An image that is represented with an underlying {@link java.nio.Buffer} that
 * holds the data.
 * <h3>Insets</h3>
 * To conserve memory <code>Bitmap</code>s may have padding on all four
 * sides. However, {@link #getInsets()} may return <code>null</code> if there
 * is no padding. The dimensions of the <code>Bitmap</code> with padding is
 * given by {@link #getWidth()} and {@link #getHeight()}, while the dimensions
 * of the actual image that is stored are given by {@link #getImageHeight()} and
 * {@link #getImageWidth()}. A <code>Bitmap</code> may entirely be padding if it
 * contains no image data and only padding, in which case {@link #isEmpty()}
 * will return true.
 * <h3>Data Formats</h3>
 * The pixel format of a Bitmap is implied by the type of Buffer that is
 * returned by {@link #getData()}. This Buffer may be:
 * <ul>
 * <li><b>ShortBuffer:</b> 16-bit per pixel. Typically 5 bits per component
 * with 1 bit for alpha. </li>
 * <li><b>ByteBuffer:</b> These pixels are indexed with a {@link Palette}.</li>
 * </ul>
 * 
 * @author Kristopher Ives
 */
public class Bitmap{
	final private int id;
	final private int width, height;
	final private Rect insets;
	final private Buffer data;
	
	public Bitmap(int id, int width, int height){
		this(id, width, height, null, null);
	}
	
	public Bitmap(int id, int width, int height, Buffer data){
		this(id, width, height, data, null);
	}
	
	public Bitmap(int id, int width, int height, Buffer data, Rect insets){
		this.id = id;
		this.width = width;
		this.height = height;
		this.data = data;
		this.insets = insets;
		// this.formatter = formatter;
	}
	
	public int getId(){
		return id;
	}
	
	/**
	 * Gets the image data of the Bitmap.
	 * @return the imag data; or <code>null</code> if no image data exists
	 * @see #isEmpty()
	 */
	public Buffer getData(){
		return data;
	}
	
	/**
	 * Gets the width of the Bitmap image. This will be <code>0</code> if
	 * the Bitmap has no image.
	 * @return the width of the bitmap image
	 * @see #isEmpty()
	 */
	public int getImageWidth(){
		return width;
	}
	
	/**
	 * Gets the height of the Bitmap image. This will be <code>0</code> if
	 * the Bitmap has no image.
	 * @return the height of the bitmap image
	 * @see #isEmpty()
	 */
	public int getImageHeight(){
		return height;
	}
	
	/**
	 * Gets the width of the Bitmap. If the Bitmap has insets it will be calculated.
	 * @return the width of the Bitmap
	 */
	public int getWidth(){
		return getImageWidth() + (insets == null ? 0 : insets.left + insets.right);
	}
	
	/**
	 * Gets the height of the Bitmap. If the Bitmap has insets it will be calculated.
	 * @return the height of the Bitmap
	 */
	public int getHeight(){
		return getImageHeight() + (insets == null ? 0 : insets.top + insets.bottom);
	}
	
	/**
	 * Checks if the Bitmap is empty and only consists of insets.
	 * @return true if no image data exists
	 */
	public boolean isEmpty(){
		return data == null;
	}
	
	/**
	 * Gets the insets needed to preserve the layout of the Bitmap once displayed
	 * @return the insets; or <code>null</code> if there is no padding
	 */
	public Rect getInsets(){
		return insets;
	}
}