package reuo.resources;

/**
 * A Block is a segment of {@link Cell}s that may be accessed by relative <i>x</i>
 * and <i>y</i> coordinates. Internally the Cells are stored in a
 * single-dimension array to avoid the overhead of additional array objects. It
 * is safe to access the contents of the <code>cells</code> array, although it
 * will not be thread safe unless synchronized on the array.
 * <h3>Block Sizes</h3>
 * Blocks must have the same that is:
 * <ul>
 * <li>The same horizontally and vertically</li>
 * <li>The same for each instance of the same class</li>
 * <li>A power of two</li>
 * </ul>
 * 
 * @author Kristopher Ives
 * @param <T> the type of cells
 */
public abstract class Block<T> {
	public final T[] cells;
	public final int size;
	
	/**
	 * A block of the specified horizontal and vertical size with the specified
	 * cells.
	 * 
	 * @param size the horizontal and vertical size
	 * @param cells the cells (in row major)
	 */
	protected Block(int size, T[] cells){
		this.cells = cells;
		this.size = size;
	}
	
	/**
	 * Gets a cell using coordinates relative to the Block
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the cell; may be <code>null</code> if the cell is unused
	 * @throws IndexOutOfBoundsException if the coordinates are outside the
	 *             Block
	 */
	public T get(int x, int y) throws IndexOutOfBoundsException{
		return cells[x + y * size];
	}
}
