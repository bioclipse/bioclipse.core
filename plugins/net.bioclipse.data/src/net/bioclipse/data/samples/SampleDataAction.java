package net.bioclipse.data.samples;

import java.util.Properties;
import net.bioclipse.data.wizards.NewDataProjectWizard;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
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

				NewDataProjectWizard wizard = new NewDataProjectWizard();
			    wizard.init(PlatformUI.getWorkbench(), null);

			      // Create the wizard dialog
			      WizardDialog dialog = new WizardDialog
			         (PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			        		 getShell(),wizard);

			      // Open the wizard dialog
			      dialog.open();
			      logger.debug("Installation of sampledata finished");
			      
			        MessageDialog.openInformation(
			        		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			        		 getShell(),
			                "Sample Data Installation",
			                "Installation of sample data was successful");


			}
		};

		Shell currentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		currentShell.getDisplay().asyncExec(r);
	}

}
