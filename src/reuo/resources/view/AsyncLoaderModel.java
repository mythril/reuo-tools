package reuo.resources.view;

import java.io.IOException;
import java.util.*;

import javax.swing.SwingUtilities;
import javax.swing.event.*;

import reuo.resources.io.*;

/**
 * Provides an asynchronous {@link javax.swing.ListModel} for accessing the data of a {@link Loader}.
 * This will spawn a background job daemon thread that will handle loading resources
 * and fire an {@link ListDataEvent}. While the resource is loading a prototype object
 * will be returned that may be worked with until the background load is completed.
 * <p>
 * <h3>Pruning</h3>
 * {@link Loader}s do not keep hard references to the resources. When a resource is loaded
 * via the daemon thread a reference must be kept until all of the event listeners have
 * been notified and subsequent call to {@link #getElementAt} should return the loaded
 * resource. Because of this it is essential that {@link #pruneTo} is called to remove
 * unused references, because otherwise an {@link OutOfMemoryError} may occur due to
 * hard references of too many loaded resources.
 * @author Kristopher Ives
 */
public class AsyncLoaderModel extends LoaderModel{
	final Object prototype;
	final JobThread thread = new JobThread();
	final JobQueue queue = new JobQueue();
	final SortedMap<Integer, Object> done = Collections.synchronizedSortedMap(new TreeMap<Integer, Object>());
	
	/**
	 * Initializes the asynchronous loader with the provided underlying loader
	 * and prototype. This is the same as calling
	 * <code>AsyncLoaderModel(indexedLoader, indexedLoader, prototype)</code>.
	 * @param indexedLoader the underlying loader
	 * @param prototype the prototype
	 * @see #AsyncLoaderModel(Loader, Iterable, Object)
	 */
	public AsyncLoaderModel(IndexedLoader<?, ?> indexedLoader, Object prototype){
		this(indexedLoader, indexedLoader, prototype);
	}
	
	/**
	 * Initializes the asynchronous loader with the provided underlying loader using
	 * <code>indices</code> as a source for valid resource identifiers and the prototype
	 * as a temporary object to provide while background loads occur.
	 * @param loader the underlying loader
	 * @param indices the source for valid resource identifiers
	 * @param prototype the temporary prototype
	 */
	public AsyncLoaderModel(Loader<?> loader, Iterable<Integer> indices, Object prototype){
		super(loader, indices);
		
		this.prototype = prototype;
		thread.setPriority(Math.max(Thread.MIN_PRIORITY, Thread.currentThread().getPriority()-5));
		thread.start();
	}
	
	// TODO: implement a less/greater system
	public void pruneTo(int from, int to){
		from = Math.max(from, 0);
		to = Math.min(to, getSize());
		
		synchronized(queue){
			queue.headSet(from).clear();
			queue.tailSet(to).clear();
			
			done.headMap(from).clear();
			done.tailMap(to).clear();
		}
	}
	
	/**
	 * Gets the underlying daemon thread that handles resource
	 * loading
	 * @return the underlying Thread
	 */
	public Thread getThread(){
		return thread;
	}
	
	@Override
	protected void finalize(){
		// Ensure that the daemon thread is ended as
		// gracefully as possible
		if(thread != null && thread.isAlive()){
			thread.end();
		}
	}
	
	@Override
	public Object getElementAt(final int index){
		// First check the existing done cache
		Object value = done.get(index);
		
		if(value != null){
			return value;
		}
		
		// Check if the loader has it cached
		final int id = valid.get(index);
		value = loader.getCached(id);
		
		if(value != null){
			return value;
		}
		
		// Enqueue the load and provide the prototype
		synchronized(queue){
			if(!queue.contains(index)){
				queue.add(index);
				queue.notifyAll();
			}
		}
		
		return prototype;
	}
	
	/**
	 * A queue that contains indices (in terms of ListModel) that
	 * are queued for loading.
	 * @author Kristopher Ives
	 */
	final private static class JobQueue extends TreeSet<Integer>{
		/**
		 * Gets the next index that needs to be loaded. This is implemented
		 * so that the resources are loaded from the "center" of the list.
		 * @return the next index (in terms of ListModel)
		 * @throws NoSuchElementException if the queue is empty
		 */
		final public Integer deque() throws NoSuchElementException{
			final int min = first();
			final int max = last();
			final int key = min + (max - min) / 2;
			int id = tailSet(key).first();
			
			if(!remove(id)){
				id = headSet(key).last();
				remove(id);
			}
			
			return id;
		}
	}
	
	/**
	 * Because Swing is not thread safe we must invoke the events on
	 * the swing worker thread. The EventDispatcher will attempt to
	 * collapse multiple events to reduce redundant event fires.
	 * @author Kristopher Ives
	 */
	final private class EventDispatcher implements Runnable{
		int from, to;
		boolean dispatched = false;
		
		/**
		 * Initializes an EventDispatcher for the provided index.
		 * @param index the element index of the ListModel
		 */
		public EventDispatcher(final int index){
			this.from = index;
			this.to = index;
		}
		
		/**
		 * Checks if the dispatcher has already occured.
		 * @return true if the event has been dispatched
		 */
		public synchronized boolean isDispatched(){
			return dispatched;
		}
		
		/**
		 * Checks if the dispatcher can combine the provided index.
		 * If the dispatcher is already dispatched this will return
		 * false.
		 * @param index the element index of the ListModel
		 * @return true if the index can be combined
		 * @see #combine
		 */
		public synchronized boolean canCombine(final int index){
			if(dispatched){
				return false;
			}
			
			return index == from-1 || index == to+1;
		}
		
		/**
		 * Adds the provided index to be dispatched
		 * @param index the element index of the ListModel
		 * @see #canCombine
		 */
		public synchronized void combine(final int index){
			if(from-1 == index){
				from = index;
			}else if(to+1 == index){
				to = index;
			}
		}
		
		public void run(){
			synchronized(this){
				dispatched = true;
			}
			
			final ListDataEvent event = new ListDataEvent(
				AsyncLoaderModel.this,
				ListDataEvent.CONTENTS_CHANGED,
				from, to
			);
			
			for(ListDataListener l : listeners){
				l.contentsChanged(event);
			}
		}
	}
	
	/**
	 * The job daemon thread that runs in the background invoking
	 * {@link Loader.#get}.
	 */
	private class JobThread extends Thread{
		boolean running = true;
		
		public synchronized void end(){
			running = false;
			notifyAll();
		}
		
		public void run(){
			Object value;
			int index, id;
			
			while(running){
				EventDispatcher lastEvent = null;
				
				while(running){
					synchronized(queue){
						if(queue.isEmpty()){
							break;
						}
						
						index = queue.deque();
					}
					
					if(done.containsKey(index)){
						continue;
					}
					
					id = valid.get(index);
					
					try{
						value = loader.get(id);
					}catch(IOException e){
						value = null;
					}
					
					done.put(index, value);
					
					if(lastEvent != null){
						if(lastEvent.canCombine(index)){
							lastEvent.combine(index);
						}else{
							SwingUtilities.invokeLater(lastEvent);
							lastEvent = new EventDispatcher(index);
						}
					}else{
						lastEvent = new EventDispatcher(index);
					}
					
					yield();
				}
				
				if(lastEvent != null && !lastEvent.isDispatched()){
					SwingUtilities.invokeLater(lastEvent);
				}
				
				try{
					synchronized(queue){
						if(queue.isEmpty()){
							queue.wait();
						}
					}
				}catch(InterruptedException e){
					continue;
				}
			}
		}
	}
}
