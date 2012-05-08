/* *****************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.data.samples;

import java.util.Properties;

import net.bioclipse.data.wizards.NewDataProjectWizard;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

public class SampleDataAction extends Action implements IIntroAction {
	
  private static final Logger logger = Logger.getLogger(SampleDataAction.class);

	/**
	 *  Default constructor
	 */
	public SampleDataAction() {
	}

	/**
	 * Run action.
	 * Try to download and install sample feature if not present.
	 */
	public void run(IIntroSite site, Properties params) {
		
//		sampleId = params.getProperty("id"); //$NON-NLS-1$
//		if (sampleId == null)
//			return;
//
		Runnable r = new Runnable() {
			public void run() {
				
				//Install sample data project from this plugin

                NewDataProjectWizard wizard =
                                new NewDataProjectWizard( null, null );
			    wizard.init(PlatformUI.getWorkbench(), null);

			      // Create the wizard dialog
			      WizardDialog dialog = new WizardDialog
			         (PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			        		 getShell(),wizard);

			      // Open the wizard dialog
			      int ret=dialog.open();
			      if (ret==Window.CANCEL){
				      logger.debug("Installation of sampledata canceled.");
			      }else{
				      logger.debug("Installation of sampledata finished OK.");

				      MessageDialog.openInformation(
				        		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				        		 getShell(),
				                "Sample Data Installation",
				                "Installation of sample data was successful");
			      }

			}
		};

		Shell currentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		currentShell.getDisplay().asyncExec(r);
	}

}
