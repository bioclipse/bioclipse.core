/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.recording.MethodRecord.BioObjectParameter;
import net.bioclipse.recording.MethodRecord.NonBioObjectParameter;
import net.bioclipse.recording.MethodRecord.Parameter;

/**
 * @author jonalv, masak
 *
 */
public class JsScriptGenerator implements IScriptGenerator {

    private Hashtable<String, Integer> refNumber
        = new Hashtable<String, Integer>();
    // (String id, String variableName) in variables hash table
    private Map<String, String> variables
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

    private boolean isPrimitive(String type) {
        if (type == null)
            return false;

        return type.equals("Boolean")
            || type.equals("Byte")
            || type.equals("Double")
            || type.equals("Float")
            || type.equals("Integer")
            || type.equals("Long")
            || type.equals("Short")
            || type.equals("String");
    }

    private String getVariableName(String type, String id) {
        if( variables.containsKey(id) ) {
            return variables.get(id);
        }

        String newVariableName = getNewVariableName(type);
        if ( !isPrimitive(type) )
            variables.put(id, newVariableName);
        return newVariableName;
    }

    private String getVariableName(String bioObjectId) {
        if(variables.containsKey( bioObjectId) ) {
            return variables.get( bioObjectId );
        }
        else if( BioList.existsListContaining(bioObjectId) ){
            String id = BioList.idOfListContainingBioObject( bioObjectId );
            return variables.get(id)
                   + ".get( "
                   + BioList.positionOfBioObjectInList(bioObjectId)
                   + " )";
        }
        return "null";
    }

    public String[] generateScript( IRecord[] records ) {
        List<String> statements = new ArrayList<String>();

        List<IRecord> recordsList = Arrays.asList(records);

        int i = 0;
        for ( IRecord record : records )
            if( record instanceof MethodRecord) {
                statements.add( recordToJsStatement(
                        record,
                        recordsList.subList(++i, recordsList.size())) );
            }


        return statements.toArray( new String[records.length] );
    }

    private boolean isReferenced(String objectId, List<IRecord> rest) {

        for ( IRecord record : rest ) {
            if(!(record instanceof MethodRecord)) {
                continue;
            }
            MethodRecord r = (MethodRecord)record;
            if ( r instanceof BioObjectRecord
                && ((BioObjectRecord)r).bioObjectId.equals(objectId) )

                return true;

            for ( MethodRecord.Parameter p : r.paramaters )
                if ( p instanceof BioObjectParameter
                     && ((BioObjectParameter)p).id.equals(objectId) )

                    return true;

            List<String> ids = new ArrayList<String>();
            if ( r instanceof BioObjectRecord ) {
                ids.add( ( (BioObjectRecord)r ).bioObjectId );
            }
            for ( MethodRecord.Parameter p : r.paramaters ) {
                if( p instanceof BioObjectParameter) {
                    ids.add( ((BioObjectParameter)p).id );
                }
            }
            for(String id : ids) {
                if( BioList.existsListContaining(id) ) {
                    return true;
                }
            }
        }

        return false;
    }

    public String recordToJsStatement( IRecord record,
                                       List<IRecord> rest ) {

        if( record instanceof MethodRecord ) {

            MethodRecord r = (MethodRecord)record;

            List<String> paramStrings = new ArrayList<String>();
            for ( Parameter p : r.getParameters() ) {
                if (p instanceof BioObjectParameter) {
                    BioObjectParameter bp = (BioObjectParameter)p;
                    String variableName = getVariableName(bp.id);
                    paramStrings.add(variableName);
                }
                else if (p instanceof NonBioObjectParameter) {
                    NonBioObjectParameter nbp = (NonBioObjectParameter)p;
                    paramStrings.add(nbp.stringRepresentation);
                }
                else {
                    throw new IllegalStateException( "Unrecognized " +
                                                     "paramater type: " + p );
                }
            }
            StringBuilder statement = new StringBuilder();

            if ( !"".equals( r.returnObjectId )
                    && isReferenced(r.returnObjectId, rest)
                 || isPrimitive(r.returnType) ) {

                statement.append( getVariableName(r.returnType, r.returnObjectId) );
                statement.append( " = ");
            }

            if ( r instanceof ManagerObjectRecord ) {
                ManagerObjectRecord mor = (ManagerObjectRecord)r;
                statement.append( mor.getManagerObjectName() );
            }
            else if ( r instanceof BioObjectRecord ) {
                BioObjectRecord bor = (BioObjectRecord)r;
                String variableName = getVariableName(bor.bioObjectId);


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
        else if(record instanceof ScriptRecord) {
            return ((ScriptRecord) record).getScript(ScriptRecord.Language.JS);
        }
        else throw new IllegalArgumentException();
    }
}
