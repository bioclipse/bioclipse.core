/* *****************************************************************************
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

import net.bioclipse.core.api.Recorded;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.core.api.jobs.IBioclipseJob;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedClass;
import net.bioclipse.core.api.managers.PublishedMethod;
import net.bioclipse.core.api.managers.TestClasses;

import org.eclipse.core.resources.IFile;


@PublishedClass("The gist manager is used for downloading gists")
@TestClasses(
    "net.bioclipse.gist.test.APITest," +
    "net.bioclipse.gist.test.CoverageTest," +
    "net.bioclipse.gist.test.JavaGistManagerPluginTest"
)
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
    
    public IBioclipseJob<IFile> download( int gist, 
                                         IFile target, 
                                         BioclipseJobUpdateHook hook );
    
    @Recorded
    @PublishedMethod(
        params = "int gist",
        methodSummary = "Downloads the Gist with the given number to the " +
                        "project 'Gists/' and returns the path"
    )
    public String download(int gist);

    public IBioclipseJob<IFile> download( int gist, 
                                         BioclipseJobUpdateHook hook );
}
