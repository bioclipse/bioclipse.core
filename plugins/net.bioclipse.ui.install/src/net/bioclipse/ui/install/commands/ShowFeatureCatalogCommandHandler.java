package net.bioclipse.ui.install.commands;

import java.net.URI;
import java.net.URISyntaxException;

import net.bioclipse.data.wizards.NewDataProjectWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.jface.wizard.WizardDialog;


public class ShowFeatureCatalogCommandHandler extends AbstractHandler {

    private static final String ID_PARAMETER_REPOSITORY =
                                                                        "org.eclipse.equinox.p2.ui.discovery.commands.RepositoryParameter2"; //$NON-NLS-1$

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        String location = event.getParameter( ID_PARAMETER_REPOSITORY );
        if ( location == null ) {
            throw new ExecutionException( "Location not specified"
                                          + ID_PARAMETER_REPOSITORY );
        }
        URI uri;
        try {
            uri = new URI( location );
        } catch ( URISyntaxException e ) {
            throw new ExecutionException( "Not a valid p2 repository", e );
        }

        Catalog catalog = new Catalog();

        ReleaseRepositoryDiscoveryStrategy strategy =
                        new ReleaseRepositoryDiscoveryStrategy();
        strategy.addLocation( uri );
        catalog.getDiscoveryStrategies().add( strategy );

        catalog.setEnvironment( DiscoveryCore.createEnvironment() );
        catalog.setVerifyUpdateSiteAvailability( false );

        CatalogConfiguration configuration = new CatalogConfiguration();
        configuration.setShowTagFilter( false );

        NewDataProjectWizard wizard =
                        new NewDataProjectWizard( catalog, configuration );
        WizardDialog dialog =
                        new WizardDialog( WorkbenchUtil.getShell(), wizard );
        dialog.open();

        return null;
    }

}
