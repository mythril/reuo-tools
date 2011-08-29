package reuo.resources.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Handles the loading of a resource. Internally this uses a {@link java.lang.ref.SoftReference}
 * to the loaded resource.
 * <h3>Identifiers</h3>
 * Resources are uniquely identified by an integer. Each resource must have an identifier
 * that is unique to it's Loader. The behaviour of the loader and cache if more than one
 * resource share the same identifier is undefined.
 * <h3>Caching</h3>
 * Once a resource is loaded it will be softly-referenced into the cache. If it becomes
 * garbage collected it will be removed from the cache. {@link #isCached(int)}
 * checks if a resource is currently in the cache and {@link #getCached(int)} will
 * attempt to load a resource without accessing any data sources.
 * <h3>Preparing</h3>
 * When a Loader is constructed it will be prepared and ready to load. To change data
 * sources you may invoke <code>prepare</code> of the implementing class. Preparing
 * will clear the cache and initialize the Loader to use new data sources. After preparing
 * a Loader the existing loaded resources are no longer associated with the Loader.
 * @author Kristopher Ives, Lucas Green
 * @param <R> the type of resource being loaded
 */
public abstract class Loader<R>{
	/** Mapping of loaded resources */
	final protected HashMap<Integer, CacheReference> cache = new HashMap<Integer, CacheReference>();
	
	/**
	 * Prepares the Loader for new input. This will clear the cache and any
	 * previously loaded resources are no longer associated with the Loader.
	 * <p>
	 * <b>All implementing classes must invoke this method</b>
	 */

	/**
	 * Gets the maximum possible key of a resource.
	 * @return the capacity
	 */
	public abstract int getCapacity();
	
	/**
	 * Loads a resource or returns a cached resource.
	 * <p>
	 * Implementing classes should initialize a <code>CacheReference</code> if
	 * the resource was loaded successfuly.
	 * @param id the key of the resource to load
	 * @return the loaded resource; or <code>null</code> if the
	 * resource does not exist or is invalid.
	 * @throws IOException if there where any errors reading
	 */
	public abstract R get(int id) throws IOException;
	
	/**
	 * Checks if a resource is currently cached. This doesn't guarantee
	 * that invoking {@link #get(int)} won't load from the resource as
	 * at any time the cache may be forced to clear. To ensure that
	 * no data source is contacted use {@link #getCached(int)}.
	 * @param id the resource key to check the cache for
	 * @see #getCached(int)
	 * @return true if the resource is in the cache
	 */
	public synchronized boolean isCached(int id){
		CacheReference ref = cache.get(id);
		if(ref == null) return(false);
		
		return(ref.get() != null);
	}
	
	/**
	 * Checks if a resource is valid.
	 * @param id the resource to check for validity
	 * @return true if the resource is valid
	 */
	public boolean isValid(int id){
		return(id >= 0 && id <= getCapacity());
	}
	
	/**
	 * Attempts to get a resource from the cache. This will not
	 * attempt to load the resource if it's not contained in the cache
	 * but instead will return <code>null</code>.
	 * @param id the resource key to load from the cache
	 * @return the resource; or <code>null</code> if the resource was not
	 * in the cache
	 */
	public synchronized R getCached( int id){
		CacheReference ref = cache.get(id);
		
		if(ref != null){
			return ref.get();
		}
		
		return null;
	}
	
	public void reset(){
		cache.clear();
	}
	
	/**
	 * A reference to a cached resource. When a resource is successfuly loaded
	 * the implementing class should create a <code>CacheReference</code> to handle
	 * the caching of that resource. If the resource is garbage collected it will
	 * be removed from the cache.
	 * @author Kristopher Ives, Lucas Green
	 */
	protected class CacheReference extends SoftReference<R>{
		final protected int id;
		
		/**
		 * Initializes a <code>CacheReference</code> to handle the
		 * provided resource.
		 * @param id the identifier of the resource
		 * @param r the resource
		 */
		public CacheReference(int id, R r){
			super(r);
			
			synchronized(cache){
				cache.put(id, this);
			}
			
			this.id = id;
		}
		
		@Override
		protected void finalize(){
			//System.out.printf("%d collecting\n", entry.id);
			cache.remove(id);
		}
	}
}