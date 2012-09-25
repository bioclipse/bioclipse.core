/*******************************************************************************
 * Copyright (c) 2010  Ola Spjuth <ola@bioclipse.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.browser.business.business;

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="A Manager for scraping web pages; methods for extracting " +
    		  "elements that Bioclipse can work with (i.e. molecules) from " +
    		  "web pages."
)
public interface IBrowserManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
      params="String url",
        methodSummary=
            "Scrapes a web URL by content and returns a List of IBioObjects"
    )
    public List<? extends IBioObject> scrapeWebpage(String url)
    throws BioclipseException;

    //For running in background and return partial results
    public BioclipseJob<List<? extends IBioObject>> scrapeWebpage(String url, 
                              BioclipseJobUpdateHook<List<? extends IBioObject>> hook) 
    throws BioclipseException;
    

    
    
    public List<? extends IBioObject> scrape(String url, String content) 
    throws BioclipseException;

}
