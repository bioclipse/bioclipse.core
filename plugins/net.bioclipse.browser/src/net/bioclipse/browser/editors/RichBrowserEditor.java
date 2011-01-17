/* *****************************************************************************
 * Copyright (c) 2010  Ola Spjuth <ospjuth@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org/epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.browser.editors;

import java.util.List;

import net.bioclipse.browser.business.Activator;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 * @author ola
 *
 */
public class RichBrowserEditor extends EditorPart implements IBrowserViewerContainer {
    
    public static final String EDITOR_ID="net.bioclipse.browser.richbrowser";

    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init( IEditorSite site, IEditorInput input )
                                                            throws PartInitException {
        
        setSite( site );
        setInput( input );
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected BrowserViewer webBrowser;

    @Override
    public void createPartControl( Composite parent ) {
        
        System.out.println("weee");

        int style = BrowserViewer.LOCATION_BAR;
        style += BrowserViewer.BUTTON_BAR;

        webBrowser = new BrowserViewer(parent, style);
        
        webBrowser.getBrowser().addProgressListener(new ProgressListener() {

            public void completed(ProgressEvent event) {
                // just before body.onLoad
                // execute JS code here

                //Should fire a BG process
                try {
                    Activator.getDefault().getJavaBrowserManager().scrape( 
                         webBrowser.getBrowser().getUrl(),
                         webBrowser.getBrowser().getText());
                    
                    final String url=webBrowser.getBrowser().getUrl();

                    //We are processing a new page, make model aware of this
                    net.bioclipse.browser.Activator.getDefault()
                    .getScrapingModel().newPage( url);
                    
                    BioclipseJob<List<? extends IBioObject>> job = 
                        Activator.getDefault().getJavaBrowserManager()
                        .scrapeWebpage(url,
                                       new BioclipseJobUpdateHook<
                                       List<? extends IBioObject>>(
                                            "Extracting objects from web page"){
                 public void partialReturn( List<? extends IBioObject> objects ) {

                     System.out.println("New scrape of size: " + objects.size());
                     
                     net.bioclipse.browser.Activator.getDefault()
                     .getScrapingModel().addToModel(
                                    url, objects );
                     
                 }

                 });
                    
                    job.addJobChangeListener( new IJobChangeListener() {
                        
                        public void sleeping( IJobChangeEvent event ) {
                        }
                        
                        public void scheduled( IJobChangeEvent event ) {
                        }
                        
                        public void running( IJobChangeEvent event ) {
                        }
                        
                        public void done( IJobChangeEvent event ) {
                            //Mark page as complete
                            Display.getDefault().asyncExec( new Runnable() {
                                public void run() {
                                    net.bioclipse.browser.Activator.getDefault()
                                    .getScrapingModel().pageComplete(
                                              webBrowser.getBrowser().getUrl());
                                }
                            });

                        }
                        
                        public void awake( IJobChangeEvent event ) {
                        }
                        
                        public void aboutToRun( IJobChangeEvent event ) {
                        }
                    });

                } catch ( BioclipseException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            public void changed(ProgressEvent event) {
//                System.out.println("== CHANGED: " + event.current + "/" + event.total);
//                System.out.println("");
//                net.bioclipse.browser.Activator.getDefault().getScrapingModel()
//                    .pageChanged(webBrowser.getBrowser().getUrl());
                
            }
        });

    }

    @Override
    public void setFocus() {
    }

    public boolean close() {
        return false;
    }

    public IActionBars getActionBars() {
        return getEditorSite().getActionBars();
    }

    public void openInExternalBrowser( String url ) {
    }
   
    public void setURL( String url ) {
        webBrowser.setURL( url );
    }
    

}
