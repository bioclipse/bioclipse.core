package net.bioclipse.usermanager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewAccountWizard extends Wizard implements INewWizard {

	private NewAccountWizardPage mainPage;
	//private IStructuredSelection initSelection;
	
	public NewAccountWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Add an account to Bioclipse");
        setNeedsProgressMonitor(false);
	}
	
    public void addPages() {
        super.addPages();
        mainPage = new NewAccountWizardPage("mainPage");
        mainPage.setTitle("New Account");
        mainPage.setDescription("Add an third-part account to Bioclipse");
        addPage(mainPage);
       // TODO Add the image via a relative path.	
        setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(null,
        		   "/Users/klasjonsson/Pictures/BioclipseAccountLogo1_medium.png"));
    }

	@Override
	public boolean performFinish() {
		// TODO Add logic for creating account
		return false;
	}

}
