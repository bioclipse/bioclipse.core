/*******************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
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
import org.eclipse.swt.layout.GridLayout;

public class UpdateSitesEditDialog extends TitleAreaDialog{

	private String[] updateSites = new String[2];
	private Text txtName;
	private Text txtUrl;
	private String name;
	private String url;


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
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				final Label lblName = new Label(container, SWT.NONE);
				lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblName.setText("Name:");
				
				txtName = new Text(container, SWT.BORDER);
				GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
				gridData_1.widthHint = 482;
				txtName.setLayoutData(gridData_1);
				txtName.setText(name);
		
				final Label lblURL = new Label(container, SWT.NONE);
				lblURL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblURL.setText("URL:");
		
		txtUrl = new Text(container, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.widthHint = 460;
		txtUrl.setLayoutData(gridData);
		txtUrl.setText(url);

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