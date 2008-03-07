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

package net.bioclipse.recording;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.domain.BioObjectList;
import net.bioclipse.core.domain.IBioObject;

/**
 * @author jonalv, masak
 *
 */
public abstract class MethodRecord implements IRecord {

	static abstract class Paramater {

		String  type;
		
		Paramater(String type) {
			this.type = type;
		}
	}
	
	static class NonBioObjectParamater extends Paramater {
		
		String stringRepresentation;
		
		NonBioObjectParamater(String type, String stringrepresentation) {
			super(type);
			this.stringRepresentation = stringrepresentation;
		}
	}
	
	static class BioObjectParamater extends Paramater {
		
		String id;
		
		BioObjectParamater(String type, String id) {
			super(type);
			this.id = id;
		}
	}

	protected String methodName;
	protected String returnObjectId;
	protected String returnType;
	protected String[] returnedListContentsIds; 
	
	protected List<Paramater> paramaters;
	
	@SuppressWarnings("unchecked")
	public MethodRecord( String methodName, 
			             Object[] parameters, 
			             Object returnValue ) {
		super();
		this.methodName = methodName;

		/*
		 * Parameters
		 */
		List<Paramater> params = new ArrayList<Paramater>();
		for(Object p : parameters) {
			if( p == null ) {
				params.add( new NonBioObjectParamater( "", "null") );
			}
			else if( p instanceof IBioObject ) {
				IBioObject bioObject = (IBioObject)p;
				String name = toVariableCase(
						bioObject.getClass().getSimpleName()
				);
				params.add( new BioObjectParamater( name, bioObject.getId() ) );
			}
			else if( p instanceof String) {
				String s = escapeNonPrintableCharacters( (String)p );
				params.add( new NonBioObjectParamater( "String",
						                               "\"" + s + "\"" ) );
			}
			else {
				params.add( new NonBioObjectParamater( p.getClass()
						                                .getSimpleName(), 
						                               p.toString() ) );
			}
		}
		paramaters = params;
		
		/*
		 * Return object id
		 */
		if(returnValue instanceof IBioObject) {
			returnObjectId = ((IBioObject)returnValue).getId();
		}
		else {
			returnObjectId = "";
		}
		
		/*
		 * Return type
		 */
		returnType = ( null == returnValue
					   ? "Void"
				       : returnValue.getClass().getSimpleName() );
		 
		int index = returnType.indexOf("$$EnhancerByCGLIB$$");
		if( index != -1) {
			returnType = returnType.substring(0, index);
		}

		/*
		 * List contents IDs
		 */
		if (returnValue instanceof BioObjectList) {
			BioObjectList<IBioObject> returnedList
				= (BioObjectList<IBioObject>) returnValue;
			returnedListContentsIds = new String[ returnedList.size() ];
			
			int i = 0;
			for ( IBioObject bioObject : returnedList )
				returnedListContentsIds[i++] = bioObject.getId();
		}
	}

	private static String toVariableCase(String name) {
		return Character.toLowerCase( name.charAt(0) ) 
		   + name.substring(1);
	}

	private static String escapeNonPrintableCharacters(String string) {
		return string.replaceAll("\\t", "\\\\t")
		             .replaceAll("\\n", "\\\\n");
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getReturnObjectId() {
		return returnObjectId;
	}

	public String getReturnType() {
		return returnType;
	}

	public List<Paramater> getParamaters() {
		return paramaters;
	}
	
	public String[] getReturnedListContentsIds() {
		if ( !returnType.equals("BioObjectList") )
			throw new IllegalStateException("Can only return element ids "
					+ "of a BioObjectList");
		
		return returnedListContentsIds;
	}
}
