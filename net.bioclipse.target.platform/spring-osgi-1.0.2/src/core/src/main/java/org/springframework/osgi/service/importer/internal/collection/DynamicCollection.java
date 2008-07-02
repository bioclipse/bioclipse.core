/*
 * Copyright 2006-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.osgi.service.importer.internal.collection;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

/**
 * Collection which can be increased or reduced at runtime while iterating.
 * Iterators returned by this implementation are consistent - it is guaranteed
 * that {@link Iterator#next()} will obey the result of the previously called
 * {@link Iterator#hasNext()} even though the collection content has been
 * modified.
 * 
 * This collection is thread-safe with the condition that there is at most one
 * writing thread at a point in time. There are no restrains on the number of
 * readers.
 * 
 * @author Costin Leau
 * 
 */
public class DynamicCollection extends AbstractCollection {

	/**
	 * Dynamic <strong>consistent</strong> iterator. This class has to be
	 * thread-safe since a thread might be iterating while the collection is
	 * being modified.
	 * 
	 * @author Costin Leau
	 * 
	 */
	protected class DynamicIterator implements Iterator {
		/**
		 * cursor pointing to the element that has to be returned by
		 * {@link #next()} method.
		 */
		protected volatile int cursor = 0;

		/**
		 * Lock protecting the cursor which might be affected by the backing
		 * collection shrinking.
		 * 
		 * (not used at the moment)
		 */
		protected final Object lock = new Object();

		protected boolean removalAllowed = false;

		// flag for enforcing the iterator consistency:
		// null - do not enforce anything
		// true - should not throw exception
		// false - should throw exception
		protected Boolean hasNext = null;

		public boolean hasNext() {
			synchronized (iteratorsLock) {
				hasNext = (cursor < storage.size() ? Boolean.TRUE : Boolean.FALSE);
			}

			return hasNext.booleanValue();
		}

		public Object next() {
			try {
				removalAllowed = true;
				// no enforcement
				if (hasNext == null) {
					if (hasNext())
						synchronized (iteratorsLock) {
							return storage.get(cursor++);
						}
					else
						throw new NoSuchElementException();
				}
				else if (hasNext.booleanValue()) {
					synchronized (iteratorsLock) {
						if (hasNext())
							return storage.get(cursor++);
					}
					return null;
				}
				else if (!hasNext.booleanValue())
					throw new NoSuchElementException();

				// default
				throw new NoSuchElementException();
			}
			finally {
				// no matter what, reset hasNext
				hasNext = null;
			}
		}

		public void remove() {
			// make sure the cursor is valid
			if (removalAllowed) {
				removalAllowed = false;
				// delete the cursor update to the main remove method
				synchronized (iteratorsLock) {
					DynamicCollection.this.remove(cursor - 1);
				}
			}
			else
				throw new IllegalStateException();
		}

	}

	/** Lock used by operations that require iterator updates (such as removal) */
	protected final Object iteratorsLock = new Object();

	/** actual collection storage * */
	protected final List storage;

	/** map of weak references to the list iterators */
	/**
	 * should have been a list but there is no 'WeakReference'-based
	 * implementation in the JDK
	 */
	protected final Map iterators;

	public DynamicCollection() {
		this(16);
	}

	public DynamicCollection(int size) {
		storage = Collections.synchronizedList(new ArrayList(size));
		iterators = new WeakHashMap(4);
	}

	public DynamicCollection(Collection c) {
		this(c.size());
		addAll(c);
	}

	public Iterator iterator() {
		Iterator iter = new DynamicIterator();

		synchronized (iteratorsLock) {
			iterators.put(iter, null);
		}

		return iter;
	}

	
	public void clear() {
		storage.clear();
	}

	public int size() {
		return storage.size();
	}

	public boolean add(Object o) {
		return storage.add(o);
	}

	public boolean addAll(Collection c) {
		return storage.addAll(c);
	}

	public boolean contains(Object o) {
		return storage.contains(o);
	}

	public boolean containsAll(Collection c) {
		return storage.containsAll(c);
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public boolean remove(Object o) {
		synchronized (storage) {
			int index = storage.indexOf(o);

			if (index == -1)
				return false;

			remove(index);
			return true;
		}
	}

	// remove an object from the list using the given index
	// this is required for cases where the underlying storage (a list) might
	// contain duplicates.
	protected Object remove(int index) {
		Object o = null;

		// first aquire iterators lock
		synchronized (iteratorsLock) {

			// update storage
			o = storage.remove(index);

			// update iterators

			for (Iterator iter = iterators.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				DynamicIterator dynamicIterator = (DynamicIterator) entry.getKey();

				if (index < dynamicIterator.cursor)
					dynamicIterator.cursor--;
			}
		}

		return o;
	}

	// extra-collection method used by list or sorted set.
	// adds an object to the indicated position forcing an update on the
	// iterators.
	protected void add(int index, Object o) {
		// update iterators (since items are not added at the end
		// anymore)

		synchronized (iteratorsLock) {

			// update storage

			storage.add(index, o);

			for (Iterator iter = iterators.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				DynamicIterator dynamicIterator = (DynamicIterator) entry.getKey();

				if (index < dynamicIterator.cursor)
					dynamicIterator.cursor++;
			}

		}
	}

	public Object[] toArray() {
		return storage.toArray();
	}

	public Object[] toArray(Object[] array) {
		return storage.toArray(array);
	}

	public String toString() {
		return storage.toString();
	}

	/**
	 * Hook used by wrapping collections to determine the position of the object
	 * being removed while iterating.
	 * 
	 * @param o
	 * @return
	 */
	protected int indexOf(Object o) {
		return storage.indexOf(o);
	}

}
