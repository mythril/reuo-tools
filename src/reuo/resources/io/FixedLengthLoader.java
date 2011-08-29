package reuo.resources.io;

import java.util.Iterator;

public abstract class FixedLengthLoader<R> extends Loader<R> {
	
	protected static long getOffset(int id){
		throw new UnsupportedOperationException
			("This method was not overridden");
	}
	
	public Iterator<Integer> iterator(){
		return new ContiguousIterator();
	}
	
	public class ContiguousIterator implements Iterator<Integer>{
		int i=0;
		
		public Integer next(){
			return(i++);
		}
		
		public boolean hasNext(){
			return(i < getCapacity());
		}
		
		public void remove(){
			throw(new UnsupportedOperationException());
		}
	}
}
