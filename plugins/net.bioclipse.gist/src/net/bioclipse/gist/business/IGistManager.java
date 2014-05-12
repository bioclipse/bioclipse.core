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

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;

@PublishedClass("The gist manager is used for downloading gists. A Gist is " +
		"a simple way to share small texts like Bioclipse scripts and " +
		"get verson control on them. For more information see: " +
		"https://gist.github.com/")
@TestClasses(
    "net.bioclipse.gist.test.APITest," +
    "net.bioclipse.gist.test.CoverageTest," +
    "net.bioclipse.gist.test.JavaGistManagerPluginTest"
)
public interface IGistManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "int gist",
        methodSummary = "Downloads the Gist with the given number to the " +
                        "project 'Gists/' and returns the path"
    )
    public List<IFile> download(int gist);
    public BioclipseJob<IFile> download( int gist, BioclipseJobUpdateHook<List<IFile>> hook );

    @Recorded
    @PublishedMethod(
        params = "String gist",
        methodSummary = "Downloads the Gist with the given identifier to the " +
                        "project 'Gists/' and returns the path"
    )
    public List<IFile> download(String gist);
    public BioclipseJob<IFile> download( String gist, BioclipseJobUpdateHook<List<IFile>> hook );
}
