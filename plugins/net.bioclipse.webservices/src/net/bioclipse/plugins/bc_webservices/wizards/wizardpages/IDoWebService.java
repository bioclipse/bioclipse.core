package net.bioclipse.plugins.bc_webservices.wizards.wizardpages;

/**
 * 
 * This Interface is used to perform the Web Service "ToDo" of a wizard page.
 * This is required as the wizard implementation of eclipse is missing a
 * OnButtonNext() thing. This Interface is accessed on the following page's
 * SetVisible()
 * 
 * @author edrin
 *
 */

public interface IDoWebService {
	/**
	 * There is no OnButtonNext. This function might be used from
	 * setVisible() or createControl() on the next page.
	 * Returns a String with errordetails or an empty string
	 * */
	public String DoWebService();
}
