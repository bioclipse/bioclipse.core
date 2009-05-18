package net.bioclipse.ui.dialogs;

import net.bioclipse.ui.Activator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class UpdatesAvailableDialog extends TitleAreaDialog{

    private String updateTitle    = "Online updates",
                   updatemessage  = "There are online updates available for "
                                    + "Bioclipse.",
                   updateQuestion = "Would you like to download and install "
                                    + "them now?",
                   rememberText   = "Remember my choice (can be changed in "
                                     + "Preferences)";

    private Button btnRemember;

    public UpdatesAvailableDialog(Shell parentShell) {
        super(parentShell);

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginTop       = 10;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());

        Label lblUpdate = new Label(composite, SWT.NONE | SWT.LEFT);
        lblUpdate.setText(updateQuestion);
        GridData gdLblUpdate = new GridData(GridData.FILL_BOTH);
        gdLblUpdate.horizontalIndent = 10;
        lblUpdate.setLayoutData(gdLblUpdate);

        btnRemember = new Button(composite, SWT.CHECK );
        GridData gdBtnRemember = new GridData(GridData.FILL_BOTH);
        gdBtnRemember.horizontalIndent = 10;
        btnRemember.setLayoutData(gdBtnRemember);
        btnRemember.setText(rememberText);
        btnRemember.setSelection(true);

        setTitle(updateTitle);
        setMessage(updatemessage);

        return composite;
    }

    /**
     * Stores settings for CANCEL press.
     */
    @Override
    protected void cancelPressed() {

        //Remember checkbox answers
        Activator.getDefault().getDialogSettings().put(
                net.bioclipse.ui.dialogs.IDialogConstants
                    .SKIP_UPDATE_DIALOG_ON_STARTUP,
                btnRemember.getSelection());

        //Remember choice YES/NO if checked remember box
        if (btnRemember.getSelection()==true){

            Activator.getDefault().getDialogSettings().put(
                    net.bioclipse.ui.dialogs.IDialogConstants
                        .SKIP_UPDATE_ON_STARTUP,
                    true);
        }

        super.cancelPressed();
    }

    /**
     * Store settings for OK press
     */
    @Override
    protected void okPressed() {

        //Remember checkbox answers
        Activator.getDefault().getDialogSettings().put(
                net.bioclipse.ui.dialogs.IDialogConstants
                    .SKIP_UPDATE_DIALOG_ON_STARTUP,
                btnRemember.getSelection());

        if (btnRemember.getSelection()) {
            Activator.getDefault().getDialogSettings().put(
                    net.bioclipse.ui.dialogs.IDialogConstants
                        .SKIP_UPDATE_ON_STARTUP,
                    false);
        }

        super.okPressed();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID,
                     IDialogConstants.YES_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                     IDialogConstants.NO_LABEL, false);
    }

    /**
     * This stores the location and size of the dialog
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        // TODO Auto-generated method stub
        return Activator.getDefault().getDialogSettings();
    }
}
