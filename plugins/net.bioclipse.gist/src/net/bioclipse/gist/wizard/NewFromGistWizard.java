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
package net.bioclipse.gist.wizard;

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.gist.business.GistManager;
import net.bioclipse.jobs.IReturner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * Creates a resource from a Gist resource.
 * 
 * @author egonw
 */
public class NewFromGistWizard extends BasicNewResourceWizard {

	public static final String WIZARD_ID =
		"net.bioclipse.gist.wizards.NewFromGistWizard"; //$NON-NLS-1$
	
    private GistNumberWizardPage mainPage;
    
    private int gist = 0;

	/**
     * Creates a wizard for creating a new file resource in the workspace.
     */
    public NewFromGistWizard() {
        super();
    }

    public void addPages() {
        super.addPages();
        mainPage = new GistNumberWizardPage("newFilePage0", this);//$NON-NLS-1$
        mainPage.setTitle("Download Gist");
        mainPage.setDescription("Create a new resource from a downloaded Gist"); 
        addPage(mainPage);
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setWindowTitle("Download Gist");
        setNeedsProgressMonitor(true);
    }

    @Override
    public boolean canFinish() {
    	return mainPage.canFlipToNextPage();
    }
    
	class DownloadGistRunnable implements IRunnableWithProgress {
		
		public DownloadGistRunnable() {}
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			downloadGist(monitor);
		}
		
	    private void downloadGist(IProgressMonitor monitor) throws InvocationTargetException {
	        GistManager gistManager = new GistManager();

	        try {
	        	gistManager.download(getGist(), new IReturner<IFile>() {
					
					@Override
					public void partialReturn(IFile file) {
						show(file);
					}
					
					@Override
					public void completeReturn(IFile file) {
						show(file);
					}
					
					private void show(IFile file) {
				        selectAndReveal(file);

				        IWorkbenchWindow bench = getWorkbench().getActiveWorkbenchWindow();
				        try {
				            if (bench != null) {
				                IWorkbenchPage page = bench.getActivePage();
				                if (page != null) {
				                    IDE.openEditor(page, file, true);
				                }
				            }
				        } catch (PartInitException e) {
				        	new InvocationTargetException(new NullPointerException(), "Error while opening editor...");
				        }
					}
				}, monitor);
	        }  catch (BioclipseException exception) {
	        	throw new InvocationTargetException(exception, "Error while downloading gist...");
	        } 
	        
	    }

	}

	public boolean performFinish() {
        try {
    		getContainer().run(true, true, new DownloadGistRunnable());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
    	} catch (InterruptedException exception) {
    		return false; // return, but keep wizard
    	}
    	return true;
    }
    
    public int getGist() {
		return gist;
	}

	public void setGist(int gist) {
		this.gist = gist;
	}

}
