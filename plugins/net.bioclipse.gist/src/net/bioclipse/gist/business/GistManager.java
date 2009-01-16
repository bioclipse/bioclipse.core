/*******************************************************************************
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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GistManager implements IGistManager {

    public void load(int gist, String target) throws IOException, BioclipseException,
            CoreException {
        load(gist, ResourcePathTransformer.getInstance().transform(target), null);
    }

    public void load(int gist, IFile target, IProgressMonitor monitor)
            throws IOException, BioclipseException, CoreException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        if (target == null) {
            throw new BioclipseException("Cannot save to a NULL file.");
        }
        
        monitor.beginTask("Downloading Gist...", 2);

        try {                
            monitor.subTask("Determining Gist revision");
            URL gistURL = new URL("http://gist.github.com/" + gist);
            URLConnection gistConn = gistURL.openConnection();
            
            String rawURLPattern = "\"/raw/" + gist + "/([0-9[a-f]]+)";
            Pattern p = Pattern.compile(rawURLPattern);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(gistConn.getInputStream()));
            String line = reader.readLine();
            String rev = null;
            while (line != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    rev = m.group(1);
                    reader.close();
                    break;
                }
                line = reader.readLine();
            }
            monitor.worked(1);
            
            if (monitor.isCanceled()) {
                return;
            }

            if (rev != null) {
                monitor.subTask("Downloading Gist revision: " + rev);
                URL rawURL = new URL("http://gist.github.com/raw/" + gist + "/" + rev);
                URLConnection rawConn = rawURL.openConnection();
                if (target.exists()) {
                    target.setContents(rawConn.getInputStream(), true, false, null);
                } else {
                    target.create(rawConn.getInputStream(), false, null);                }
                
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
                return;
            }
        } catch (PatternSyntaxException e) {
            System.err.println ("Regex syntax error: " + e.getMessage());
            System.err.println ("Error description: " + e.getDescription());
            System.err.println ("Error index: " + e.getIndex());
            System.err.println ("Erroneous pattern: " + e.getPattern());
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            new InvocationTargetException(new NullPointerException(), "Invalid URL.");
        } catch (IOException exception) {
            exception.printStackTrace();
            new InvocationTargetException(new NullPointerException(), "Error while downloading Gist...");
        } catch (CoreException e1) {
            e1.printStackTrace();
            new InvocationTargetException(new NullPointerException(), "Error while downloading Gist...");
        }
        monitor.done();
    }

    public String getNamespace() {
        return "gist";
    }

}
