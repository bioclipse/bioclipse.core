package net.bioclipse.webservices.wizards.wizardpages;

/**
 * 
 * This Interface offers a function to have the performFinish on a wizard page.
 * This Interface is accessed in wizard's performFirnish()
 * 
 * @author edrin
 *
 */

public interface IDoPerformFinish {

	/**
	 * In example a WebService could run a job, or something eles specific for
	 * the wizard.
	 */
	public boolean DoPerformFinish();
}
