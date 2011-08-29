package reuo.resources;

import java.nio.ByteBuffer;
import java.util.*;

import reuo.resources.io.Entry;

/**
 * A data structure that contains multiple properties. Each property may be
 * loaded independently and the resource may be queried for details on what
 * properties are currently present. For example, you may wish to load the name
 * of a {@link Sound}, but not the actual waveform data.
 * <h3>Identifiers</h3>
 * Every resource must have an identifier that should be unique to it's class.
 * If two resources of the same class have the same identifier it may result in
 * undefined behaviour.
 * <h3>Properties</h3>
 * Resources may have an arbitrary amount of properties. These properties may be
 * loaded at any time using {@link #load(Enum, ByteBuffer)}. Loading a property
 * will discard the property if it was already loaded.
 * <h4>Lengths</h4>
 * Each property may have either a fixed or variable length. Fixed length
 * properties will always consume their length, even if not used. For example an
 * ASCII <code>String</code> of fixed-length <code>20</code> will always
 * take up <code>20</code> bytes even if the string was <code>"foo"</code>
 * and only required 4 bytes.
 * <h4>Layout</h4>
 * Each property may be at a fixed position relative to the resource or at a
 * position which is dependent on other properties. For example, if a resource
 * contained a variable-length property <code>A</code> followed by a
 * fixed-length property <code>B</code>, then <code>B</code> would be
 * dependent on the variable-length of <code>A</code>.
 * 
 * @author Kristopher Ives
 * @param
 *         <P>
 *         the type of properties this Resource contains
 */
public abstract class Resource<P extends Enum<P> & Resource.Property>{
	/** The identifier of the resource */
	final protected int id;
	
	public interface Property{
		public Class<?> getType();
	}
	
	/**
	 * Instantiates an empty resource with the provided identifier. Invoking
	 * {@link #has(Enum)} should return <code>false</code> for every property.
	 * 
	 * @param id an identifier unique to this class of resources
	 */
	public Resource(int id){
		this.id = id;
	}
	
	/**
	 * Gets the identifier for the resource. This is unique for this class of
	 * resources.
	 * 
	 * @return the resource identifier
	 */
	public int getId(){
		return id;
	}
	
	public Class<?> type(){
		return null;
	}
	
	/*
	public static class Range{
		final int position, length;
		
		public Range(int position, int length){
			this.position = position;
			this.length = length;
		}
	}
	
	public abstract EnumMap<P, Range> map(EnumSet<P> properties, int length);
	*/
	/** @deprecated */
	public abstract int locate(P property, Entry entry) throws IllegalArgumentException;
	/** @deprecated */
	public abstract int measure(P property, Entry entry) throws IllegalArgumentException;
	
	/**
	 * Checks if a property is already loaded with this resource.
	 * 
	 * @param property the property to check
	 * @return true if the property has been loaded
	 * @throws IllegalArgumentException if the property is not part of this
	 *             resource
	 */
	public abstract boolean has(P property) throws IllegalArgumentException;
	
	/**
	 * Loads a property into this resource. If the property was previously
	 * loaded it may be discarded even if this invocation fails. The property
	 * will be read from the provided buffer from it's current position with
	 * respect to it's limit, but this does not guarantee that the position will
	 * be the limit after this invocation.
	 * 
	 * @param property the property to load
	 * @param in the buffer to read the property from
	 * @throws IllegalArgumentException
	 */
	public abstract void load(P property, ByteBuffer in) throws IllegalArgumentException;
	
	/**
	 * Gets a previously loaded property from this resource. This method will
	 * <b>not</b> return <code>null</code> if the property was invalid; but
	 * instead will throw an <code>IllegalStateException</code>. You should
	 * use {@link #has(Enum)} to check if the property exists before invoking
	 * this method.
	 * 
	 * @param property the property to get
	 * @return the requested property
	 * @throws IllegalStateException if the resource does not have the property
	 * @throws IllegalArgumentException if the property is not part of this
	 *             resource
	 */
	public abstract Object get(P property) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Skips over this propery in the buffer. Afterward the position will be as
	 * if the property had been read from the buffer.
	 * 
	 * @deprecated this will no longer be needed once we do proper mapping of
	 *             input buffers.
	 * @param property the property to skip
	 * @param buf the buffer to use
	 * @throws IllegalArgumentException
	 */
	public abstract void skip(P property, ByteBuffer buf) throws IllegalArgumentException;
	
	/**
	 * Checks if the resource has all the specified properties. This is a
	 * convienence method and unless overriden will just call {@link #has(Enum)}
	 * for each property.
	 * 
	 * @param properties the properties to check
	 * @return true if all the properties exist in the resource
	 * @throws IllegalArgumentException if any of the requested properties are
	 *             invalid
	 */
	public boolean hasAll(EnumSet<P> properties) throws IllegalArgumentException{
		for(P p : properties){
			if(!has(p)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc #hasAll(EnumSet)}
	 */
	public boolean hasAll(P first, P... props) throws IllegalArgumentException{
		if(!has(first)){
			return false;
		}
		
		for(P p : props){
			if(!has(p)){
				return false;
			}
		}
		
		return true;
	}
}
