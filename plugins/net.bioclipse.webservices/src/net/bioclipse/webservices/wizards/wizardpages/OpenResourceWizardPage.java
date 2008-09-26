package net.bioclipse.webservices.wizards.wizardpages;

/**
 * 
 * The Job Finished Wizard Page.
 * 
 * @author edrin
 *
 */

import java.io.StringBufferInputStream;
import java.lang.reflect.InvocationTargetException;

import net.bioclipse.webservices.WebservicesConstants;
import net.bioclipse.webservices.wizards.WebServiceWizardData;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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

public class OpenResourceWizardPage extends WizardPage implements IDoPerformFinish {
	private WebServiceWizardData data;
	private Text text_filename, text_data;
	private String filename;

	public OpenResourceWizardPage(WebServiceWizardData data, String filename) {
		super("OpenResourceWizardPage");
		this.data = data;
		setTitle("Bioclipse can open the result as a BioResource.");
		setDescription("Press 'Finish' to open the file.");
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
		String[] searchResult = data.GetSearchResult();		
		for (int i = 0; i < searchResult.length; i++) {
			if (i != 0) 
				text_data.append("\n");
			text_data.append(searchResult[i]);
 		}
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
	
	private void CreateResource(IProgressMonitor monitor) throws CoreException {
		monitor.worked(1);
		monitor.subTask("new resource");
		
		//If null, create the virtual folder to hold results
		IProject root = net.bioclipse.core.Activator.getVirtualProject();
		if(!root.exists(new Path(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER))) {
			root.getFolder(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER).create(true,true, new NullProgressMonitor());
		}
		IFolder wsVirtualFolder=root.getFolder(new Path(WebservicesConstants.WEBSERVICES_VIRTUAL_FOLDER));
		monitor.worked(1);
		monitor.subTask("load file data");

		StringBuffer strbuf = new StringBuffer();

		String[] searchResult = data.GetSearchResult();		
		for (int i = 0; i < searchResult.length; i++) {
			if (i != 0){
				if (!(i==1 && searchResult[0].equals(""))){
					strbuf.append("\n");
				}
			}
			if (!(i==0 | searchResult[i].equals("")))
				strbuf.append(searchResult[i]);
 		}
		//TODO: remove first line if "<PRE>\n" - however these are serverside bugs...
		//TODO: remove last line if "<TER>\n"
		String content=strbuf.toString();
		
		wsVirtualFolder.getFile(text_filename.getText()).create(new StringBufferInputStream(content), false, monitor);
		monitor.worked(1);
		monitor.subTask("parsing resource");
		monitor.worked(1);
	}

	public boolean DoPerformFinish() {
		boolean success = true;
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Creating BioResource", 5);
				try {
					CreateResource(monitor);
					monitor.done();
				} catch (CoreException e) {
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
	
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "net.bioclipse.webservices", IStatus.OK, message, null);
		throw new CoreException(status);
	}
}