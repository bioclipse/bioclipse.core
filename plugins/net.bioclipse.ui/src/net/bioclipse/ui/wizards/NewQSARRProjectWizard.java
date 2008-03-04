/*******************************************************************************
 * Copyright (c) 2005 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package net.bioclipse.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;


public class NewQSARRProjectWizard extends Wizard implements INewWizard {
    
	
	private WizardNewProjectCreationPage fFirstPage;
	private WizardNewProjectReferencePage fReferencePage;

	private IConfigurationElement fPerspConfig;
	
    public NewQSARRProjectWizard() {
    		super();
//        setDefaultPageImageDescriptor();
        setWindowTitle("New QSAR project"); 
    }
  
    public void addPages() {
    	
        super.addPages();
        fFirstPage = new WizardNewProjectCreationPage("New QSAR project");
        addPage(fFirstPage);
                
    }
    
    @Override
    public boolean performFinish() {
/* to compile, FIXME
    	// befor super, because eclipse-pages access ui
    	fNewRProject = new ProjectCreator(
    			fFirstPage.getProjectName(),
    			(fFirstPage.useDefaults()) ? null : fFirstPage.getLocationPath(),
    			(fReferencePage != null) ? fReferencePage.getReferencedProjects() : null,
    			fFirstPage.getSelectedWorkingSets()
    			) {
    		@Override
    		protected void doConfigProject(IProject project, IProgressMonitor monitor) throws CoreException {
    			RProject.addNature(fNewRProject.getProjectHandle(), monitor);
    		}
    	};
    	boolean result = super.performFinish();

    	IProject newProject = null;
        if (result && newProject != null) {
        	updatePerspective(fPerspConfig);
        	selectAndReveal(newProject);
        }
    	return result;
*/    	
    	return false;
    }
    
	protected void doFinish(IProgressMonitor monitor) throws InterruptedException, CoreException, InvocationTargetException {
    
		try {
			monitor.beginTask("Create new R project...", 1000); //$NON-NLS-1$
//			fNewRProject.createProject(new SubProgressMonitor(monitor, 1000) );
//		fFirstPage.saveSettings();
		}
		finally {
			monitor.done();
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}
    
}
