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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import net.bioclipse.core.Activator;
import net.bioclipse.core.Recorded;
import net.bioclipse.recording.IHistory;

public class BioList<T extends IBioObject> extends BioObject 
                                                 implements List<T> {
	
	private static Map<List<String>, String> createdLists 
		= new HashMap<List<String>, String>();
	
	public static String idOfListContainingBioObject(String id) {
		for ( List<String> l : createdLists.keySet() ) {
			for ( String s : l ) {
				if ( id.equals(s) ) {
					return createdLists.get(l);
				}
			}
		}
		throw new IllegalStateException( "No bioObjectlist containing that " +
				                         "object could be found" );
	}
	
	public static int positionOfBioObjectInList( String id ) {
		for( List<String> l : createdLists.keySet() ) {
			if(l.contains(id)) {
				return l.indexOf(id);
			}
		}
		throw new IllegalStateException();
	}
	
	public static boolean existsListContaining(String bioObjectId) {
		for( List<String> l : createdLists.keySet() ) {
			if(l.contains(bioObjectId)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateCreatedLists( BioList<? extends IBioObject> list ) {
		for( List<String> l : createdLists.keySet() ) {
			if( createdLists.get(l).equals( list.getId() ) ) {
				createdLists.remove(l);
			}
		}
		List<String> newList = new ArrayList<String>();
		for( IBioObject b : list) {
			newList.add( b.getId() );
		}
		createdLists.put( newList, list.getId() );
	}
	
	public List<T> list = new ArrayList<T>();
	
	@Recorded
	public boolean add(T e) {
		boolean b = list.add(e);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public void add(int index, T element) {
		list.add(index, element);
		updateCreatedLists(this);
	}

	@Recorded
	public boolean addAll(Collection<? extends T> c) {
		boolean b = list.addAll(c);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean b = list.addAll(c);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public void clear() {
		list.clear();
		updateCreatedLists(this);
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
		boolean b = list.remove(o);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public T remove(int index) {
		T t = list.remove(index);
		updateCreatedLists(this);
		return t;
	}

	@Recorded
	public boolean removeAll(Collection<?> c) {
		boolean b = list.removeAll(c);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public boolean retainAll(Collection<?> c) {
		boolean b = list.retainAll(c);
		updateCreatedLists(this);
		return b;
	}

	@Recorded
	public T set(int index, T element) {
		T t = list.set(index, element);
		updateCreatedLists(this);
		return t;
	}

	public int size() {
		return list.size();
	}

	@Recorded
	public List<T> subList(int fromIndex, int toIndex) {
		//TODO: find a good solution to this. It is very complicated...
		throw new NotImplementedException();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Recorded
	public Object getParsedResource() {
		return list;
	}
}
