/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *     Ola Spjuth
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
import java.util.Stack;
import org.eclipse.ui.views.properties.IPropertySource;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.domain.props.BioListPropertySource;
import net.bioclipse.core.domain.props.BioObjectPropertySource;
public class BioList<T extends IBioObject> extends BioObject 
                                           implements List<T> {
    /**
     * The PropertySource available as adapter
     */
    private IPropertySource propertySource;
    //<bioList.id, list of bioObjects in that biolist>
    private static Map<String, List<String>> createdLists 
         = new HashMap<String, List<String>>();
    //<bioObject.id, bioList.id>
    private static Map<String, Stack<String>> listIdForObject 
         = new HashMap<String, Stack<String>>();
    public List<T> list = new ArrayList<T>();
    public BioList() {
    }
    public BioList(List<T> list) {
        this.list = new ArrayList<T>(list);
    }
    /**
     * @param id for the sought after bioObject
     * @return id of the latest updated list conatining the bioobject 
     *         with the given id 
     */
    public static String idOfListContainingBioObject(String id) {
        if(!listIdForObject.containsKey(id)) {
            throw new IllegalStateException( "No bioObjectlist containing " +
                                             "that object could be found" );
        }
        return listIdForObject.get(id).peek();
    }
    public static int positionOfBioObjectInList( String id ) {
        List<String> list = createdLists.get(idOfListContainingBioObject(id));
        for (int i = 0; i < list.size(); i++) {
            if( id.equals(list.get(i))) {
                return i;
            }
        }
        throw new IllegalStateException( "No bioObjectlist containing " +
                                         "that object could be found" );
    }
    public static boolean existsListContaining(String bioObjectId) {
        for( List<String> l : createdLists.values() ) {
            if(l.contains(bioObjectId)) {
                return true;
            }
        }
        return false;
    }
    /**
     *  Updates the created lists table.
     */
    private void updateCreatedLists( BioList<? extends IBioObject> list ) {
        createdLists.remove(list.getUID());
        clearListIdForObject( list.getUID() );
        List<String> newList = new ArrayList<String>();
        for( IBioObject b : list) {
            newList.add( b.getUID() );
            if (listIdForObject.containsKey(b.getUID())) {
                listIdForObject.get(b.getUID()).push( list.getUID() );
            }
            else {
                Stack<String> newStack = new Stack<String>();
                newStack.push( list.getUID() );
                listIdForObject.put(b.getUID(), newStack);
            }
        }
        createdLists.put( list.getUID(), newList );
    }
    private void clearListIdForObject(String listId) {
        for( String objectId : listIdForObject.keySet() )
            if( listIdForObject.get(objectId).contains(listId) )
                listIdForObject.get(objectId).remove(listId);
    }
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
        boolean b = list.addAll(index, c);
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
    public Iterator<T> iterator() {
        return list.iterator();
    }
    @Recorded
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }
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
        // (NotImplementedException is not what it sounds like, use j.l.UOE)
        throw new java.lang.UnsupportedOperationException();
    }
    public Object[] toArray() {
        return list.toArray();
    }
    public <U> U[] toArray(U[] a) {
        return list.toArray(a);
    }
    @Recorded
    public Object getParsedResource() {
        return list;
    }
    /**
     * Basic properties. Should be overridden by subclasses.
     */
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class){
            return propertySource!=null 
                ? propertySource : new BioListPropertySource(this);
        }
        return super.getAdapter(adapter);
    }
}
