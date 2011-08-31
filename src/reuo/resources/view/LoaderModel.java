package reuo.resources.view;

import java.io.IOException;
import java.util.*;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import reuo.resources.io.*;

/**
 * Provides a synchrnous {@link ListModel} interface to a {@link Loader}. When
 * an element is requested via {@link #getElementAt(int)} it will be loaded from
 * the underlying loader, which may have the resource cached. This is only
 * recommened for loaders that offer very quick access, because this will block
 * the AWT event thread.
 * 
 * @author Kristopher Ives
 * @see AsyncLoaderModel
 */
public class LoaderModel<T> implements ListModel<T> {
	/** The listeners that will be alerted of changes */
	final protected List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	/**
	 * A mapping between valid resource ids and the indices exposed to the
	 * ListModel
	 */
	final protected Map<Integer, Integer> valid = new HashMap<Integer, Integer>();
	/** The underlying loader */
	final protected Loader<T> loader;

	/**
	 * Initializes a LoaderModel with the specified underlying loader which will
	 * be used to valid entry identifiers. This is the same as invoking
	 * <code>LoaderModel(indexedLoader, indexedLoader)</code>.
	 * 
	 * @param indexedLoader
	 *            the underlying loader
	 * @see #LoaderModel(Loader, Iterable)
	 */
	public LoaderModel(final IndexedLoader<?, T> indexedLoader) {
		this(indexedLoader, indexedLoader);
	}

	/**
	 * Initializes a LoaderModel with the specified loader using
	 * <code>indices</code> as a source for valid entry identifiers.
	 * 
	 * @param loader
	 *            the underlying loader
	 * @param indices
	 *            the source for valid entry identifiers
	 */
	public LoaderModel(final Loader<T> loader, final Iterable<Integer> indices) {
		this.loader = loader;

		for (int i : indices) {
			valid.put(valid.size(), i);
		}
	}

	/**
	 * Gets a entry identifier from the provided model index.
	 * 
	 * @param index
	 *            the ListModel index
	 * @return the resource entry identifier
	 */
	public int getId(final int index) {
		return valid.get(index);
	}

	public void addListDataListener(final ListDataListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public T getElementAt(int index) {
		try {
			return (T)loader.get(valid.get(index));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getSize() {
		return valid.size();
	}

	public void removeListDataListener(final ListDataListener listener) {
		listeners.remove(listener);
	}
}
