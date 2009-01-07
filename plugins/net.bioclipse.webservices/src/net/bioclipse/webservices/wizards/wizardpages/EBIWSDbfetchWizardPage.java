package net.bioclipse.webservices.wizards.wizardpages;

/**
 * 
 * WizardPage to use the EBI's WSDbfetch Web Service.
 * 
 * @author edrin
 * @author ola
 *
 */

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.webservices.services.WSDbfetch;
import net.bioclipse.webservices.wizards.JobFinishedWizard;
import net.bioclipse.webservices.wizards.WebServiceWizardData;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

public class EBIWSDbfetchWizardPage extends WizardPage implements IDoPerformFinish {
	private WebServiceWizardData data;
	private Text text_query;
	private Combo combo_dbs, combo_formats, combo_styles;
	private String[] dbs, formats, styles;
	private static String title = "Bioclipse Web Service - WSDbfetch at EBI";
	private static String defPageTitle =
			"WSDbfetch offers access various up-to-date biological databases.";
	private static String defPageDescription =
			"Please select database, format and style and enter the query (in example 1JR8 for pdb, NM_210721 for refseq).";
	private String selDatabase, selFormat, selStyle, selQuery;
	private boolean blockcombo;

	/** Constructor for SampleNewWizardPage. */
	public EBIWSDbfetchWizardPage(WebServiceWizardData data) {
		super("EBIWSDbfetchWizardPage");
		this.data = data;
		setTitle(defPageTitle);
		setDescription(defPageDescription);
		dbs = new String[0];
		formats = new String[0];
		styles = new String[0];
		// no preselection, set to empty strings
		selDatabase = new String();
		selFormat = new String();
		selStyle = new String();
		selQuery = new String();
		blockcombo = false;
	}
	
	public EBIWSDbfetchWizardPage(WebServiceWizardData data,
									String database,
									String format,
									String style,
									String query,
									String description,								
									boolean blockcombo) {
		super("EBIWSDbfetchWizardPage");
		this.data = data;
		setTitle(defPageTitle);
		if (description.length() > 0)
			setDescription(description);
		else
			setDescription(defPageDescription);		
		dbs = new String[0];
		formats = new String[0];
		styles = new String[0];
		// set preselection strings
		selDatabase = database;
		selFormat = format;
		selStyle = style;
		selQuery = query;
		this.blockcombo = blockcombo;
	}

