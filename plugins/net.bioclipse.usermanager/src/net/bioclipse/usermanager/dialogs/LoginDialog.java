/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.usermanager.dialogs;

import java.util.ArrayList;
import java.util.Iterator;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.business.IUserManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Dialog for logging in to the given UserContainer
 * 
 * @author jonalv
 *
 */
public class LoginDialog extends TitleAreaDialog {
    
    private static final Logger logger 
        = Logger.getLogger(LoginDialog.class);

    private Button         createNewKeyringButton;
    private Label          usernameLabel;
    private Label          passwordLabel;
    private Text           usernameText;
    private Text           passwordText;
    private UserContainer  userContainer;
    private String         username;
    private String         password;
    private boolean        userContainerEdited;
    private String         errorMessage;
    /**
     * Create the dialog
     * @param parentShell
     */
    public LoginDialog( Shell parentShell, 
                                   UserContainer userContainer ) {
        super(parentShell);
        
        this.userContainer = userContainer;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceManager.getImage(LoginDialog.class, "/net/bioclipse/usermanager/BioclipseAccountLogo3_medium.png"));
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new FormLayout());
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        usernameLabel = new Label(container, SWT.NONE);
        final FormData formData = new FormData();
        usernameLabel.setLayoutData(formData);
        usernameLabel.setText("Username:");

        passwordLabel = new Label(container, SWT.NONE);
        final FormData formData_1 = new FormData();
        passwordLabel.setLayoutData(formData_1);
        passwordLabel.setText("Password:");

        passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_1.bottom = new FormAttachment(passwordText, 0, SWT.BOTTOM);
        formData_1.right = new FormAttachment(passwordText, -5, SWT.LEFT);
        final FormData formData_2 = new FormData();
        formData_2.top = new FormAttachment(0, 93);
        passwordText.setLayoutData(formData_2);

        usernameText = new Text(container, SWT.BORDER);
        formData.bottom = new FormAttachment(usernameText, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(usernameText, -5, SWT.LEFT);
        formData_2.right = new FormAttachment(usernameText, 0, SWT.RIGHT);
        formData_2.left = new FormAttachment(usernameText, 0, SWT.LEFT);
        final FormData formData_3 = new FormData();
        formData_3.left = new FormAttachment(0, 140);
        formData_3.right = new FormAttachment(100, -34);
        formData_3.top = new FormAttachment(0, 53);
        usernameText.setLayoutData(formData_3);

        createNewKeyringButton = new Button(container, SWT.NONE);
        createNewKeyringButton.addSelectionListener(new SelectionAdapter() {
            /*
             * CREATE NEW USER 
             */
            public void widgetSelected(SelectionEvent e) {

                CreateUserDialog createDialog = 
                    new CreateUserDialog( PlatformUI
                                          .getWorkbench()
                                          .getActiveWorkbenchWindow()
                                          .getShell(),
                                          userContainer );
                createDialog.open();
                if(createDialog.getReturnCode() == Window.OK) {
                    close();
                    EditUserDialog dialog = 
                        new EditUserDialog( PlatformUI
                                            .getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell(), 
                                            userContainer );
                    dialog.open();
                    if(dialog.getReturnCode() == Window.OK) {
                        userContainerEdited = true;
                    }
                }
            }
        });
        final FormData formData_4 = new FormData();
        formData_4.right = new FormAttachment(100, -34);
        formData_4.top = new FormAttachment(0, 136);
        createNewKeyringButton.setLayoutData(formData_4);
        createNewKeyringButton.setText("Create new Account...");
        container.setTabList(new Control[] { usernameText, 
                                             passwordText, 
                                             passwordLabel, 
                                             usernameLabel, 
                                             createNewKeyringButton });
        setTitle("Log In To Your Bioclipse Account");
        //
        return area;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton( parent, 
                      IDialogConstants.OK_ID, 
                      IDialogConstants.OK_LABEL,
                      true );
        createButton( parent, 
                      IDialogConstants.CANCEL_ID,
                      IDialogConstants.CANCEL_LABEL, 
                      false );
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(500, 337);
    }
    protected void buttonPressed(int buttonId) {
        /*
         * LOGIN
         */
        if (buttonId == IDialogConstants.OK_ID) { 
            username = usernameText.getText();
            password = passwordText.getText();
            final String username = this.username;
            final String password = this.password;
            Job job = new Job("Signing in " + username) {
                
                @Override
                protected IStatus run( IProgressMonitor monitor ) {

                    try {
                        int scale = 1000;
                        monitor.beginTask( "Signing in...", 
                                           IProgressMonitor.UNKNOWN );
                        IUserManager us = Activator.getDefault().getUserManager();

                        us.signInWithProgressBar( 
                                                 username, password, 
                                                 new SubProgressMonitor(
                                                                   monitor, 
                                                                   1 * scale) );
                        ArrayList<String> failedLogins = us.getFailedLogins();
                        if (!failedLogins.isEmpty()) {
                            Iterator<String> itr = failedLogins.iterator();
                            String name = "";
                            errorMessage = "Bioclipse could not " +
                            		"log-in to your one or several third-part " +
                            		"account(s):\n\n";
                            while(itr.hasNext())
                                name = itr.next();
                                errorMessage += "\t" + name.substring( name.lastIndexOf( '.' ) + 1 )+ "\n";
                            errorMessage += "\nPlease check your log-in " +
                            		"settings for the feature(s) that has failed.";
                            Display.getDefault().syncExec( new Runnable() {

                                @Override
                                public void run() {

                                    MessageDialog.openInformation( PlatformUI.getWorkbench()
                                                                   .getActiveWorkbenchWindow()
                                                                   .getShell(),
                                                                   "Feature log-in failure",
                                                                   errorMessage );

                                }
                            } );   
                        }   
                    }
                    catch ( final Exception e ) {
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {
                                MessageDialog.openInformation( 
                                           PlatformUI
                                           .getWorkbench()
                                           .getActiveWorkbenchWindow()
                                           .getShell(), 
                                           "Could not sign in "
                                           + username, 
                                           e.getMessage() );
                                try {
                                    ((IHandlerService) 
                                    PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getService(IHandlerService.class) )
                                        .executeCommand(
                                            "net.bioclipse.usermanager" +
                                                ".commands.login", 
                                            null );
                                }
                                catch ( Exception e ) {
                                    LogUtils.handleException( 
                                         e, 
                                         logger, 
                                         "net.bioclipse.usermanager" );
                                }
                            }
                        });
                    }
                    finally {
                        monitor.done();
                    }
                    return Status.OK_STATUS;
                }
            };
            job.setUser( true );
            job.schedule();
          }
        super.buttonPressed(buttonId);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUserContainerEdited() {
        return userContainerEdited;
    }
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Log In");
	}
}
