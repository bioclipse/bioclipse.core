package net.bioclipse.browser.views;

import net.bioclipse.browser.ScrapingModel;
import net.bioclipse.browser.ScrapingPage;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ScrapingContentProvider implements ITreeContentProvider {

    public Object[] getChildren( Object parentElement ) {
        if ( parentElement instanceof ScrapingPage ) {
            ScrapingPage page = (ScrapingPage) parentElement;
            return page.getScrapedObjects().toArray();
        }
        return new Object[0];
    }

    public Object getParent( Object element ) {
        return null;
    }

    public boolean hasChildren( Object element ) {
        if ( element instanceof ScrapingModel ) {
            ScrapingModel smodel = (ScrapingModel) element;
            return smodel.getScrapingPages().size()>0;
        }
        else if ( element instanceof ScrapingPage ) {
            ScrapingPage page = (ScrapingPage) element;
            return page.getScrapedObjects().size()>0;
        }

        return false;
    }

    public Object[] getElements( Object inputElement ) {
        
        if ( inputElement instanceof ScrapingModel ) {
            ScrapingModel smodel = (ScrapingModel) inputElement;
            if (smodel.getScrapingPages()==null || 
                    smodel.getScrapingPages().size()<=0){
                return new String[]{"No extracted objects available."};
            }
            return smodel.getScrapingPages().toArray();
        }

        return new Object[0];
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }
}
