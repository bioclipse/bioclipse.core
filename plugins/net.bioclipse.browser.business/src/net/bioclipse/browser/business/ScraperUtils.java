package net.bioclipse.browser.business;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import net.bioclipse.browser.scraper.IBrowserScraper;

/**
 * 
 * @author ola
 *
 */
public class ScraperUtils {

    public static List<IBrowserScraper> readScrapersFromEP() {


        List<IBrowserScraper> scrapers=new ArrayList<IBrowserScraper>();

        //Initialize implementations via extension points
        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if ( registry == null )
            throw new RuntimeException("Registry is null, no services can " +
            "be read. Workbench not started?");
        // it likely means that the Eclipse workbench has not
        // started, for example when running tests

        /*
         * service objects
         */
        IExtensionPoint serviceObjectExtensionPoint = registry
        .getExtensionPoint(BrowserConstants.SCRAPER_EXTENSION_POINT);

        IExtension[] serviceObjectExtensions
        = serviceObjectExtensionPoint.getExtensions();


        for(IExtension extension : serviceObjectExtensions) {
            for( IConfigurationElement element
                    : extension.getConfigurationElements() ) {

                if (element.getName().equals("scraper")){

                    try {
                        String pid=element.getAttribute("id");
                        String pname=element.getAttribute("name");

                        IBrowserScraper scraper=(IBrowserScraper) element
                        .createExecutableExtension("class");

                        scrapers.add(scraper);
                        System.out.println("Finished adding scraper: " + pname);

                    } catch (CoreException e) {
                        System.out.println("Could not initialize EP. Reason: " 
                                           + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
        }

        return scrapers;
    }

}
