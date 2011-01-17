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

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.Recorded;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedClass;
import net.bioclipse.core.api.managers.PublishedMethod;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseUIJob;

@PublishedClass(
    value="A Manager for scraping web pages"
)
public interface IBrowserManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
      params="String url, String content",
        methodSummary=
            "Scrapes a web URL by content"
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
