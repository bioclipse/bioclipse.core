/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.bioclipse.core.Activator;
import net.bioclipse.core.Recorded;
import net.bioclipse.recording.IHistory;

public class BioObjectList<T extends IBioObject> extends BioObject 
                                                 implements List<T> {
	
	public List<T> list = new ArrayList<T>();
	
	@Recorded
	public boolean add(T e) {
		return list.add(e);
	}

	@Recorded
	public void add(int index, T element) {
		list.add(index, element);
	}

	@Recorded
	public boolean addAll(Collection<? extends T> c) {
		return list.addAll(c);
	}

	@Recorded
	public boolean addAll(int index, Collection<? extends T> c) {
		return list.addAll(c);
	}

	@Recorded
	public void clear() {
		list.clear();
	}

	@Recorded
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Recorded
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Recorded
	public T get(int index) {
		return list.get(index);
	}

	@Recorded
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Recorded
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Recorded
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Recorded
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Recorded
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Recorded
	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	@Recorded
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Recorded
	public T remove(int index) {
		return list.remove(index);
	}

	@Recorded
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Recorded
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Recorded
	public T set(int index, T element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	@Recorded
	public List<T> subList(int fromIndex, int toIndex) {
		BioObjectList<T> result = new BioObjectList<T>();
		result.list = list.subList(fromIndex, toIndex);
		return result;
	}

	@Recorded
	public Object[] toArray() {
		return list.toArray();
	}

	@Recorded
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Recorded
	public Object getParsedResource() {
		return list;
	}
}
