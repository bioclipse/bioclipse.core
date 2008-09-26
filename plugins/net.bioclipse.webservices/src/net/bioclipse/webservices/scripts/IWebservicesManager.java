 /*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *     Stefan Kuhn
 *
 ******************************************************************************/
package net.bioclipse.webservices.scripts;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;

@PublishedClass( "Contains webservices related methods")
public interface IWebservicesManager extends IBioclipseManager {

    /**
     * Fetches an entry from the EBI databases.
     *
     * @param path The id to load
     * @return loaded entry
     * @throws BioclipseException
     */
    @Recorded
    @PublishedMethod( params = "String pdbid", 
                      methodSummary = "Fetches an entry from the EBI databases")
    public String downloadPDB(String pdbid)
        throws BioclipseException;

}