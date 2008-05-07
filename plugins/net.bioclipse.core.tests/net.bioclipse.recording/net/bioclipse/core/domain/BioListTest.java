package net.bioclipse.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BioListTest {

    private static class TestBioObject extends BioObject {}

    private BioList<IBioObject> biolist;
    private IBioObject          bioObject;
    private IBioObject          bioObject2;
    
    public BioListTest() {
        biolist   = new BioList<IBioObject>();
        bioObject = new TestBioObject();
        bioObject2 = new TestBioObject();
    }
    
    @Test
    public void testAddT() {
        biolist.add(bioObject);
        assertTrue( biolist.contains(bioObject) );
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(bioObject.getUID()) );
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
                      BioList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testAddAllCollectionOfQextendsT() {
        BioList<IBioObject> toBeAdded = new BioList<IBioObject>();
        toBeAdded.add(bioObject);
        biolist.addAll(toBeAdded);
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testAddAllIntCollectionOfQextendsT() {
        BioList<IBioObject> toBeAdded = new BioList<IBioObject>();
        biolist.add( new TestBioObject() );
        toBeAdded.add(bioObject);
        biolist.addAll(0, toBeAdded);
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testClear() {
        
        testAddT();
        BioList<IBioObject> aList = new BioList<IBioObject>();
        aList.add(bioObject);
        aList.clear();
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(bioObject.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(bioObject.getUID()) );
    }

    @Test
    public void testContains() {
        testAddT();
        assertTrue( biolist.contains(bioObject) );
    }

    @Test
    public void testContainsAll() {
        testAddT();
        BioList<IBioObject> anotherList = new BioList<IBioObject>();
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
        assertFalse( BioList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testRemoveInt() {
        testAddT();
        biolist.remove(0);
        assertFalse( BioList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testRemoveAll() {
        testAddT();
        BioList<IBioObject> list = new BioList<IBioObject>();
        list.add(bioObject);
        
        biolist.removeAll(list);
        assertEquals( BioList.idOfListContainingBioObject(bioObject.getUID()),
                      list.getUID() );
    }

    @Test
    public void testRetainAll() {
        IBioObject obj2 = new TestBioObject();
        biolist.add(obj2);
        
        BioList<IBioObject> list = new BioList<IBioObject>();
        list.add(obj2);
        
        biolist.retainAll(list);
        
        assertTrue(  biolist.contains(obj2)    );
        assertFalse( biolist.contains(biolist) );
        
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(obj2.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(obj2.getUID()) );
        assertFalse( BioList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testSet() {
        testAddT();
        IBioObject obj2 = new TestBioObject();
        biolist.set(0, obj2);
        assertEquals( biolist.getUID(), 
                      BioList.idOfListContainingBioObject(obj2.getUID()) );
        assertEquals( 0, 
                      BioList.positionOfBioObjectInList(obj2.getUID()) );
        assertFalse( BioList.existsListContaining(bioObject.getUID()) );
    }

    @Test
    public void testSubList() {
        fail("this method not implemented in BioList. It is complicated...");
    }
}
