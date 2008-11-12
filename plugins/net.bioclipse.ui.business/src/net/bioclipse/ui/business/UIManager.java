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

import net.bioclipse.core.ResourcePathTransformer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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
}
