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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Manager to process HTML files with JSoup (MIT license)."
)
public interface IJSoupManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
    	params="String htmlString",
        methodSummary="Parses a HTML String into a JSoup document"
    )
    public Document parseString(String htmlString);

    @Recorded
    @PublishedMethod(
    	params="File htmlFile",
        methodSummary="Parses a HTML file into a JSoup document"
    )
    public Document parse(String htmlFile) throws BioclipseException;
    
    @Recorded
    @PublishedMethod(
    	params="Document doc, String cssSelector",
        methodSummary="Extracts elements from the JSoup Element (e.g. a Document) using the CSS seletor."
    )
    public Elements select(Element doc, String cssSelector);
    
}
