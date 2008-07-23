package net.bioclipse.ui.prefs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class UpdateSitesEditDialog extends TitleAreaDialog{

	private String[] updateSites = new String[2];
	private Text txtName;
	private Text txtUrl;
	private String name;
	private String url;


	public UpdateSitesEditDialog(Shell parentShell) {
		this(parentShell,"","");
	}

	public UpdateSitesEditDialog(Shell shell, String name, String url) {
		super(shell);
		this.name=name;
		this.url=url;
	}

	protected Control createDialogArea(Composite parent) {

		setTitle("Update Site");
		setMessage("Enter details for the Update Site. \nURL should start with http:// or file:// and end without /");

		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		txtName = new Text(container, SWT.BORDER);
		txtName.setBounds(20, 30, 180, 30);
		txtName.setText(name);
		
		txtUrl = new Text(container, SWT.BORDER);
		txtUrl.setBounds(235, 30, 350, 30);
		txtUrl.setText(url);
		
		final Label lblName = new Label(container, SWT.NONE);
		lblName.setBounds(20, 10, 185, 20);
		lblName.setText("Name");

		final Label lblFileExtension = new Label(container, SWT.NONE);
		lblFileExtension.setBounds(235, 10, 220, 20);
		lblFileExtension.setText("URL");

		return area;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {

			if (txtName.getText().length()<=0){
				showMessage("Name cannot be empty");
				return;
			}
			if (txtUrl.getText().length()<=0){
				showMessage("URL cannot be empty");
				return;
			}
			
			updateSites[0]=txtName.getText();
			updateSites[1]=txtUrl.getText();
			okPressed();
			return;
		}
		super.buttonPressed(buttonId);
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Update Site",
				message);
	}

	public String[] getUpdateSites() {
		return updateSites;
	}

	public void setUpdateSites(String[] updateSites) {
		this.updateSites = updateSites;
	}

	public Text getTxtFileExtension() {
		return txtUrl;
	}

	public void setTxtFileExtension(String filext) {
		this.txtUrl.setText(filext);
	}

	public Text getTxtName() {
		return txtName;
	}

	public void setTxtName(String name) {
		this.txtName.setText(name);
	}

}