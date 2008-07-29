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
package net.bioclipse.core.business;

public class BioclipseException extends Exception {

    private static final long serialVersionUID = -9210522509375410904L;

    public BioclipseException(String message) {
        super(message);
    }
    
    public BioclipseException(String message, Throwable cause) {
        super(message, cause);
    }

}
