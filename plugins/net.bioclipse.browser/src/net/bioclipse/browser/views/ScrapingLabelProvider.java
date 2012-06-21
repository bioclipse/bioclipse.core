package net.bioclipse.browser.views;

import net.bioclipse.browser.Activator;
import net.bioclipse.browser.ScrapingPage;
import net.bioclipse.core.domain.IBioObject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;


public class ScrapingLabelProvider implements ILabelProvider {

    private Image pageImage;
    private Image changingImage0;
    private Image changingImage1;
    private Image changingImage2;
    private Image missingImage;

    private Image image;
        
    public ScrapingLabelProvider() {
        pageImage=Activator.getImageDescriptor("icons/world_dl.png")
            .createImage();
        changingImage0=Activator.getImageDescriptor("icons/p0.gif")
        .createImage();
        changingImage1=Activator.getImageDescriptor("icons/p1.gif")
        .createImage();
        changingImage2=Activator.getImageDescriptor("icons/p2.gif")
        .createImage();
        missingImage=ImageDescriptor.getMissingImageDescriptor().createImage();
    }
    
    public Image getImage( Object element ) {
        if ( element instanceof ScrapingPage ) {
            ScrapingPage page=(ScrapingPage) element;
            if (page.isChanging()){
                if (page.getIcon_nr()==0)
                    return changingImage0;
                if (page.getIcon_nr()==1)
                    return changingImage1;
                if (page.getIcon_nr()==2)
                    return changingImage2;
            }
            else
                return pageImage;
        }
        if ( element instanceof IBioObject ) {
        	ImageDescriptor imageDescriptor = (ImageDescriptor) ((IBioObject)element).getAdapter( ImageDescriptor.class );
                if (image==null) {
                	//FIXME only the first image that is asked for is used.
                	image = imageDescriptor.createImage();
                }
                return (Image) image;
        }
        
        return missingImage;
    }

    public String getText( Object element ) {
        if ( element instanceof ScrapingPage ) {
            return ((ScrapingPage)element).getUrl();
        }
        if ( element instanceof IBioObject ) {
            IBioObject bo = (IBioObject) element;
            return bo.toString();
        }
        else if ( element instanceof String ) {
            return (String) element;
        }
        return "N/A";
    }

    public void addListener( ILabelProviderListener listener ) {
    }

    public void dispose() {
    	if(image!= null ) {
    		image.dispose();
    	}
    }

    public boolean isLabelProperty( Object element, String property ) {
        return false;
    }

    public void removeListener( ILabelProviderListener listener ) {
    }

}
