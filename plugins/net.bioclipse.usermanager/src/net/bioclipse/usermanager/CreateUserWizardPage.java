package net.bioclipse.usermanager;

import net.bioclipse.usermanager.UserContainer;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

public class CreateUserWizardPage extends WizardPage implements Listener {

    private Label repeatPasswordLabel;
    private Label passwordLabel;
    private Label usernameLabel;
    private Text  repeatPasswordText;
    private Text  passwordText;
    private Text  userNameText;
    private UserContainer sandBox;
    
    protected CreateUserWizardPage(String pageName, UserContainer userContainer) {
        super( pageName );
        sandBox = userContainer;
    }

    @Override
    public void createControl( Composite parent ) {
        Composite container = new Composite(parent, SWT.NONE);
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

        repeatPasswordLabel = new Label(container, SWT.NONE);
        final FormData formData_2 = new FormData();
        repeatPasswordLabel.setLayoutData(formData_2);
        repeatPasswordLabel.setText("Repeat password:");

        userNameText = new Text(container, SWT.BORDER);
        formData.bottom = new FormAttachment(userNameText, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(userNameText, -5, SWT.LEFT);
        final FormData formData_3 = new FormData();
        formData_3.top = new FormAttachment(0, 19);
        userNameText.setLayoutData(formData_3);
        userNameText.addListener( SWT.Modify, this );
        
        passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_3.left = new FormAttachment(passwordText, -317, SWT.RIGHT);
        formData_3.right = new FormAttachment(passwordText, 0, SWT.RIGHT);
        formData_1.bottom = new FormAttachment(passwordText, 0, SWT.BOTTOM);
        formData_1.right = new FormAttachment(passwordText, -5, SWT.LEFT);
        final FormData formData_4 = new FormData();
        formData_4.top = new FormAttachment(0, 63);
        passwordText.setLayoutData(formData_4);
        passwordText.addListener( SWT.Modify, this );
        
        repeatPasswordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        formData_2.bottom = new FormAttachment(repeatPasswordText, 0, SWT.BOTTOM);
        formData_2.right = new FormAttachment(repeatPasswordText, -5, SWT.LEFT);
        formData_4.left = new FormAttachment(repeatPasswordText, -316, SWT.RIGHT);
        formData_4.right = new FormAttachment(repeatPasswordText, 0, SWT.RIGHT);
        final FormData formData_5 = new FormData();
        formData_5.top = new FormAttachment(0, 107);
        formData_5.left = new FormAttachment(0, 154);
        formData_5.right = new FormAttachment(0, 470);
        repeatPasswordText.setLayoutData(formData_5);
        repeatPasswordText.addListener( SWT.Modify, this );
        
        container.setTabList(new Control[] { userNameText, 
                                             passwordText, 
                                             repeatPasswordText, 
                                             usernameLabel, 
                                             passwordLabel, 
                                             repeatPasswordLabel });
        setControl(container);
        setPageComplete( isPageComplete() );
    }

    @Override
    public boolean isPageComplete() {
        
        if (userNameText.getText().isEmpty() &&
                passwordText.getText().isEmpty() &&
                repeatPasswordText.getText().isEmpty() ) {
            setErrorMessage( null );
            return false;
        }
        String message = "Please fill in ";
        if (userNameText.getText().isEmpty()) {
            message +=" username";
            if (passwordText.getText().isEmpty())
                message += "and password";
            if (repeatPasswordText.getText().isEmpty() )
                message += "and repeat password";
            message += ".";
            setErrorMessage( message );
            return false;
        }
        if (passwordText.getText().isEmpty()) {
            message += " password";
            if (userNameText.getText().isEmpty()) 
                message +=" and username";
            if (repeatPasswordText.getText().isEmpty() )
                message += "and repeat password";
            message += ".";
            setErrorMessage( message );
            return false;
        }
        if (repeatPasswordText.getText().isEmpty()) {
            message += "repeat password";
            if (userNameText.getText().isEmpty()) 
                message +=" and username";
            if (passwordText.getText().isEmpty())
                message += "and password";
            message += ".";
            setErrorMessage( message );
            return false;
        }
        if (!passwordText.getText().equals( repeatPasswordText.getText() )) {
            setErrorMessage( "Password and repeat password must be the same" );
            return false;
        }        
        setErrorMessage( null );
        return true;
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
            String userName = userNameText.getText();
            String password = passwordText.getText();
            sandBox.createUser( userName, password );
            if (!sandBox.isLoggedIn())
                sandBox.signIn( userName, password, null );
        }
        super.setVisible( visible );
    }

    @Override
    public void handleEvent( Event event ) {
        setPageComplete( isPageComplete() );
    }
    
    @Override
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem()
        .setHelp( Display.getCurrent().getActiveShell(),
                  "net.bioclipse.usermanager.createAccountHelp" );
    }
}
