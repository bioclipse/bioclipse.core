/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RecordableListTest {

    private static class TestBioObject extends BioObject {}

    private RecordableList<IBioObject> biolist;
    private IBioObject          bioObject;
    private IBioObject          bioObject2;
    
    public RecordableListTest() {
        biolist   = new RecordableList<IBioObject>();
        bioObject = new TestBioObject();
        bioObject2 = new TestBioObject();
    }
    
    @Test
    public void testAddT() {
        biolist.add(bioObject);
        assertTrue( biolist.contains(bioObject) );
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testAddSeveral() {
        biolist.add(bioObject);
        biolist.add(bioObject2);
        assertTrue( biolist.contains(bioObject) );
        assertTrue( biolist.contains(bioObject2) );
    }

    @Test
    public void testAddIntT() {
        biolist.add( new TestBioObject() );
        biolist.add( 0,bioObject );
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testAddAllCollectionOfQextendsT() {
        RecordableList<IBioObject> toBeAdded = new RecordableList<IBioObject>();
        toBeAdded.add(bioObject);
        biolist.addAll(toBeAdded);
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testAddAllIntCollectionOfQextendsT() {
        RecordableList<IBioObject> toBeAdded = new RecordableList<IBioObject>();
        biolist.add( new TestBioObject() );
        toBeAdded.add(bioObject);
        biolist.addAll(0, toBeAdded);
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testClear() {
        
        testAddT();
        RecordableList<IBioObject> aList = new RecordableList<IBioObject>();
        aList.add(bioObject);
        aList.clear();
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testContains() {
        testAddT();
        assertTrue( biolist.contains(bioObject) );
    }

    @Test
    public void testContainsAll() {
        testAddT();
        RecordableList<IBioObject> anotherList = new RecordableList<IBioObject>();
        anotherList.add(bioObject);
        assertTrue( biolist.containsAll(anotherList) );
    }

    @Test
    public void testGet() {
        testAddT();
        assertEquals( bioObject, biolist.get(0) );
    }

    @Test
    public void testIndexOf() {
        biolist.add(new TestBioObject());
        biolist.add(bioObject);
        assertEquals(1, biolist.indexOf(bioObject));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(biolist.isEmpty());
    }

    @Test
    public void testRemoveObject() {
        testAddT();
        biolist.remove(bioObject);
        assertFalse( RecordableList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testRemoveInt() {
        testAddT();
        biolist.remove(0);
        assertFalse( RecordableList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testRemoveAll() {
        testAddT();
        RecordableList<IBioObject> list = new RecordableList<IBioObject>();
        list.add(bioObject);
        
        biolist.removeAll(list);
        assertEquals( RecordableList.idOfListContainingBioObject(bioObject.getUID()),
                      list.getUID() );
    }

    @Test
    public void testRetainAll() {
        IBioObject obj2 = new TestBioObject();
        biolist.add(obj2);
        
        RecordableList<IBioObject> list = new RecordableList<IBioObject>();
        list.add(obj2);
        
        biolist.retainAll(list);
        
        assertTrue(  biolist.contains(obj2)    );
        assertFalse( biolist.contains(biolist) );
        
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(obj2.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(obj2.getUID()) );
        assertFalse( RecordableList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testSet() {
        testAddT();
        IBioObject obj2 = new TestBioObject();
        biolist.set(0, obj2);
        assertEquals( biolist.getUID(), 
                      RecordableList.idOfListContainingBioObject(obj2.getUID()) );
        assertEquals( 0, 
                      RecordableList.positionOfBioObjectInList(obj2.getUID()) );
        assertFalse( RecordableList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testSubList() {
        fail("this method not implemented in BioList. It is complicated...");
    }
}
