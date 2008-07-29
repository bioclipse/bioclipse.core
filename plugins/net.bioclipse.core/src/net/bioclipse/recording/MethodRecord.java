 /*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Ola Spjuth
 *     
 ******************************************************************************/

package net.bioclipse.recording;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IBioObject;

/**
 * @author jonalv, masak, ola
 *
 */
public abstract class MethodRecord implements IRecord {

    static abstract class Parameter {

        String  type;
        
        Parameter(String type) {
            this.type = type;
        }
    }
    
    static class NonBioObjectParameter extends Parameter {
        
        String stringRepresentation;
        
        NonBioObjectParameter(String type, String stringrepresentation) {
            super(type);
            this.stringRepresentation = stringrepresentation;
        }
    }
    
    static class BioObjectParameter extends Parameter {
        
        String id;
        
        BioObjectParameter(String type, String id) {
            super(type);
            this.id = id;
        }
    }

    protected String methodName;
    protected String returnObjectId;
    protected String returnType;
    protected String[] returnedListContentsIds; 
    
    protected List<Parameter> paramaters;
    
    @SuppressWarnings("unchecked")
    public MethodRecord( String methodName, 
                         Object[] parameters, 
                         Object returnValue ) {
        super();
        this.methodName = methodName;

        /*
         * Parameters
         */
        List<Parameter> params = new ArrayList<Parameter>();
        for(Object p : parameters) {
            if( p == null ) {
                params.add( new NonBioObjectParameter( "", "null") );
            }
            else if( p instanceof IBioObject ) {
                IBioObject bioObject = (IBioObject)p;
                String name = toVariableCase(
                        bioObject.getClass().getSimpleName()
                );
                params.add( new BioObjectParameter( name, bioObject.getUID() ) );
            }
            else if( p instanceof String) {
                String s = escapeNonPrintableCharacters( (String)p );
                params.add( new NonBioObjectParameter( "String",
                                                       "\"" + s + "\"" ) );
            }
            else {
                params.add( new NonBioObjectParameter( p.getClass()
                                                        .getSimpleName(), 
                                                       p.toString() ) );
            }
        }
        paramaters = params;
        
        /*
         * Return object id
         */
        if(returnValue instanceof IBioObject) {
            returnObjectId = ((IBioObject)returnValue).getUID();
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
        if (returnValue instanceof BioList) {
            BioList<IBioObject> returnedList
                = (BioList<IBioObject>) returnValue;
            returnedListContentsIds = new String[ returnedList.size() ];
            
            int i = 0;
            for ( IBioObject bioObject : returnedList )
                returnedListContentsIds[i++] = bioObject.getUID();
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

    public List<Parameter> getParameters() {
        return paramaters;
    }
    
    public String[] getReturnedListContentsIds() {
        if ( !returnType.equals("BioList") )
            throw new IllegalStateException("Can only return element ids "
                    + "of a BioList");
        
        return returnedListContentsIds;
    }
}
