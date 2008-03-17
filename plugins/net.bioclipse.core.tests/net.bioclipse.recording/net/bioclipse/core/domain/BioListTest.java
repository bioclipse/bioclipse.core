package net.bioclipse.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class BioListTest {

	private static class TestBioObject extends BioObject {

		@Override
		public Object getParsedResource() {
			return "object";
		}
	}

	private BioList<IBioObject> biolist;
	private IBioObject          bioObject;
	private String              id;
	
	public BioListTest() {
		biolist   = new BioList<IBioObject>();
		bioObject = new TestBioObject();
		id        = bioObject.getId();
	}
	
	@Test
	public void testAddT() {
		biolist.add(bioObject);
		assertTrue( biolist.contains(bioObject) );
		assertEquals( biolist.getId(), 
				      BioList.idOfListContainingBioObject(bioObject.getId()) );
		assertEquals( 0, 
				      BioList.positionOfBioObjectInList(bioObject.getId()) );
	}

	@Test
	public void testAddIntT() {
		biolist.add( new TestBioObject() );
		biolist.add( 0,bioObject );
		assertEquals( biolist.getId(), 
			          BioList.idOfListContainingBioObject(bioObject.getId()) );
		assertEquals( 0, 
			          BioList.positionOfBioObjectInList(bioObject.getId()) );
	}

	@Test
	public void testAddAllCollectionOfQextendsT() {
		BioList<IBioObject> toBeAdded = new BioList<IBioObject>();
		toBeAdded.add(bioObject);
		biolist.addAll(toBeAdded);
		assertEquals( biolist.getId(), 
		              BioList.idOfListContainingBioObject(bioObject.getId()) );
	    assertEquals( 0, 
		              BioList.positionOfBioObjectInList(bioObject.getId()) );
	}

	@Test
	public void testAddAllIntCollectionOfQextendsT() {
		BioList<IBioObject> toBeAdded = new BioList<IBioObject>();
		biolist.add( new TestBioObject() );
		toBeAdded.add(bioObject);
		biolist.addAll(0, toBeAdded);
		assertEquals( biolist.getId(), 
		              BioList.idOfListContainingBioObject(bioObject.getId()) );
	    assertEquals( 0, 
		              BioList.positionOfBioObjectInList(bioObject.getId()) );
	}

	@Test
	public void testClear() {
		testAddT();
		biolist.clear();
		
	}

	@Test
	public void testContains() {
		fail("Not yet implemented");
	}

	@Test
	public void testContainsAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testIndexOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	@Test
	public void testIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testLastIndexOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testListIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testListIteratorInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetainAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		fail("Not yet implemented");
	}

	@Test
	public void testSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubList() {
		fail("Not yet implemented");
	}

	@Test
	public void testToArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testToArrayTArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetParsedResource() {
		fail("Not yet implemented");
	}

}
