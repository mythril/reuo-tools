package reuo.resources.io;

/**
 * An entry into an index that describes meta-information about {@link Resource}s.
 * All entries contain information about the identifier, offset, and length of
 * the resource. Since most entries include an "extra" field that is used for
 * various reasons it has also been included
 * <p>
 * Before version <code>1.5</code> of this class there where <code>get</code>
 * methods for id, offset, length, and extra. These have now been ommited
 * because these members have been made final and are now exposed as public.
 * 
 * @author Kristopher Ives, Lucas Green
 */
public class Entry{
	/** The identiier of the resource this entry is indexing */
	final public int id;
	/** The offset (in bytes) in the data source for the resource */
	final public long offset;
	/** The length (in bytes) of the resource */
	final public int length;
	/** Extra information stored in the index (optional) */
	final public int extra;
	
	/**
	 * An entry in the index for a resource identified by <code>id</code> at
	 * the given <code>offset</code> of <code>length</code>.
	 * 
	 * @param id the resource identifier
	 * @param offset the offset (in bytes) of the resource
	 * @param length the length (in bytes) of the resource
	 * @param extra any extra information stored in the index
	 */
	public Entry(int id, long offset, int length, int extra){
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.extra = extra;
	}
	
	/**
	 * {@inheritDoc #Entry(int, long, int, int)} The <code>extra</code> field
	 * is initialized to <code>0</code>.
	 * 
	 * @param id the resource identifier
	 * @param offset the offset (in bytes) of the resource
	 * @param length the length (in bytes) of the resource
	 */
	public Entry(int id, long offset, int length){
		this(id, offset, length, 0);
	}
	
	/**
	 * Checks if entry in the index is valid. If the entry is not valid the
	 * resource should not be loaded. Extending classes may override this method
	 * as needed, but the default implementaiton ensures that
	 * <code>offset &gt;= 0 && length &gt; 0</code>
	 * 
	 * @return true if and only if the entry is valid
	 */
	public boolean isValid(){
		return offset >= 0 && length > 0;
	}
}