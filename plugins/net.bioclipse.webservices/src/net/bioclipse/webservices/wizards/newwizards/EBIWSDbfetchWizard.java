package net.bioclipse.webservices.wizards.newwizards;

/**
 * 
 * The Wizard to fetch entries with EBI's WSDbfetch.
 * 
 * @author edrin
 *
 */

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import net.bioclipse.webservices.wizards.WebServiceWizardData;
import net.bioclipse.webservices.wizards.wizardpages.EBIWSDbfetchWizardPage;
import net.bioclipse.webservices.wizards.wizardpages.IDoPerformFinish;

public class EBIWSDbfetchWizard extends Wizard implements INewWizard {
	private WebServiceWizardData data;
	private EBIWSDbfetchWizardPage page;

	public EBIWSDbfetchWizard() {
		super();
		setWindowTitle("WSDbfetch Web Service at the EBI");
		setNeedsProgressMonitor(true);
		data = new WebServiceWizardData();
		page = new EBIWSDbfetchWizardPage(data);	
	}

	/**
	 * The EBIWSDbfetchWizard allows you to retrieve entries
	 * from various up-to-date biological databases. This
	 * constructor can preselect parameters for the search. You
	 * should verify your parameters by using the wizard from
	 * the "new" menu.
	 * @param database	the database identifier	(i.e. 'emb' or 'pdb')
	 * @param format	the format of the result (i.e. 'fasta' or 'pdb'), put empty string for default
	 * @param sytle		the style (use 'raw' !), put empty string for default
	 * @param query		the query value,  put empty string for... nothing
	 * @param description	a description of the database and format you select; will be shown instead
	 * 						of normal description, put empty string for default
	 * @param blockcombo	user can not change the database/format/style
	 */
	public EBIWSDbfetchWizard(String database,
								String format,
								String style,
								String query,
								String description,								
								boolean blockcombo) {
		super();
		setWindowTitle("WSDbfetch Web Service at the EBI");
		setNeedsProgressMonitor(true);
		data = new WebServiceWizardData();
		page = new EBIWSDbfetchWizardPage(data, database, format, style, query, description, blockcombo);
	}

	public void addPages() {		
		addPage(page);
	}

	public boolean performFinish() {
		if(page instanceof IDoPerformFinish)
		{
			((IDoPerformFinish)page).DoPerformFinish();
		}
		return true;
	}

	/** the selection in the workbench IWorkbenchWizard#init(IWorkbench, IStructuredSelection) */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		data.SetSelection(selection);
	}

	public boolean canFinish() {
		return data.canFinish();
		//return page.isPageComplete();
	}
}