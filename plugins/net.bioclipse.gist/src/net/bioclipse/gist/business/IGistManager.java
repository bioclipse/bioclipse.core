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

import java.io.IOException;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IGistManager extends IBioclipseManager {

    public final static String GIST_PROJECT = "Gists";

    @Recorded
    @PublishedMethod(
        params = "int gist, String path to save the Gist too", 
        methodSummary = "Downloads the Gist with the given number to the given " +
            "path and echos the target path"
    )
    public String download(int gist, String target)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    public String download(int gist, IFile target, IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "int gist",
        methodSummary = "Downloads the Gist with the given number to the" +
            "project 'Gists/'"
    )
    public String download(int gist)
        throws IOException, BioclipseException, CoreException;

    @Recorded
    public String download(int gist, IProgressMonitor monitor )
        throws IOException, BioclipseException, CoreException;

}
