package reuo.resources.io;

import java.util.Iterator;

/**
 * A {@link Loader} that has an index. The index is a set of
 * {@link Entry entries} that describe the {@link Resource}s.
 * <h3>Entries</h3>
 * Entries describe resources and share the same identifier. This index
 * typically describes where the resource is, but may include other
 * meta-information. Any entry may be invalid in the index. The identifiers for
 * entries may be sparse because some entries may be invalid. This class
 * implements {@link Iterable} which provides an iterator for valid entry
 * identifiers.
 * <h3>Stored and Generated Indexes</h3>
 * An index may exist from a data source or generated in memory. Generated
 * indexes are typically small, while indexes from data sources are large and
 * usually mapped to a file.
 * 
 * @author Kristopher Ives
 * @param <E> the type of entries
 * @param <R> the type of the resources
 * @see MemoryIndexedLoader
 * @see StoredIndexedLoader
 */
public abstract class IndexedLoader<E extends Entry, R> extends Loader<R> implements Iterable<Integer>{
	
	/**
	 * Gets the entry for the specified resource from the index.
	 * 
	 * @param id the resource identifier
	 * @return the index entry
	 */
	public abstract E getEntry(int id);
	
	/**
	 * Gets an iteration of the valid entry identifiers in the index.
	 * Implementing classes may override this, but by default this will iterate
	 * from <code>0</code> to {@link #getCapacity()} only returning valid
	 * entries.
	 */
	public Iterator<Integer> iterator(){
		return new ValidIndexIterator();
	}
	
	protected class ValidIndexIterator implements Iterator<Integer>{
		int start, limit;
		int index;
		
		public ValidIndexIterator(){
			this(0, getCapacity());
		}
		
		public ValidIndexIterator(int start, int limit){
			this.start = start;
			this.limit = limit;
			this.index = start - 1;
		}
		
		public boolean hasNext(){
			E entry = null;
			
			do{
				index++;
				if(index >= limit){
					return false;
				}
				
				entry = getEntry(index - start);
			}while(entry == null || !entry.isValid());
			
			return true;
		}
		
		public Integer next(){
			return index - start;
		}
		
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}
}
