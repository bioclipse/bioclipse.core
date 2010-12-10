/*******************************************************************************
 * Copyright (c) 2010  Ola Spjuth <ola@bioclipse.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.browser.tests;

import java.util.List;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;

import org.junit.BeforeClass;
import org.junit.Test;

public class JavaBrowserManagerPluginTest
    extends AbstractBrowserManagerPluginTest {

    @BeforeClass public static void setup() {
        browser = net.bioclipse.browser.business.Activator.getDefault()
            .getJavaBrowserManager();
    }

    @Test public void testScrapePubchemPagePartialJob()
    throws BioclipseException, InterruptedException {

        BioclipseJob<List<? extends IBioObject>> job =
                                 browser.scrapeWebpage( pubchem_omeprazole_page,
                                 new BioclipseJobUpdateHook<
                                 List<? extends IBioObject>>("Scraping"){

                  public void partialReturn( List<? extends IBioObject> mols ) {

                      System.out.println("New scrape of size: " + mols.size());
                      //I guess we should assert something here..
                      int a=0; a++;
                  }
        });

        job.join();

    }
}
