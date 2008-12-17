package net.bioclipse.ui.dialogs;
import net.bioclipse.ui.Activator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
public class UpdatesAvailableDialog extends TitleAreaDialog{
        private String updateTitle="Online updates";
        private String updatemessage="There are online updates available for Bioclipse.";
        private String updateQuestion="Would you like to download and install them now?";
        private String rememberText="Remember my choice (can be changed in Preferences)";
        private Button btnRemember;
        private Button btnReview;
//	private String reviewText="Review available features before download";
        private final static int UPDATE_BUTTON=678;
        public UpdatesAvailableDialog(Shell parentShell) {
                super(parentShell);
        }
        @Override
        protected Control createDialogArea(Composite parent) {
                // create the top level composite for the dialog area
                Composite composite = new Composite(parent, SWT.NONE);
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginWidth = 0;
                layout.verticalSpacing = 0;
                layout.horizontalSpacing = 0;
//		layout.numColumns=2;
                composite.setLayout(layout);
                composite.setLayoutData(new GridData(GridData.FILL_BOTH));
                composite.setFont(parent.getFont());
                // Build the separator line
                Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
                                | SWT.SEPARATOR);
                GridData gd=new GridData(GridData.FILL_BOTH);
//		gd.horizontalSpan=2;
                titleBarSeparator.setLayoutData(gd);
                Label lblUpdate=new Label(composite, SWT.NONE | SWT.CENTER);
                lblUpdate.setText(updateQuestion);
                GridData gd2=new GridData(GridData.FILL_BOTH);
//		gd2.horizontalSpan=2;
                lblUpdate.setLayoutData(gd2);
//		btnReview=new Button(composite, SWT.CHECK );
//		btnReview.setLayoutData(new GridData(GridData.FILL_BOTH));
//		btnReview.setText(reviewText);
                btnRemember=new Button(composite, SWT.CHECK );
                btnRemember.setLayoutData(new GridData(GridData.FILL_BOTH));
                btnRemember.setText(rememberText);
                btnRemember.setSelection(true);
                setTitle(updateTitle);
                setMessage(updatemessage);
                return composite;
        }
        /**
         * Store settings for CANCEL press
         */
        @Override
        protected void cancelPressed() {
//		Activator.getDefault().getDialogSettings().put(
//				net.bioclipse.ui.dialogs.IDialogConstants.REVIEW_UPDATES,
//				btnReview.getSelection());
                //Remember checkbox answers
                Activator.getDefault().getDialogSettings().put(
                net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_DIALOG_ON_STARTUP,
                btnRemember.getSelection());
                //Remember choice YES/NO if checked remember box
                if (btnRemember.getSelection()==true){
                        //Remember answers yes/no
                        Activator.getDefault().getDialogSettings().put(
                                        net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_ON_STARTUP,
                                        true);
                }
                super.cancelPressed();
        }
        /**
         * Store settings for OK press
         */
        @Override
        protected void okPressed() {
//		Activator.getDefault().getDialogSettings().put(
//				net.bioclipse.ui.dialogs.IDialogConstants.REVIEW_UPDATES,
//				btnReview.getSelection());
                //Remember checkbox answers
                Activator.getDefault().getDialogSettings().put(
                net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_DIALOG_ON_STARTUP,
                btnRemember.getSelection());
                //Remember choice if checked
                if (btnRemember.getSelection()==true){
                        //Remember
                        Activator.getDefault().getDialogSettings().put(
                                        net.bioclipse.ui.dialogs.IDialogConstants.SKIP_UPDATE_ON_STARTUP,
                                        false);
                }
                super.okPressed();
        }
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
                // create OK and Cancel buttons by default
                createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL,
                                true);
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
