/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.core;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
public class StartupVirtualEditorCloser implements IStartup {
    public void earlyStartup() {    
        for(IWorkbenchWindow workbWindow:
                               PlatformUI.getWorkbench().getWorkbenchWindows()){
            for(IWorkbenchPage workbPage:workbWindow.getPages()){   
                Collection<IEditorReference> toRemove=
                                                new HashSet<IEditorReference>();
                 IEditorReference[] template=new IEditorReference[0];
                for(IEditorReference ef:workbPage.getEditorReferences()){
                    try{                     
                     IFile file=(IFile)
                                    ef.getEditorInput().getAdapter(IFile.class);
                     if(file!=null &&
                       file.getProject().equals(Activator.getVirtualProject())){
                         // marks this EditorReference to be removed
                         toRemove.add(ef);   
                     }                                           
                    }catch(PartInitException x){
//                        can't check if it is a Virtual file, 
//                        continue with next one
                    }
                }
                final IEditorReference[] remove=toRemove.toArray(template);
                final IWorkbenchPage workbenchPage=workbPage;
                Display.getDefault().asyncExec(new Runnable(){
                   public void run() {
                       workbenchPage.closeEditors(remove,false); 
                    } 
                });
            }
        }
    }
}
