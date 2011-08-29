package reuo.resources.format;

/**
 * An array with an <code>offset</code> to where valid data begins and a
 * <code>length</code> for how much data is valid. This is used to avoid
 * duplicating arrays and to access {@link java.nio.Buffer}s that are backed by
 * an array.
 * 
 * @author Kristopher Ives, Lucas Green
 */
final public class DataMetrics {
	/** The underlying array */
	final public byte[] array;
	/** The position where elements that should be accessed begins */
	final public int pos;
	/** The number of elements that should be accessed */
	final public int length;
	/** The first index that should <b>not</b> be accessed */
	final public int end;
	
	/**
	 * Wraps an array with information about the position where data should be
	 * accessed and the number of elements that should be accessed.
	 * 
	 * @param array the array to wrap
	 * @param pos the begining index
	 * @param length the number of elements
	 * @throws IndexOutOfBoundsException if either the <code>position</code>
	 *             or <code>length</code> are outisde the bounds of
	 *             <code>array</code>
	 */
	public DataMetrics(byte[] array, int pos, int length) throws IndexOutOfBoundsException{
		this.end = pos + length;
		
		if(pos < 0 || end > array.length){
			throw new IndexOutOfBoundsException();
		}
		
		this.array = array;
		this.pos = pos;
		this.length = length;
	}
}
