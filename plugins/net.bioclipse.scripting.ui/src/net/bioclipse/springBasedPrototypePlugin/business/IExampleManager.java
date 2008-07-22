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
package net.bioclipse.springBasedPrototypePlugin.business;

import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;

/**
 * Example manager interface defining all methods of the service object
 * 
 * @author jonalv
 *
 */
public interface IExampleManager extends IBioclipseManager {

    /**
     * example method
     */
    @Recorded
    public void example();
}
