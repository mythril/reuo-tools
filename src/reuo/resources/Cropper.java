package reuo.resources;

import reuo.resources.io.AutoCropChannel;
import reuo.util.Rect;

/**
 * Provides information about the cropping of image data, usually a
 * {@link Bitmap}. If the image can be cropped while loading this interface
 * should be implemented. If the class loading the image is more complex the
 * {@link AutoCropChannel} may be used to handle cropping at the expense of
 * performance.
 * 
 * @author Kristopher Ives
 */
public abstract class Cropper{
	protected Rect crop = new Rect(
		Integer.MAX_VALUE,
		Integer.MAX_VALUE,
		Integer.MIN_VALUE,
		Integer.MIN_VALUE);
	
	/**
	 * Checks if any uncroppable pixels where encountered.
	 * 
	 * @return true if the image was empty
	 */
	public boolean isEmpty(){
		return crop.top == Integer.MAX_VALUE && crop.left == Integer.MAX_VALUE && crop.bottom == Integer.MIN_VALUE && crop.right == Integer.MIN_VALUE;
	}
	
	/**
	 * Resets the state of cropping to empty. {@link #isEmpty()} should return
	 * <code>true</code> until the cropper has been fed uncroppable pixels.
	 */
	public void resetCropping(){
		crop.left = crop.top = Integer.MAX_VALUE;
		crop.right = crop.bottom = Integer.MIN_VALUE;
	}
	
	/**
	 * Gets the insets that pad the image that was cropped
	 * @return the padding rectangle
	 */
	public Rect getInsets(){
		return new Rect(crop.top, crop.left, crop.bottom - 1, crop.right - 1);
	}
	
	/**
	 * Gets the coordinates of the cropped portion of the image.
	 * @return the cropped coordinates
	 */
	public Rect getCoordinates(){
		return new Rect(crop.top, crop.left, crop.bottom + 1, crop.right + 1);
	}
}
