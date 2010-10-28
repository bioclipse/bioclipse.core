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
package net.bioclipse.browser.tests;

import static org.junit.Assert.*;

import java.util.List;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.browser.business.business.IBrowserManager;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ola
 *
 */
public abstract class AbstractBrowserManagerPluginTest {

    protected final static String pubchem_omeprazole_page =
       "http://www.ncbi.nlm.nih.gov/sites/entrez?db=pccompound&term=omeprazole";

    protected static IBrowserManager browser;

    @Test
    @Ignore("Doesn't test in an independent way. Depends on the state of a " +
    		"webpage ouf of our control")
    public void testScrapePubchemPageSync() throws BioclipseException {

        List<? extends IBioObject> mols = browser.scrapeWebpage(
                                                       pubchem_omeprazole_page);

        System.out.println("Scraped " + mols.size() + " mols from page.");
        //We don't know how many it is, could vary over time
        assertTrue( "Expected > 3, was: " + mols.size(), mols.size()>3 );
    }
}
