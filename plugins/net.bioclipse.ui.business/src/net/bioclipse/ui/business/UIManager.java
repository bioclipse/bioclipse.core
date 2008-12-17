/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *     
 ******************************************************************************/
package net.bioclipse.ui.business;
import java.io.InputStream;
import net.bioclipse.core.ResourcePathTransformer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
/**
 * Contains general methods for interacting with the Bioclipse graphical
 * user interface (GUI).
 * 
 * @author masak
 */
public class UIManager implements IUIManager {
    public String getNamespace() {
        return "ui";
    }
    public void remove( IFile file ) {
        //TODO: jonalv use real progressmonitor
        try {
            file.delete(true, new NullProgressMonitor());
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }
    public void open( final IFile file ) {
        Display.getDefault().asyncExec(new Runnable() {    // do not use async, we need the GUI!
            public void run() {
                IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    IDE.openEditor(page, file);
                } catch (PartInitException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void remove( String filePath ) {
        remove(ResourcePathTransformer.getInstance().transform( filePath ));
    }
    public void open( String filePath ) {
        open(ResourcePathTransformer.getInstance().transform( filePath ));
    }
    public void save(String filePath, InputStream toWrite) {
        save(
            ResourcePathTransformer.getInstance().transform( filePath ),
            toWrite, null, null
        );
    }
    public void save(final IFile target, InputStream toWrite,
                     IProgressMonitor monitor, Runnable callbackFunction) {
        if (monitor == null) monitor = new NullProgressMonitor();
        try {
            int ticks = 10000;
            monitor.beginTask("Writing file", ticks);
            if (target.exists()) {
                target.setContents(toWrite, false, true, monitor);
            } else {
                target.create(toWrite, false, monitor);
            }
            monitor.worked(ticks);
        } catch (Exception exception) {
            throw new RuntimeException(
                "Error while saving to IFile", exception
            );
        } finally {
            monitor.done();
        }
        if (callbackFunction != null) {
            Display.getDefault().asyncExec(callbackFunction);
        }
    }
        public boolean fileExists(IFile file) {
                return file.exists();
        }
        public boolean fileExists(String filePath) {
                try {
                        return fileExists(ResourcePathTransformer.getInstance()
                                        .transform( filePath ));
                } catch (IllegalArgumentException exception) {
                        return false;
                }
        }
}
