package net.bioclipse.ui.install;

import java.net.URI;

import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;


@SuppressWarnings("restriction")
public class InstallUtils {

    public static Catalog configureCatalog( URI[] uris,
                                            BasicRepositoryDiscoveryStrategy strategy ) {

        Catalog catalog = new Catalog();

        for ( URI uri : uris ) {
            strategy.addLocation( uri );
        }
        catalog.getDiscoveryStrategies().add( strategy );

        catalog.setEnvironment( DiscoveryCore.createEnvironment() );
        catalog.setVerifyUpdateSiteAvailability( true );
        return catalog;
    }

    public static URI[] listInstalledRepositories( ProvisioningUI ui ) {

        ui.signalRepositoryOperationStart();
        IMetadataRepositoryManager metaManager = ProvUI.getMetadataRepositoryManager(ui.getSession());
//        IArtifactRepositoryManager artManager = ProvUI.getArtifactRepositoryManager(ui.getSession());
        try {
            int visibilityFlags = ui.getRepositoryTracker().getMetadataRepositoryFlags();
            URI[] currentlyEnabled = metaManager.getKnownRepositories(visibilityFlags);
//            URI[] currentlyDisabled = metaManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_DISABLED | visibilityFlags);
            return currentlyEnabled;
        } finally {
            ui.signalRepositoryOperationComplete( null, true );
        }
    }
}
