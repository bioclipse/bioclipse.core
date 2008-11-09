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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;

/**
 * Example manager interface defining all methods of the service object
 * 
 * @author jonalv
 *
 */
@PublishedClass ( "This is an example manager with one example class." +
		          "Normally this text should contain some information of " +
		          "what the manager can be ued for" )
public interface IJChemPaintManager extends IBioclipseManager {

    /**
     * example method
     */
    @Recorded
    @PublishedMethod ( params = "String ex", 
                       methodSummary = "This is an example method. " +
                       		           "Normally this text should describe " +
                       		           "what the method does " )
    public void example(String ex);
}
