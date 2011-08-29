package reuo.resources.io;

import java.io.IOException;
import java.util.*;

/**
 * A {@link Loader} that has an index that was generated and is stored in
 * memory. The index should be small, otherwise a {@link StoredIndexedLoader}
 * should be used.
 * 
 * @author Kristopher Ives, Lucas Green
 * @param <E> the type of entries in the index
 * @param <R> the type of resources being loaded
 */
public abstract class MemoryIndexedLoader<E extends Entry, R> extends IndexedLoader<E, R>{
	final protected SortedMap<Integer, E> entries = new TreeMap<Integer, E>();
	
	public void reset(){
		super.reset();
		entries.clear();
	}
	
	/**
	 * Generates the index. This should be invoked after the loader has been
	 * prepared.
	 * 
	 * @throws IOException if any data source errors occur while generating the
	 *             index
	 */
	protected abstract void generateIndex() throws IOException;
	
	@Override
	public E getEntry(int id){
		return entries.get(id);
	}
	
	@Override
	public int getCapacity(){
		return entries.size();
	}
	
	@Override
	public Iterator<Integer> iterator(){
		return entries.keySet().iterator();
	}
}
