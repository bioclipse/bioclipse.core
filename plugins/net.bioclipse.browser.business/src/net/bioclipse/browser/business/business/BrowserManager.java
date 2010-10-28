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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.browser.business.Activator;
import net.bioclipse.browser.business.ScraperUtils;
import net.bioclipse.browser.scraper.IBrowserScraper;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.jobs.IReturner;
import net.bioclipse.core.api.managers.IBioclipseManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class BrowserManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(BrowserManager.class);

    List<IBrowserScraper> scrapers;
    
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "browser";
    }
    
    public void scrapeWebpage(String url,
                              IReturner<List<? extends IBioObject>> returner,
                              IProgressMonitor monitor) 
    throws BioclipseException {
        
        if (url==null ||url.length()<=0)
            throw new BioclipseException( "URL cannot be empty.");

        //Read from URL
        BufferedReader r;
        StringBuffer buffer=new StringBuffer();
        try {
            r = new BufferedReader(new InputStreamReader(
                                   new URL(url).openStream()));

            String line=r.readLine();
            while ( line!=null ) {
                buffer.append( line );
                line=r.readLine();
            }
            r.close();

            System.out.println("Read from URL: \n" + buffer.toString());

        } catch ( Exception e ) {
            throw new BioclipseException( "Error reading url: "+e.getMessage());
        }

        if (scrapers==null)
            scrapers=ScraperUtils.readScrapersFromEP();
        if (scrapers==null)
            throw new BioclipseException( "No scrapers found." );
        
        for (IBrowserScraper scraper : scrapers){
             List<? extends IBioObject> list = scraper.extractObjects(url,buffer.toString() );
             if (list!=null)
             returner.partialReturn( list );
        }

    }

    /**
     * Scrape a String
     * @param url
     * @param content
     * @return
     * @throws BioclipseException
     */
    public List<? extends IBioObject> scrape(String url, String content) 
    throws BioclipseException {
    
        if (scrapers==null)
            scrapers=ScraperUtils.readScrapersFromEP();
        if (scrapers==null)
            return null;
        
        List<IBioObject> retlist=new ArrayList<IBioObject>();
        for (IBrowserScraper scraper : scrapers){
            List<? extends IBioObject> objs = scraper.extractObjects(url, content );
            if (objs!=null)
            retlist.addAll( objs );
        }
        
        return retlist;
    }

}
