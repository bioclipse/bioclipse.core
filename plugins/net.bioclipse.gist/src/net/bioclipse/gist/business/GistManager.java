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
package net.bioclipse.gist.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GistManager implements IBioclipseManager {

	private static final Logger logger = Logger.getLogger(GistManager.class);

    public final static String GIST_PROJECT = "Gists";

    private class GistFile {
    	String rev;
    	String filename;
    	GistFile(String rev, String filename) {
    		this.rev = rev;
    		this.filename = filename;
    	}
    }

    public void download(int gist, IReturner<IFile> returner, IProgressMonitor monitor)
                  throws BioclipseException {
    	download(Integer.toString(gist), returner, monitor);
    }

    public void download(String gist, IReturner<IFile> returner, IProgressMonitor monitor)
                  throws BioclipseException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        SubMonitor subMonitor = SubMonitor.convert(monitor);
        subMonitor.beginTask( "Downloading Gist", 100 );
        
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(GIST_PROJECT);
        try {
            if ( !project.exists() ) {
                project.create(subMonitor.newChild(10));
            }
            project.open(subMonitor.newChild(10));
        } 
        catch ( CoreException e ) {
            throw new RuntimeException(
                          "Failed to prepare the Gists project", 
                          e );
        }
        
        monitor = subMonitor.newChild( 80 );
        monitor.beginTask("Downloading Gist...", 2);

        try {                
            monitor.subTask("Determining Gist revision");
            URL gistURL = new URL("https://gist.github.com/" + gist);
            URLConnection gistConn = gistURL.openConnection();
            
            String rawURLPattern = "/" + gist + "/raw/([0-9[a-f]]+)/([^\"]+)";
            Pattern p = Pattern.compile(rawURLPattern);

            // parse the HTML and extract the links to the raw files
            List<GistFile> files = new ArrayList<GistFile>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(gistConn.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                	String rev = m.group(1);
                	String filename = m.group(2);
                	logger.debug("Found file: " + filename);
                	System.out.println("Found file: " + filename);
                	files.add(new GistFile(rev, filename));
                }
                line = reader.readLine();
            }
            monitor.worked(1);
            
            if (monitor.isCanceled()) return;

            // download 
            if (files.size() > 0) {
            	for (GistFile file : files) {
                	String link = "https://gist.github.com/raw/" + gist + "/" + file.rev + "/" + file.filename;
            		monitor.subTask("Downloading Gist revision: " + link);
            		URL rawURL = new URL(link);
            		logger.debug("Downloading Gist as raw from URL: " + rawURL.toString());
            		System.out.println("Downloading Gist as raw from URL: " + link);
            		System.out.println("Downloading Gist as raw from URL: " + rawURL.toString());
            		URLConnection rawConn = rawURL.openConnection();
                    
                    IFile targetFile = ResourcePathTransformer.getInstance().transform(
                    	GIST_PROJECT + "/" + gist + "_" + file.filename
                    );
            		if (targetFile.exists()) {
            			targetFile.setContents(rawConn.getInputStream(), true, false, null);
            		} else {
            			targetFile.create(rawConn.getInputStream(), false, null);                
            		}
            		returner.partialReturn(targetFile);
            		if (monitor.isCanceled()) return;
            	}
            	monitor.worked(1);
            } else {
                Display.getDefault().syncExec(
                    new Runnable() {
                        public void run(){
                            MessageBox mb = new MessageBox(new Shell(), SWT.OK);
                            mb.setText("Gist Download Error");
                            mb.setMessage("Could not find the gist");
                            mb.open();
                        }
                    });
                monitor.done();
                return;
            }
        } catch (PatternSyntaxException exception) {
            exception.printStackTrace();
            throw new BioclipseException("Invalid Pattern.", exception);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            throw new BioclipseException("Invalid URL.", exception);
        } catch ( IOException e ) {
        	logger.error("Failed to download the gist", e);
            throw new BioclipseException("Failed to download the gist", e);
        } catch ( CoreException e ) {
        	logger.error("Failed to save the gist", e);
            throw new BioclipseException("Failed during saving of gist", e);
        }
        finally {
            monitor.done();
        }
        return;
    }

    public String getManagerName() {
        return "gist";
    }

}
