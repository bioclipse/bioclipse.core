package net.bioclipse.ui.install.commands;

import java.net.URI;
import java.net.URISyntaxException;

import net.bioclipse.ui.install.InstallUtils;
import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryWizard;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;


public class ShowRepositoryCatalogCommandHandler extends AbstractHandler {

    private static final String ID_PARAMETER_REPOSITORY =
                                                                        "org.eclipse.equinox.p2.ui.discovery.commands.RepositoryParameter"; //$NON-NLS-1$
    private static final String ID_PARAMETER_STRATEGY   =
                                                                        "net.bioclipse.ui.install.commands.RepositoryStrategyParameter";

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        // getRepository
        URI uri = getRepository( event );
        // getStrategy
        BasicRepositoryDiscoveryStrategy strategy = getStrategy( event );
        // configureCatalog
        Catalog catalog = InstallUtils.configureCatalog( uri, strategy );
        CatalogConfiguration configuration = new CatalogConfiguration();
        configuration.setShowTagFilter( false );

        // open dialog
        IWizard wizard = getWizard( catalog, configuration );
        WizardDialog dialog =
                        new WizardDialog( WorkbenchUtil.getShell(), wizard );
        dialog.open();

        return null;
    }

    private IWizard getWizard( Catalog catalog,
                               CatalogConfiguration configuration ) {

        // NewDataProjectWizard wizard = new NewDataProjectWizard( catalog,
        // configuration );

        return new DiscoveryWizard( catalog, configuration );// wizard;
    }

    private BasicRepositoryDiscoveryStrategy getStrategy( ExecutionEvent event ) {

        String ss = event.getParameter( ID_PARAMETER_STRATEGY );
        RepositoryDiscoveryStrategyParameterConverter rdspc = new RepositoryDiscoveryStrategyParameterConverter();
        BasicRepositoryDiscoveryStrategy rds = null;
        try {
            rds = (BasicRepositoryDiscoveryStrategy) rdspc.convertToObject( ss );
        } catch ( ParameterValueConversionException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BasicRepositoryDiscoveryStrategy strategy = rds;
        return strategy;
    }

    private URI getRepository( ExecutionEvent event ) throws ExecutionException {

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
        return uri;
    }

}
