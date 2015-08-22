/*******************************************************************************
 * Copyright (c) 2015  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.jsoup.business.business;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.ui.business.UIManager;

public class JSoupManager implements IBioclipseManager {

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "jsoup";
    }

    public Document parseString(String htmlString, IProgressMonitor monitor) {
    	if (monitor == null) monitor = new NullProgressMonitor();

    	monitor.beginTask("Parsing HTML string...", 1);
    	Document doc = Jsoup.parse(htmlString);
    	monitor.worked(1);
    	return doc;
    };

    public Document parse(IFile htmlFile, IProgressMonitor monitor) throws BioclipseException {
    	if (monitor == null) monitor = new NullProgressMonitor();

    	monitor.beginTask("Parsing HTML file...", 1);
    	String htmlString = new UIManager().readFile(htmlFile);
    	Document doc = parseString(htmlString, monitor);
    	monitor.worked(1);
    	return doc;
    };

    public Elements select(Element doc, String cssSelector, IProgressMonitor monitor) {
    	if (monitor == null) monitor = new NullProgressMonitor();

    	monitor.beginTask("Extracting elements...", 1);
    	Elements results = doc.select(cssSelector);
    	monitor.worked(1);
    	return results;    	
    }
}
