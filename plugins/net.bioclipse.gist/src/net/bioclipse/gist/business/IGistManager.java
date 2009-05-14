/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.gist.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
<<<<<<< HEAD:plugins/net.bioclipse.gist/src/net/bioclipse/gist/business/IGistManager.java
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.ui.jobs.BioclipseJob;
import net.bioclipse.ui.jobs.BioclipseJobUpdateHook;

import org.eclipse.core.resources.IFile;

@PublishedClass("Manager for downloading Gists")
=======
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;

@PublishedClass("The gist manager is used for downloading gists")
>>>>>>> Started to refactor the Gist manager. Ran into some problems with the manager design and changed that. (removed annotation for ManagerImplementation and had to wrap dispatchers in Advisors):plugins/net.bioclipse.gist/src/net/bioclipse/gist/business/IGistManager.java
public interface IGistManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "int gist, String path", 
        methodSummary = "Downloads the Gist with the given number to the " +
        		            "given path and returns the target path"
    )
    public String download( int gist, String path );

    public IFile download( int gist, 
                           IFile target );
    
    public BioclipseJob<IFile> download( int gist, 
                                         IFile target, 
                                         BioclipseJobUpdateHook hook );
    
    @Recorded
    @PublishedMethod(
        params = "int gist",
        methodSummary = "Downloads the Gist with the given number to the" +
                        "project 'Gists/' and returns the path"
    )
    public String download(int gist);

    public BioclipseJob<IFile> download( int gist, 
                                         BioclipseJobUpdateHook hook );
}
