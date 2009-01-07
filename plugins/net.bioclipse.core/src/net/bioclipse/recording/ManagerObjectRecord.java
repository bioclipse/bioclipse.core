/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.recording;

public class ManagerObjectRecord extends MethodRecord {

    protected String managerObjectName;
    
    public ManagerObjectRecord( String methodName, 
                                String managerObjectName,
                                Object[] parameters, 
                                Object returnValue ) {
        
        super(methodName, parameters, returnValue);
        this.managerObjectName = managerObjectName;
    }
    
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < paramaters.size(); i++) {
            Parameter p = paramaters.get(i);
            
            if( p instanceof NonBioObjectParameter ) {
                sb.append( ( (NonBioObjectParameter)p ).stringRepresentation );
            }
            else if( p instanceof BioObjectParameter) {
                sb.append(p.type);
            }
            else {
                throw new IllegalStateException( "Unrecognized " +
                                                 "paramater type: " + p ); 
            }
            
            if(i != paramaters.size() - 1) {
                sb.append(", ");
            }
            else {
                sb.append(' ');
            }
        }
        
        return managerObjectName + "." + methodName + "( "
            + sb.toString() + ")";
    }

    public String getManagerObjectName() {
        return managerObjectName;
    }

    public void setManagerObjectName(String managerObjectName) {
        this.managerObjectName = managerObjectName;
    }

}
