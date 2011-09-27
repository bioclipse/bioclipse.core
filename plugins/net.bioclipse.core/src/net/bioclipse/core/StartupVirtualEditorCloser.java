/* ***************************************************************************
 * Copyright (c) 2008-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.core;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public class StartupVirtualEditorCloser implements IStartup {

    public void earlyStartup() {
        Job job = new UIJob("Close editors from") {
            @Override
            public IStatus runInUIThread( IProgressMonitor monitor ) {
                closeEditors();
                return Status.OK_STATUS;
            }
        };
        job.setRule( Activator.getVirtualProject() );
        job.schedule();
        try {
            //virtual.delete( true, true, null );
            Activator.createVirtualProject();
        } catch ( CoreException e ) {
            Logger.getLogger( StartupVirtualEditorCloser.class )
                    .warn( "Could not create virtual project" ,e);
        }
        
    }

    private void closeEditors() {

        IProject virtual = Activator.getVirtualProject();
        IWorkbench workbench = PlatformUI.getWorkbench();
        for ( IWorkbenchWindow workbWindow : workbench.getWorkbenchWindows() ) {
            for ( IWorkbenchPage workbPage : workbWindow.getPages() ) {
                Collection<IEditorReference> toRemove =
                                new HashSet<IEditorReference>();
                IEditorReference[] template = new IEditorReference[0];
                for ( IEditorReference ref : workbPage.getEditorReferences() ) {
                    try {
                        IFile fil = null;
                        fil=(IFile)ref.getEditorInput().getAdapter(IFile.class);
                        if(fil != null && fil.getProject().equals( virtual)) {
                            // marks this EditorReference to be removed
                            toRemove.add( ref );
                        }
                    } catch ( PartInitException x ) {
                        // can't check if it is a Virtual file,
                        // continue with next one
                    }
                }
                final IEditorReference[] remove = toRemove.toArray( template );
                final IWorkbenchPage workbenchPage = workbPage;
                workbenchPage.closeEditors( remove, false );
            }
        }
    }

}
