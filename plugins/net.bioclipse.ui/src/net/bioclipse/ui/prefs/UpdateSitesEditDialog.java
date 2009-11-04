/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class UpdateSitesEditDialog extends TitleAreaDialog{

	private String[] updateSites = new String[2];
	private Text txtName;
	private Text txtUrl;
	private String name;
	private String url;
	private FormData formData_1;
	private FormData formData_2;
	private FormData formData_3;


	/**
	 * @wbp.parser.constructor
	 */
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
		container.setLayout(new FormLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

    formData_1 = new FormData();
		
		txtUrl = new Text(container, SWT.BORDER);
		formData_1.bottom = new FormAttachment(txtUrl, -4);
		{
		    formData_2 = new FormData();
		    formData_2.top = new FormAttachment(0, 33);
		    formData_2.right = new FormAttachment(100, -10);
		    txtUrl.setLayoutData(formData_2);
		}
		txtUrl.setText(url);
		
		final Label lblName = new Label(container, SWT.NONE);
		formData_1.left = new FormAttachment(lblName, 6);
		{
		    formData_3 = new FormData();
		    formData_3.left = new FormAttachment(0, 10);
		    formData_3.top = new FormAttachment(0, 20);
		    lblName.setLayoutData(formData_3);
		}
		lblName.setText("Name:");
		
		txtName = new Text(container, SWT.BORDER);
		formData_2.left = new FormAttachment(txtName, 0, SWT.LEFT);
        {
            formData_1.right = new FormAttachment(100, -10);
            formData_1.left = new FormAttachment(lblName, 6);
            formData_1.bottom = new FormAttachment(lblName, 0, SWT.BOTTOM);
            txtName.setLayoutData(formData_1);
        }
        txtName.setText(name);

		final Label lblURL = new Label(container, SWT.NONE);
		{
		    FormData formData = new FormData();
		    formData.bottom = new FormAttachment(txtUrl, 0, SWT.BOTTOM);
		    formData.left = new FormAttachment(lblName, 0, SWT.LEFT);
		    lblURL.setLayoutData(formData);
		}
		lblURL.setText("URL:");

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

	@Override
	protected boolean isResizable() {
	    return true;
	}
}