	public void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.NULL);
		label.setText("Supported Databases:");

		// DB has no influence on the query!
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		combo_dbs = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		combo_dbs.setLayoutData(gd);
		combo_dbs.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				combo_dbsChanged();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("Format:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		combo_formats = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		combo_formats.setLayoutData(gd);

		label = new Label(composite, SWT.NULL);
		label.setText("Style:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		combo_styles = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		combo_styles.setLayoutData(gd);

		label = new Label(composite, SWT.NULL);
		label.setText("Query:");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		text_query = new Text(composite, SWT.BORDER | SWT.SINGLE);
		text_query.setLayoutData(gd);
		text_query.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				text_queryChanged();
			}
		});

		setControl(composite);
		setPageComplete(false);
		
		FillCombos();
	}

	public void combo_dbsChanged() {
		String db = combo_dbs.getText();
		combo_formats.removeAll();
		String[] dbformats = GetDbFormats(db);
		for (int z = 1; z < dbformats.length; z++) {
			combo_formats.add(dbformats[z]);
		}
		combo_formats.select(0);
		
		combo_styles.removeAll();
		String[] dbstyles = GetDbStyles(db);
		for (int z = 0; z < dbstyles.length; z++) {
			combo_styles.add(dbstyles[z]);
		}
		combo_styles.select(0);		
	}

	private String[] GetDbFormats(String db) {
		String[] sub_db_formats, sub_formats;
		/*
		 * new version looks like this:
		 * formats[0] = "embl	default,embl,emblxml,fasta,insdxml";
		 * ...
		 * 
		 * now we split:	String[] sub_db_formats;
		 * sub_db_formats[0] = "embl";
		 * sub_db_formats[1] = "default,embl,emblxml,fasta,insdxml";
		 * split("\\s") ->regular expression for spaces
		 * split("\t")	->java level tab
		 * split(",")	->, key
		 * 
		 */
		for (int i = 0; i < formats.length; i++) {
			sub_db_formats = formats[i].split("\t");
			if (sub_db_formats.length >= 2 &&
					sub_db_formats[0].equals(db) == true) {
				sub_formats = sub_db_formats[1].split(",");
				return sub_formats;				
			}
		}
		return new String[0];
	}

	private String[] GetDbStyles(String db) {
		String[] sub_db_styles, sub_styles;
		for (int i = 0; i < styles.length; i++) {
			sub_db_styles = styles[i].split("\t");
			if (sub_db_styles.length >= 2 &&
					sub_db_styles[0].equals(db) == true) {
				sub_styles = sub_db_styles[1].split(",");
				return sub_styles;				
			}
		}
		return new String[0];
	}

	public void text_queryChanged() {
		if (text_query.getCharCount() > 0) {			
			data.SetCanFinish(true);
			setPageComplete(true);	// will update buttons!
		}
		else {
			data.SetCanFinish(false);
			setPageComplete(false);	// will update buttons!
		}
	}
	
	private void FillCombos() {
		// get properties (databases, formats and styles)
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask(title, 5);
				try {
					GetParameters(monitor);
					monitor.done();
				} catch (CoreException e) {
					throw new InvocationTargetException(e, e.getMessage());
				}
			}
		};
		try {
			getWizard().getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			setErrorMessage("Error - could not get properties: " + e.getMessage());
		} catch (InterruptedException e) {	// we could simply ignor this exception!?
			setErrorMessage("Error - the action was canceled: " + e.getMessage());
		}

		// and fill the combos; the formats combo will be filled on db selection
		for (int i = 0; i < dbs.length; i++) {
			combo_dbs.add(dbs[i]);
		}

		// default selection
		combo_dbs.select(0);	

		// preselect something?
		if (selDatabase.length() > 0) {
			combo_dbs.setText(selDatabase);
			combo_dbsChanged();
		}
		if (selFormat.length() > 0)
			combo_formats.setText(selFormat);
		if (selStyle.length() > 0)
			combo_styles.setText(selStyle);
		if (selQuery.length() > 0)
			text_query.setText(selQuery);
		if (blockcombo == true) {
			combo_dbs.setEnabled(false);
			combo_formats.setEnabled(false);
			combo_styles.setEnabled(false);
		}
	}

	private void GetParameters(IProgressMonitor monitor) throws CoreException {
		monitor.worked(1);
		WSDbfetch wsdbfetch = new WSDbfetch();
		monitor.worked(1);
		monitor.subTask("getSupportedDBs();");
		dbs = wsdbfetch.getSupportedDBs(monitor);
		monitor.worked(1);
		monitor.subTask("getSupportedFormats();");
		formats = wsdbfetch.getSupportedFormats(monitor);
		monitor.worked(1);
		monitor.subTask("getSupportedStyles();");
		styles = wsdbfetch.getSupportedStyles(monitor);
		monitor.worked(1);
	}

	private String GetEntries(String query, String format, String style, IProgressMonitor monitor)
	throws CoreException {
		String searchResult = null;
		monitor.subTask("fetchData(" + query + ", " + format + ", " + style + ");");
		monitor.worked(1);
		WSDbfetch wsdbfetch = new WSDbfetch();
		monitor.worked(1);

		//It is important to have PDB queries as lowercase
		if (format.equals("pdb")){
			query=query.toLowerCase();
		}
		searchResult = wsdbfetch.fetchData(query, format, style, monitor);
		monitor.worked(1);
		return searchResult;
	}

	public boolean DoPerformFinish() {
		final String query = combo_dbs.getText() + ":" + text_query.getText();
		final String format = combo_formats.getText();
		final String style = combo_styles.getText();
		final String filename = combo_dbs.getText() + "_" + text_query.getText() + "." + format;
		// in example EMBL:AY310909
		
		// show progress window		
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchPage wbPage = wb.getActiveWorkbenchWindow().getActivePage(); 
        if (wbPage != null) {
            IViewPart progressView = wbPage.findView("org.eclipse.ui.views.ProgressView");
            if (progressView == null)
            	try {
            		wbPage.showView("org.eclipse.ui.views.ProgressView");
            	} catch (PartInitException e) {
            		net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("PartInitException: " + e.getMessage());
            	}
        }
        // define the job
		Job job = new Job(title) {
			private String errordetails;
			protected IStatus run(IProgressMonitor monitor) {
				boolean bSuccess = true;				
				// set a friendly icon and keep the job in list when it is finished
				//TODO what's this in bc2?
				//setProperty(IProgressConstants.ICON_PROPERTY, ImageUtils.getImageDescriptor("ws_ebi"));			
				
				monitor.beginTask(title, 3);
				try {					
					data.SetSearchResult(GetEntries(query, format, style, monitor));
					monitor.done();
				} catch (CoreException e) {
					monitor.setTaskName("Error: " + e.getMessage());
					errordetails = e.getMessage();
					bSuccess = false;
				}

				if (bSuccess == true)
					monitor.setTaskName("Result of " + query + " ");

				if (IsModal() == true && bSuccess == true) {	// finish job imediately!
					Display.getDefault().syncExec(new Runnable() {	// do not use async, we need the GUI!
						public void run() {
							FinishJob();
						}
					});
				} else {	// inform user about news
					setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					
					if (bSuccess == true)
						setProperty(IProgressConstants.ACTION_PROPERTY, JobSuccessAction());
					else	// error
						setProperty(IProgressConstants.ACTION_PROPERTY, JobErrorAction());
				}
				return Status.OK_STATUS;
			}
			protected Action JobErrorAction() {
				return new Action("Web Service done") {
					public void run() {
						MessageDialog.openError(getShell(),
												title,
												"The Web Service returned an error:\n" + errordetails);
					}
				};
			}
			protected Action JobSuccessAction() {
				return new Action("Web Service done") {
					public void run() {
						FinishJob();
					}
				};
			}
			private void FinishJob() {
				OpenResourceWizardPage page = new OpenResourceWizardPage(data, filename); 
				JobFinishedWizard wizard = new JobFinishedWizard(data, page, title);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);				
				dialog.open();
			}
			private boolean IsModal() {
				Boolean isModal = (Boolean)getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
				if (isModal == null || isModal.booleanValue() == false) {
					return false;
				}
				return true;
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}
}