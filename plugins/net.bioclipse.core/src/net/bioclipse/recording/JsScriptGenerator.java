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
import java.util.Hashtable;
import java.util.List;

import net.bioclipse.recording.MethodRecord.BioObjectParamater;
import net.bioclipse.recording.MethodRecord.NonBioObjectParamater;
import net.bioclipse.recording.MethodRecord.Paramater;

/**
 * @author jonalv, masak
 *
 */
public class JsScriptGenerator implements IScriptGenerator {

	private Hashtable<String, Integer> refNumber
		= new Hashtable<String, Integer>();
	// (String id, String variableName) in variables hash table
	private Hashtable<String, String> variables
		= new Hashtable<String, String>();
	
	private void increaseRefNumber(String type) {
		if( refNumber.containsKey(type) ) {
			refNumber.put(type, refNumber.get(type) + 1);
		}
		else {
			refNumber.put(type, 1);
		}
	}
	
	private String firstToLowerCase(String s) {
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}
	
	private String getNewVariableName(String type) {
		increaseRefNumber(type);
		return firstToLowerCase(type) + refNumber.get(type);
	}
	
	private String getVariableName(String type, String id) {
		if( variables.contains(id) ) {
			return variables.get(id);
		}
		else {
			String newVariableName = getNewVariableName(type);
			variables.put(id, newVariableName);
			return newVariableName;
		}
	}
	
	public String[] generateScript( MethodRecord[] records ) {
		List<String> statements = new ArrayList<String>();
		
		for ( MethodRecord record : records )
			statements.add( recordToJsStatement(record) );

		return statements.toArray( new String[records.length] );
	}

	public String recordToJsStatement( MethodRecord r ) {
		List<String> paramStrings = new ArrayList<String>();
		for ( Paramater p : r.getParamaters() ) {
			if (p instanceof BioObjectParamater) {
				BioObjectParamater bp = (BioObjectParamater)p;
				String variableName = getVariableName(p.type, bp.id);
				paramStrings.add(variableName);
			}
			else if (p instanceof NonBioObjectParamater) {
				NonBioObjectParamater nbp = (NonBioObjectParamater)p;
				paramStrings.add(nbp.stringRepresentation);
			}
			else {
				throw new IllegalStateException( "Unrecognized " +
                                                 "paramater type: " + p );
			}
		}
		StringBuilder statement = new StringBuilder();
		
		if ( !"".equals( r.returnObjectId )
			 || r.returnType.equals("Boolean")
			 || r.returnType.equals("Byte") 
			 || r.returnType.equals("Double")
			 || r.returnType.equals("Float") 
			 || r.returnType.equals("Integer")
			 || r.returnType.equals("Long")
			 || r.returnType.equals("Short")
			 || r.returnType.equals("String") ) {
			
			statement.append( getNewVariableName(r.returnType) );
			statement.append( " = ");
		}
		
		if ( r instanceof ManagerObjectRecord ) {
			ManagerObjectRecord mor = (ManagerObjectRecord)r;
			statement.append( mor.getManagerObjectName() );
		}
		else if ( r instanceof BioObjectRecord ) {
			BioObjectRecord bor = (BioObjectRecord)r;

			String variableName = bor.bioObjectId;

			statement.append(variableName);
		}

		statement.append( "." );
		statement.append( r.getMethodName() );
		statement.append( '(' );
		
		if ( !paramStrings.isEmpty() )
			statement.append(' ');
			
		for (int j = 0; j < paramStrings.size(); j++) {
			statement.append( paramStrings.get(j) );
				
			if(j != paramStrings.size() - 1) {
				statement.append(", ");
			}
			else {
				statement.append(' ');
			}
		}
		statement.append(")");
		
		return statement.toString();
	}
}
