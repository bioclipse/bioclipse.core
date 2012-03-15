package net.bioclipse.usermanager;

import net.bioclipse.usermanager.dialogs.DialogArea;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class LoginWizardPage extends WizardPage {
	
	private DialogArea loginDialogArea;
	
	protected LoginWizardPage(String pageName, UserContainer userContainer) {
		super(pageName);
		this.loginDialogArea = new DialogArea(userContainer ,true);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = loginDialogArea.getLoginArea(parent);
		setControl(container);
		setErrorMessage(loginDialogArea.getErrorMessage());
	}

	@Override
	public boolean isPageComplete() {
		return loginDialogArea.getErrorFlag();
	}
	
}
