package net.bioclipse.browser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IPostSelectionProvider;

import net.bioclipse.browser.business.IScrapingModelChangedListener;
import net.bioclipse.core.domain.IBioObject;

/**
 * 
 * @author ola
 *
 */
public class ScrapingModel {

    //Simple map from URl to set of scraped objects
    List<ScrapingPage> scrapingPages;
    Set<IScrapingModelChangedListener> listeners;
    
    
    public ScrapingModel() {
        scrapingPages= new ArrayList<ScrapingPage>();
        listeners=new HashSet<IScrapingModelChangedListener>();
    }
    
    public List<ScrapingPage> getScrapingPages() {
        return scrapingPages;
    }

    public void addChangedListener( IScrapingModelChangedListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangedListener( IPostSelectionProvider provider ) {
        listeners.remove( provider );
    }

    /**
     * Add list of objects to an existing page
     * @param url to page
     * @param objects list of obejcts to add
     */
    public void addToModel(String url, List<? extends IBioObject> objects){
        for (ScrapingPage page : scrapingPages){
            if (page.getUrl().equals( url )){
                page.addScrapedObjects(objects);
                page.setChanging( false );
            }
        }
                
        //We need to refresh the page
       for (IScrapingModelChangedListener listener : listeners){
           listener.pagesChanged( url);
       }
        
    }

    /**
     * We have received a tick in progress for a page.
     * @param url to page
     */
    public void pageChanged( String url ) {

        if (url==null || url.equals( "" ))
            return;

        //If already exists, mark as changing
        for (ScrapingPage page : scrapingPages){
            if (page.getUrl().equals( url )){
                page.setChanging( true );
                //Notify all listeners that this page has changed
                for (IScrapingModelChangedListener listener : listeners){
                    listener.pagesChanged( url);
                }
                return;
            }
        }

        //If not exists, create a new page and mark as changing
        ScrapingPage page=new ScrapingPage(url, true);
        scrapingPages.add( page );

        //Notify all listeners that we have a new page
        for (IScrapingModelChangedListener listener : listeners){
            listener.modelChanged(url);
        }
        
    }

    /**
     * The page is loaded.
     * @param url to page
     */
    public void pageComplete( String url ) {

        for (ScrapingPage page : new ArrayList<ScrapingPage>(scrapingPages)){
            //We might as well set all pages to not changing
            page.setChanging( false );
            if (page.getUrl().equals( url )){
                if (page.getScrapedObjects()==null || page.getScrapedObjects().size()<=0){
                    scrapingPages.remove( page );
                }
                //Notify all listeners that this page has changed
                for (IScrapingModelChangedListener listener : listeners){
//                    listener.modelChanged(url);
                    listener.pagesChanged( url );
                }
                return;
            }
        }        
    }

    /**
     * A new page is scraped. Note that the page might exist and be visited
     *  earlier.
     * @param url
     */
    public void newPage( String url ) {

        if (url==null || url.equals( "" ))
            return;

        //If already exists, mark as changing
        boolean pageExists=false;
        for (ScrapingPage page : scrapingPages){
            if (page.getUrl().equals( url )){
                pageExists=true;
            }else{
                page.setChanging( false );
                
            }
        }

        //Don't go further if page exists.
        if (pageExists) return;
        
        //If not exists, create a new page and mark as changing
        ScrapingPage page=new ScrapingPage(url, true);
        scrapingPages.add( page );

        //Notify all listeners that we have a new page
        for (IScrapingModelChangedListener listener : listeners){
            listener.modelChanged(url);
        }
                
    }

    
}
