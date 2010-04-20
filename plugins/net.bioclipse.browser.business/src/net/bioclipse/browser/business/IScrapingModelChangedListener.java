package net.bioclipse.browser.business;

/**
 * 
 * @author ola
 *
 */
public interface IScrapingModelChangedListener {

    /**
     * This URL is now changing
     */
    public void modelChanging( String url );

    /**
     * This model has changed (e.g. a new page is added or removed)
     */
    public void modelChanged( String url );

    /**
     * A page has changed
     * @param url URL to page
     */
    void pagesChanged( String url );

}
