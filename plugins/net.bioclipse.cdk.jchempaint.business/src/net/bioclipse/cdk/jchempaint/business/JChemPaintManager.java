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
package net.bioclipse.cdk.jchempaint.business;

import net.bioclipse.ui.Activator;

/**
 * ExampleManager. All methods declared here that should be reachable 
 * in the service object must be declared in the corresponding interface  
 * 
 * @author jonalv
 *
 */
public class JChemPaintManager implements IJChemPaintManager {

    public void example(String ex) {
        Activator.getDefault().CONSOLE.echo(
            "JChemPaintManager.example() called with:" + ex ); 
    }

    public String getNamespace() {
        return "jcp";
    }
}
