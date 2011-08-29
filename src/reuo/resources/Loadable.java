package reuo.resources;

import java.nio.*;

/**
 * A data structure that may be loaded. This consists of contiguous data.
 * Loadables may be varying in size, but are loadable from a single data source
 * @author Kristopher Ives
 */
public interface Loadable{
	/**
	 * Loads this from the provided data source. The position and limit of the
	 * buffer will be respected, however the position is not guaranteed to be
	 * the limit after invocation.
	 * 
	 * @param in the provided data source
	 * @throws BufferUnderflowException if not enough data was provided
	 */
	public void load(ByteBuffer in) throws BufferUnderflowException;
}
