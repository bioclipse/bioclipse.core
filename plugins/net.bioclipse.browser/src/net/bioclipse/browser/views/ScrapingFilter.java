package net.bioclipse.browser.views;

import net.bioclipse.browser.ScrapingPage;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


public class ScrapingFilter extends ViewerFilter {

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {

        if ( element instanceof ScrapingPage ) {
            ScrapingPage page = (ScrapingPage) element;
            if (page.isChanging()) return true;
            else if (page.getScrapedObjects().size()<=0) return false;
        }
        
        return true;
    }

}
