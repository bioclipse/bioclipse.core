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
package net.bioclipse.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass("The Bioclipse Platform manager is used for providing some " +
		"textual information on the Bioclipse platform.")
public interface IBioclipsePlatformManager extends IBioclipseManager {

	@PublishedMethod(methodSummary="Opens Planet Bioclipse.")
	public void planet();

	@PublishedMethod(methodSummary="Opens the Bioclipse Bug Tracker.")
	public void bugTracker();

    @PublishedMethod(
        params="String url",
        methodSummary="Download the URL content as String."
    )
    public String download(String url)
    throws BioclipseException;

    @PublishedMethod(
        params="String url, String filename",
        methodSummary="Download the URL content into a file."
    )
    public String downloadAsFile(String url, String filename)
    throws BioclipseException;

    @PublishedMethod(
        params="String url, String mimeType",
        methodSummary="Download the URL content as String while passing the " +
        		"given MIME type as Accept: HTTP header."
    )
    public String download(String url, String mimeType)
    throws BioclipseException;

    @PublishedMethod(
        params="String url, String mimeType, String filename",
        methodSummary="Download the URL content into a file while passing " +
        		"the given MIME type as Accept: HTTP header."
    )
    public String downloadAsFile(String url, String mimeType,
            String filename)
    throws BioclipseException;

    @PublishedMethod(
        methodSummary="Returns true if Bioclipse is connected to the internet."
    )
    public boolean isOnline();

    @PublishedMethod(
        methodSummary="Returns a string representation of the current " +
        		      "Bioclipse version")
    public String version();
}
