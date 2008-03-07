/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager.preferences;

import net.bioclipse.dialogs.CreateUserDialog;
import net.bioclipse.dialogs.EditUserDialog;
import net.bioclipse.dialogs.PassWordPromptDialog;
import net.bioclipse.usermanager.UserContainer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preferencepage for the UserContainer
 * 
 * @author jonalv
 *
 */
public class UserManagerPreferencePage extends PreferencePage 
                                    implements IWorkbenchPreferencePage {

	private Button     deleteButton;
	private Button     editButton;
	private Label      usersLabel;
	private ListViewer listViewer;
	private Button     createButton;
	private List       list;
	
	private UserContainer sandBoxUserManager;
	
	@Override
	protected Control createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.BORDER);
		container.setLayout(new FormLayout());

		listViewer = new ListViewer(container, SWT.BORDER | SWT.SINGLE);
		listViewer.setLabelProvider(new ListLabelProvider());
		listViewer.setContentProvider(new ContentProvider());
		listViewer.setInput( sandBoxUserManager.getUserNames() );
		if( sandBoxUserManager.getUserNames().size() > 0 ) {
			listViewer.getList().select(0);
		}
		list = listViewer.getList();
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(100, -42);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		list.setLayoutData(formData);

		usersLabel = new Label(container, SWT.NONE);
		formData.top = new FormAttachment(usersLabel, 5, SWT.BOTTOM);
		final FormData formData_1 = new FormData();
		formData_1.top = new FormAttachment(0, 5);
		formData_1.left = new FormAttachment(list, 0, SWT.LEFT);
		usersLabel.setLayoutData(formData_1);
		usersLabel.setText("Users:");

		createButton = new Button(container, SWT.NONE);
		createButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * CREATE NEW USER
			 */
			public void widgetSelected(SelectionEvent e) {
				CreateUserDialog createDialog = 
					new CreateUserDialog( PlatformUI
							              .getWorkbench()
							              .getActiveWorkbenchWindow()
							              .getShell(),
							              sandBoxUserManager );
				createDialog.open();
				if(createDialog.getReturnCode() == createDialog.OK) {
					EditUserDialog dialog = 
						new EditUserDialog( PlatformUI
								            .getWorkbench()
								            .getActiveWorkbenchWindow()
								            .getShell(), 
								            sandBoxUserManager );
					dialog.open();
				}
				updateListViewer();
			}
		});
		final FormData formData_2 = new FormData();
		formData_2.left = new FormAttachment(0, 6);
		formData_2.top = new FormAttachment(list, 5, SWT.BOTTOM);
		createButton.setLayoutData(formData_2);
		createButton.setText("Create...");

		editButton = new Button(container, SWT.NONE);
		editButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * EDIT USER
			 */
			public void widgetSelected(SelectionEvent e) {
				
				String userName = getSelectedUserName();
				if(userName == null) {
					MessageDialog.openInformation( PlatformUI
							                       .getWorkbench()
							                       .getActiveWorkbenchWindow()
							                       .getShell(), 
							                       "No user selected", 
							                       "There is no user selected to edit" );
					return;
				}
				PassWordPromptDialog passDialog = 
					new PassWordPromptDialog( PlatformUI
							                  .getWorkbench()
							                  .getActiveWorkbenchWindow()
							                  .getShell(), 
							                  userName, 
							                  sandBoxUserManager );
				passDialog.open();
				if(passDialog.getReturnCode() == passDialog.OK) {
					EditUserDialog dialog = 
						new EditUserDialog( PlatformUI
								            .getWorkbench()
								            .getActiveWorkbenchWindow()
								            .getShell(), 
								            sandBoxUserManager );
					dialog.open();	
				}
			}		
		});
		final FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(createButton, 0, SWT.BOTTOM);
		formData_3.left = new FormAttachment(createButton, 5, SWT.DEFAULT);
		editButton.setLayoutData(formData_3);
		editButton.setText("Edit...");

		deleteButton = new Button(container, SWT.NONE);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * DELETE USER
			 */
			public void widgetSelected(SelectionEvent e) {
				
				String userName = getSelectedUserName();
				if ( MessageDialog.openQuestion( PlatformUI
						                         .getWorkbench()
						                         .getActiveWorkbenchWindow()
						                         .getShell(), 
						                         "Confirm removing " +
						                         	"of Keyring user", 
						                         "Really remove the user: " 
						                         	+ userName + "?" ) ) {
					sandBoxUserManager.deleteUser(userName);
				}
				updateListViewer();
			}
		});
		final FormData formData_4 = new FormData();
		formData_4.left = new FormAttachment(editButton, 5, SWT.DEFAULT);
		formData_4.top = new FormAttachment(createButton, -29, SWT.BOTTOM);
		deleteButton.setLayoutData(formData_4);
		deleteButton.setText("Delete");
		container.setTabList(new Control[] { createButton, 
				                             editButton, 
				                             deleteButton, 
				                             list, 
				                             usersLabel });
		//
		
		return container;
	}

	@Override
	public boolean performOk() {
		
		UserContainer.replaceWithSandBoxInstance(sandBoxUserManager);
        return super.performOk();
    }
	
	public void init(IWorkbench workbench) {
		
		sandBoxUserManager = UserContainer.getSandBoxInstance();
	}
	
	private String getSelectedUserName() {

		if( list.getSelection().length == 1) {
			return list.getSelection()[0];
		}
		return null;
	}
	
	private void updateListViewer() {
		listViewer.setInput( sandBoxUserManager.getUserNames() );
		listViewer.refresh();
	}
	
	/**
	 * LabelProvider for the Keyring users list
	 * 
	 * @author jonathan
	 *
	 */
	class ListLabelProvider extends LabelProvider {
		public String getText(Object element) {
			return element.toString();
		}
		public Image getImage(Object element) {
			return null;
		}
	}
	
	/**
	 * ContentProvider for the Keyring users list
	 * 
	 * @author jonathan
	 *
	 */
	class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return ( (java.util.List<String>)inputElement )
			          .toArray( new String[0] );
		}
		public void dispose() {
		}
		public void inputChanged( Viewer viewer, 
				                  Object oldInput, 
				                  Object newInput ) {
		}
	}
}