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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GistManager implements IBioclipseManager {

	private static final Logger logger = Logger.getLogger(GistManager.class);

    public final static String GIST_PROJECT = "Gists";
    
    private static String findUnusedFileName( IFolder currentFolder,
                                              String prefix, 
                                              String suffix ) {
        String fileName = prefix + suffix;
        IPath path = currentFolder.getFullPath();
        path = path.append( fileName );
        IFile file = currentFolder.getFile( path );
        int i=0;
        while(file.exists()) {
            i++;
            path = path.removeLastSegments( 1 );
            path = path.append( prefix+i+suffix );
            IPath copy = path.removeFirstSegments( 1 );
            file = currentFolder.getProject().getFile( copy );
        }
        fileName = path.lastSegment();
        return fileName;
    }

    private IFolder getProjectDirectory(IProgressMonitor monitor)
                    throws CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject project = root.getProject(GIST_PROJECT);
        if (!project.exists()) project.create(monitor);
        if (!project.isOpen()) project.open(monitor);

        return project.getFolder(GIST_PROJECT);
    }

    public IFile download(int gist, IProgressMonitor monitor)
                  throws BioclipseException {
        IFolder project;
        try {
            project = getProjectDirectory(monitor);
        } 
        catch ( CoreException e ) {
            throw new BioclipseException( "Could not find a project " +
            		                          "to save the gist in." );
        }
        String tName = findUnusedFileName(project, gist + ".0", ".js");
        tName = project.getFullPath().toString() + tName;
        IFile target = ResourcePathTransformer.getInstance().transform(tName);
        return download(gist, target, monitor);
    }

    public IFile download(int gist, IFile target, IProgressMonitor monitor)
                  throws BioclipseException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        if (target == null) {
            throw new BioclipseException("Cannot save to a NULL file.");
        }
        
        monitor.beginTask("Downloading Gist...", 2);

        try {                
            monitor.subTask("Determining Gist revision");
            URL gistURL = new URL("https://gist.github.com/" + gist);
            URLConnection gistConn = gistURL.openConnection();
            
            String rawURLPattern = "\"/raw/" + gist + "/([0-9[a-f]]+)/gistfile1.txt";
            Pattern p = Pattern.compile(rawURLPattern);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(gistConn.getInputStream()));
            String line = reader.readLine();
            String rev = null;
            while (line != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    rev = m.group(1);
                	logger.debug("Found revision: " + rev);
                    reader.close();
                    break;
                }
                line = reader.readLine();
            }
            monitor.worked(1);
            
            if (monitor.isCanceled()) {
                return null;
            }

            if (rev != null) {
                monitor.subTask("Downloading Gist revision: " + rev);
                URL rawURL = new URL(
                	"https://gist.github.com/raw/" + gist + "/" +
                	rev + "/gistfile1.txt"
                );
                logger.debug("Downloading Gist as raw from URL: " + rawURL.toString());
                URLConnection rawConn = rawURL.openConnection();
                if (target.exists()) {
                    target.setContents(rawConn.getInputStream(), true, false, null);
                } else {
                    target.create(rawConn.getInputStream(), false, null);                
                }
                monitor.worked(1);
            } else {
                Display.getDefault().syncExec(
                    new Runnable() {
                        public void run(){
                            MessageBox mb = new MessageBox(new Shell(), SWT.OK);
                            mb.setText("Gist Download Error");
                            mb.setMessage("Could not find Gist");
                            mb.open();
                        }
                    });
                monitor.done();
                return null;
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
        return target;
    }

    public String getManagerName() {
        return "gist";
    }

}
