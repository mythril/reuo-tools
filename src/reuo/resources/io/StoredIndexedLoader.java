package reuo.resources.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * A loader that has an index that is backed by a file channel. The entries are
 * softly cached and the underlying data source may be accessed at any time.
 * When <code>prepare</code>d, any previous data source will no longer be
 * access and the entry cache will be cleared.
 * 
 * @author Kristopher Ives, Lucas Green
 * @param <E> the type of entries in the index
 * @param <R> the type of resources being loaded
 */
public abstract class StoredIndexedLoader<E extends Entry, R> extends IndexedLoader<E, R>{
	final protected Map<Integer, SoftReference<E>> entries = new HashMap<Integer, SoftReference<E>>();
	protected FileChannel entrySource = null;
	protected ByteBuffer entryBuffer = null;
	private int entrySize, capacity;
	
	/**
	 * Gets an entry from the buffer.
	 * 
	 * @param id the identifier of the entry in the index
	 * @param buffer the buffer to read from
	 * @return the entry in the index
	 * @throws BufferUnderflowException if there was not enough data in the
	 *             buffer
	 */
	protected abstract E getEntryFromBuffer(int id, ByteBuffer buffer) throws BufferUnderflowException;
	
	public void prepare(FileChannel entrySource, int entrySize) throws IOException{
		entries.clear();
		
		this.entrySource = entrySource;
		this.entrySize = entrySize;
		
		capacity = (int)(entrySource.size() / entrySize);
		entryBuffer = ByteBuffer.allocate(entrySize);
		entryBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public void reset(){
		entries.clear();
		entrySource = null;
		entryBuffer = null;
		entrySize = 0;
		capacity = 0;
	}
	
	/**
	 * Gets the length (in bytes) of an entry in the index. By default this is:
	 * <code>entrySize</code>.
	 * 
	 * @param id the identifier of the entry in the index
	 * @return the size (in bytes) of the entry
	 */
	protected int getEntryLength(int id){
		return entrySize;
	}
	
	/**
	 * Gets the offset in the data source of the entry in the index. By default this
	 * is <code>id * entrySize</code>.
	 * @param id the identifier of the entry in the index
	 * @return the offset (in bytes) of the entry in the index
	 */
	protected long getEntryOffset(int id){
		return id * entrySize;
	}
	
	@Override
	public int getCapacity(){
		return capacity;
	}
	
	@Override
	public E getEntry(int id){
		if(id < 0 || id > getCapacity()){
			return null;
		}
		
		SoftReference<E> ref = entries.get(id);
		E entry = null;
		
		if(ref != null){
			entry = ref.get();
			
			if(entry != null){
				return entry;
			}
		}
		
		if(entry == null){
			entryBuffer.limit(getEntryLength(id)).rewind();
			
			try{
				entrySource.read(entryBuffer, getEntryOffset(id));
			}catch(IOException e){
				return null;
			}
			
			entryBuffer.rewind();
			
			// Construct an entry and place it into the cache
			entry = getEntryFromBuffer(id, entryBuffer);
			// entry.id = id;
			
			if(entry.isValid()){
				entries.put(id, new SoftReference<E>(entry));
			}
		}
		
		return entry;
	}
}
