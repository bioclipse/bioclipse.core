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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Subclass offering a List extension for a DynamicCollection. This allows not
 * just forward, but also backwards iteration through the
 * <code>ListIterator</list>.
 * 
 * @author Costin Leau
 *
 */
public class DynamicList extends DynamicCollection implements List, RandomAccess {

	/**
	 * List iterator.
	 * 
	 * @author Costin Leau
	 * 
	 */
	private class DynamicListIterator extends DynamicIterator implements ListIterator {

		private DynamicListIterator(int index) {
			super.cursor = index;
		}

		public void add(Object o) {
			removalAllowed = false;
			synchronized (lock) {
				DynamicList.this.add(cursor, o);
			}
		}

		public boolean hasPrevious() {
			synchronized (lock) {
				return (cursor - 1 >= 0);
			}
		}

		public int nextIndex() {
			synchronized (lock) {
				return cursor;
			}
		}

		public Object previous() {
			removalAllowed = true;
			if (hasPrevious()) {
				synchronized (lock) {
					return storage.get(--cursor);
				}
			}

			throw new NoSuchElementException();
		}

		public int previousIndex() {
			synchronized (lock) {
				return (cursor - 1);
			}
		}

		public void set(Object o) {
			if (!removalAllowed)
				throw new IllegalStateException();

			synchronized (lock) {
				storage.set(cursor - 1, o);
			}
		}

	}

	public DynamicList() {
		super();
	}

	public DynamicList(Collection c) {
		super(c);
	}

	public DynamicList(int size) {
		super(size);
	}

	public void add(int index, Object o) {
		super.add(index, o);
	}

	public boolean addAll(int index, Collection c) {
		return storage.addAll(index, c);
	}

	public Object get(int index) {
		return storage.get(index);
	}

	public int indexOf(Object o) {
		return storage.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return storage.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		ListIterator iter = new DynamicListIterator(0);

		synchronized (iterators) {
			iterators.put(iter, null);
		}

		return iter;
	}

	public ListIterator listIterator(int index) {
		return new DynamicListIterator(index);
	}

	public Object remove(int index) {
		return super.remove(index);
	}

	public Object set(int index, Object o) {
		return storage.set(index, o);
	}

	// TODO: test behavior to see if the returned list properly behaves under
	// dynamic circumstances
	public List subList(int fromIndex, int toIndex) {
		return storage.subList(fromIndex, toIndex);
	}

}
