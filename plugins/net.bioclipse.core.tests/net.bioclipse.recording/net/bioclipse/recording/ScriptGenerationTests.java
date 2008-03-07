package net.bioclipse.recording;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.BioObjectList;
import net.bioclipse.core.domain.IBioObject;

import org.junit.Test;

/**
 * 
 * @author jonalv, masak
 */
public class ScriptGenerationTests {

	public static class TestObject extends BioObject {
		public Object getParsedResource() {
			return null;
		}
	}
	
	private void generateAndCompare( MethodRecord[] records,
			                         String[] expectedResults ) {
		
		IScriptGenerator generator = new JsScriptGenerator();
		String[] results = generator.generateScript(records);
		
		for (int i = 0; i < expectedResults.length; i++)
			assertEquals( expectedResults[i], results[i] );
	}
	
	@Test
	public void testBasicScriptGeneration() {
		
		generateAndCompare(
            new MethodRecord[] {
                new ManagerObjectRecord( "methodName", 
                                         "objectName", 
                                         new Object[] {""}, 
                                         null ), 
                new ManagerObjectRecord( "methodName", 
                		                 "objectName", 
                		                 new Object[] {"x"}, 
                		                 null ) 
            },
		
            new String[] {
				"objectName.methodName( \"\" )",
				"objectName.methodName( \"x\" )",
			}
		);
	}
	
	@Test
	public void testSimpleReferenceGeneration() {
		
		TestObject referencedObject = new TestObject();

		String refString = toVariableCase(
				referencedObject.getClass().getSimpleName()
		) + "1"; 

		generateAndCompare(
	            new MethodRecord[] {
	    				new ManagerObjectRecord( "methodName", 
	    						                 "objectName", 
	    						                 new Object[] {""}, 
	    						                 referencedObject ), 
				        new ManagerObjectRecord( "methodName", 
				                                 "objectName", 
				                                 new Object[] {
				        		                     referencedObject
				        		                 }, 
				        		                 null ) 
	            },
			
	            new String[] {
	       			 refString + " = objectName.methodName( \"\" )",
	     			"objectName.methodName( " + refString + " )",
				}
		);
	}
	
	private static String toVariableCase(String name) {
		return Character.toLowerCase( name.charAt(0) ) 
			   + name.substring(1);
	}

	@Test
	public void testCharacterEscaping() {
		
		generateAndCompare(
	            new MethodRecord[] {
	            		new ManagerObjectRecord( "method",
	            				                 "object",
	            				                 new Object[] {"a\nb"},
	            				                 null ), 
	            },
			
	            new String[] {
	    				"object.method( \"a\\nb\" )",
	            		// and _not_ \n, that's the point
				}
		);
	}
	
	@Test
	public void testScriptGenerationOnBioObjectListReturningMethod() {
		
		BioObjectList<IBioObject>
			returnValue1 = new BioObjectList<IBioObject>(),
			returnValue2 = new BioObjectList<IBioObject>();
		
		generateAndCompare(
	            new MethodRecord[] {
    				new ManagerObjectRecord( "method",
			                                 "object", 
			                                 new Object[] {}, 
			                                 returnValue1 ),
			        new ManagerObjectRecord( "method",
			        		                 "object", 
			        		                 new Object[] {}, 
			        		                 returnValue2 ),
			        new ManagerObjectRecord( "method",
			        		                 "object", 
			        		                 new Object[] { returnValue1,
			        		                                returnValue2 }, 
			        		                 null ),
		        },
			
	            new String[] {
	    			"bioObjectList1 = object.method()",
	    			"bioObjectList2 = object.method()",
				}
		);
	}
	
	private String firstToLowerCase(String s) {
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	@Test
	public void testBioObjectRecordScriptGeneration() {
		
		TestObject testObject = new TestObject();
		List<MethodRecord> methodRecords = new ArrayList<MethodRecord>();
		List<String> expectedStrings = new ArrayList<String>();
		
		methodRecords.add( new ManagerObjectRecord( "method",
				                                    "manager",
				                                    new Object[0],
				                                    testObject) );
		expectedStrings.add( "testObject1 = manager.method()" );
		
		for( Object returnObject : new Object[] { new String("return string"),
				                                  new Double(34.0),
				                                  new Integer(34),
				                                  new Short( (short)34 ),
				                                  new Long(34),
				                                  new Float(34.0),
				                                  new Boolean(false),
				                                  new Byte( (byte)34 ) } ) {
			
			methodRecords.add( new BioObjectRecord( "method", 
						                            testObject.getId(), 
						                            new Object[] {},
						                            returnObject ) );
			
			expectedStrings.add( firstToLowerCase(returnObject
						                          .getClass()
						                          .getSimpleName()) 
						                          +"1 = testObject1.method()" ); 
		}
		generateAndCompare( methodRecords.toArray(new MethodRecord[0]),
				            expectedStrings.toArray(new String[0]) );
	}
	
	@Test
	public void testObjectInABioObjectListRecording() {
		
		BioObjectList<IBioObject> returnedList 
			= new BioObjectList<IBioObject>();
		
		returnedList.add( new TestObject() );
		TestObject theTestObject = new TestObject(); 
		returnedList.add( theTestObject );
		
		generateAndCompare(
	            new MethodRecord[] {
    				new ManagerObjectRecord( "method",
			                                 "object", 
			                                 new Object[] {}, 
			                                 returnedList ),
			        new BioObjectRecord( "get",
					                     returnedList.getId(),
					                     new Object[] { new Integer(1) },
			                             theTestObject ),
			        new BioObjectRecord( "getParsedResource",
			        		             theTestObject.getId(),
			        		             new Object[] {},
			        		             "returnValue" ),
	            },
	            
	            new String[] {
	    			"bioObjectList1 = object.method()",
	    			"testObject1 = bioObjectList1.get( 1 )",
	    			"string1 = testObject1.getParsedResource()"
				}
		);
	}
}
