/*******************************************************************************
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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GistNumberWizardPage extends WizardPage {

	private NewFromGistWizard wizard;
	
	public GistNumberWizardPage(String pageName, NewFromGistWizard wizard) {
		super(pageName);
		this.wizard = wizard;
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		setControl(container);
		
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);
		label.setText("What Gist do you want to download?");
		
		Text gistNumberField = new Text(container, SWT.BORDER);
		gistNumberField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatePageComplete((Text)e.getSource());
			}
		});
		gistNumberField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void updatePageComplete(Text field) {
		setPageComplete(false);
		String gistNumber = field.getText();
		if (gistNumber.length() != 0) {
			try {
				int gist = Integer.parseInt(gistNumber);
				if (gist < 1) {
					setMessage(null);
					setErrorMessage("The Gist must be a non-zero, positive integer.");
					return;
				}
				wizard.setGist(gist);
			} catch (NumberFormatException exception) {
				setMessage(null);
				setErrorMessage("The given Gist must be an integer.");
				return;
			}
		}
		setPageComplete(true);
		setMessage(null);
		setErrorMessage(null);
	}
	
    public boolean canFlipToNextPage() {
    	return wizard.getGist() != 0;
    }
}

