package net.bioclipse.webservices.wizards;

/**
 * 
 * The New Wizard Data to store information for the wizard pages.
 * 
 * @author edrin
 *
 */

import org.eclipse.jface.viewers.ISelection;

public class WebServiceWizardData {
	private ISelection selection;
	private String searchResult;
	private boolean canfinish;
	
	public WebServiceWizardData() {
		this.selection = null;
		searchResult = null;
		canfinish = false;
	}

	// we can finish if we got some data
	public boolean canFinish() {
		return canfinish;
	}

	public void SetCanFinish(boolean canfinish)
	{
		this.canfinish = canfinish;
	}

	public void SetSelection(ISelection selection) {
		this.selection = selection;
	}
	
	public ISelection GetSelection() {
		return selection;		
	}
	
	public void SetSearchResult(String searchResult) {
		this.searchResult = searchResult;
	}
	
	public String GetSearchResult() {
		return searchResult;
	}	
}