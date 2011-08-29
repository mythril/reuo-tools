package reuo.resources.io;

import java.io.IOException;

public class FragmentIndexedLoader<E extends Entry, R> extends IndexedLoader<E, R>{
	final IndexedLoader<E, R> parent;
	final int offset, limit;
	
	public FragmentIndexedLoader(IndexedLoader<E, R> parent, int offset){
		this(parent, offset, Integer.MAX_VALUE);
	}
	
	public FragmentIndexedLoader(IndexedLoader<E, R> parent, int offset, int limit){
		this.parent = parent;
		this.offset = offset;
		this.limit = limit;
	}
	
	@Override
	public E getEntry(int id){
		return parent.getEntry(offset + id);
	}
	
	@Override
	public R get(int id) throws IOException{
		return parent.get(offset + id);
	}
	
	@Override
	public int getCapacity(){
		return Math.min(limit - offset, parent.getCapacity());
	}
	
	@Override
	public boolean isCached(int id){
		return parent.isCached(offset + id);
	}
	
	@Override
	public boolean isValid(int id){
		return parent.isValid(offset + id);
	}
	
	@Override
	public R getCached(int id){
		return parent.getCached(offset + id);
	}
}
