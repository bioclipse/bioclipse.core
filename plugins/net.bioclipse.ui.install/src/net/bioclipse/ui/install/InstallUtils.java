package net.bioclipse.ui.install;

import java.net.URI;

import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;


@SuppressWarnings("restriction")
public class InstallUtils {

    public static Catalog configureCatalog( URI uri,
                                            BasicRepositoryDiscoveryStrategy strategy ) {

        Catalog catalog = new Catalog();

        strategy.addLocation( uri );
        catalog.getDiscoveryStrategies().add( strategy );

        catalog.setEnvironment( DiscoveryCore.createEnvironment() );
        catalog.setVerifyUpdateSiteAvailability( false );
        return catalog;
    }
}
