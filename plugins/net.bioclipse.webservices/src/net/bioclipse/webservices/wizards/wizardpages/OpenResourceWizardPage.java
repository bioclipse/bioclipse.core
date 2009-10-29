package net.bioclipse.webservices.wizards.wizardpages;

/**
 * 
 * The Job Finished Wizard Page.
 * 
 * @author edrin
 *
 */

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.webservices.ResourceCreator;
import net.bioclipse.webservices.WebservicesConstants;
import net.bioclipse.webservices.wizards.WebServiceWizardData;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class OpenResourceWizardPage extends WizardPage implements IDoPerformFinish {
	private WebServiceWizardData data;
	private Text text_filename, text_data;
	private String filename;

	public OpenResourceWizardPage(WebServiceWizardData data, String filename) {
		super("OpenResourceWizardPage");
		this.data = data;
		setTitle("Bioclipse will save the result in folder '" +
				WebservicesConstants.WEBSERVICES_RESULTS +
				"' of project '" +
				WebservicesConstants.WEBSERVICES_PROJECT + "'.");
		setDescription("Press 'Finish' to save and open the file.");
		this.filename = filename;
	}

	public void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(layout);

		// composite 2 start
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		Composite composite2 = new Composite(composite, SWT.NULL);
		composite2.setLayout(layout);
		composite2.setLayoutData(gd);

		Label label = new Label(composite2, SWT.NULL);
		label.setText("File name:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		text_filename = new Text(composite2, SWT.BORDER | SWT.SINGLE);
		text_filename.setLayoutData(gd);
		text_filename.setText(filename);
		text_filename.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				text_filenameChanged();
			}
		});
		// composite 2 end

		// composite 3 start
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;

		gd = new GridData(GridData.FILL_BOTH);
		Composite composite3 = new Composite(composite, SWT.NULL);
		composite3.setLayout(layout);
		composite3.setLayoutData(gd);
		
		label = new Label(composite3, SWT.NULL);
		label.setText("Preview:");
		
		gd = new GridData(GridData.FILL_BOTH);
		text_data = new Text(composite3, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		text_data.setLayoutData(gd);
		String searchResult = data.GetSearchResult();

		if (searchResult != null)
			text_data.append(searchResult);
		else
			text_data.append("Error: no valid data."); // valid
		// composite 3 end

		setControl(composite);
		if(filename.length() > 0)
			setPageComplete(true);
		else
			setPageComplete(false);
	}

	public void text_filenameChanged() {
		if (text_filename.getCharCount() > 0) {
			data.SetCanFinish(true);
			setPageComplete(true);	// will update buttons!
		}
		else {
			data.SetCanFinish(false);
			setPageComplete(false);	// will update buttons!
		}
	}
	
	public boolean DoPerformFinish() {
		boolean success = true;
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Saving downloaded file.", 5);
				try {
					IFile file = ResourceCreator.createResource(
							text_filename.getText(), data.GetSearchResult(),
							monitor);
					monitor.done();
					
					IWorkbenchPage page =
						PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getActivePage();

					if (page != null) {
						IEditorDescriptor desc = PlatformUI.getWorkbench().
				        getEditorRegistry().getDefaultEditor(file.getName());
						page.openEditor(new FileEditorInput(file),
								desc.getId());					}
				} catch (Exception e) {
					throw new InvocationTargetException(e, e.getMessage());
				}
			}
		};
		try {
			getWizard().getContainer().run(false, false, runnable);
		} catch (InvocationTargetException e) {
			setErrorMessage("Error - could not get properties: " + e.getMessage());
			success = false;
		} catch (InterruptedException e) {	// we could simply ignor this exception!?
			setErrorMessage("Error - the action was canceled: " + e.getMessage());
			success = false;
		}
		return success;
	}
}