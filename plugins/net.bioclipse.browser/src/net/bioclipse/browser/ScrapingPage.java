package net.bioclipse.browser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bioclipse.core.domain.IBioObject;

/**
 * 
 * @author ola
 */
public class ScrapingPage {

    volatile Set<IBioObject> scrapedObjects;
    String url;
    boolean changing;
    int icon_nr=0;

    public ScrapingPage() {
        scrapedObjects=new HashSet<IBioObject>();
    }

    public ScrapingPage(String url, boolean changing) {
        this();
        this.url = url;
        this.changing=changing;
    }

    public ScrapingPage(String url, 
                        List<? extends IBioObject> objects, 
                        boolean changing) {
        this.scrapedObjects = new HashSet<IBioObject>(objects);
        this.url = url;
        this.changing=changing;
    }

    public int getIcon_nr() {
        return icon_nr;
    }

    public boolean isChanging() {
        return changing;
    }
    public void setChanging( boolean changing ) {
        this.changing = changing;
        
        //Rotate icon
        icon_nr++;
        if (icon_nr==3)
            icon_nr=0;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl( String url ) {
        this.url = url;
    }

    public Set<IBioObject> getScrapedObjects() {
        return scrapedObjects;
    }
    public void setScrapedObjects( Set<IBioObject> scrapedObjects ) {
        this.scrapedObjects = scrapedObjects;
    }

    public void addScrapedObjects( List<? extends IBioObject> objects ) {
        
//        System.out.println("We have new objects: " + objects.size());
//        Set<IBioObject> a = new HashSet<IBioObject>(objects);
//        a.removeAll( scrapedObjects );
//        System.out.println("We have new unique objects: " + a.size());

        Set<IBioObject> b = new HashSet<IBioObject>(objects);

        for (IBioObject o : scrapedObjects){
            for (IBioObject obj : objects){
                if (o.equals( obj )){
                    b.remove( obj );
                }
            }
        }
        
        System.out.println("We have new unique equals objects: " + b.size());

        scrapedObjects.addAll( b );
    }
    
    @Override
    public String toString() {
        return url;
    }


}
