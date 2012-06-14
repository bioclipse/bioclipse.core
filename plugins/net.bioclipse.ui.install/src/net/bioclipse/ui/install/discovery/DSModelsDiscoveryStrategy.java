
package net.bioclipse.ui.install.discovery;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.discovery.operations.DiscoveryInstallOperation;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.statushandlers.StatusManager;

public class DSModelsDiscoveryStrategy extends
                BasicRepositoryDiscoveryStrategy {

    public static final String ID_PLUGIN = "net.bioclipse.ui.install";

    protected void queryInstallableUnits( SubMonitor monitor,
                                          List<IMetadataRepository> repositories ) {
		monitor.setWorkRemaining(repositories.size());
		for (final IMetadataRepository repository : repositories) {
			checkCancelled(monitor);
            IQuery<IInstallableUnit> cQuery = QueryUtil
                            .createMatchQuery( "id == 'net.bioclipse.ds.models_feature.feature.group'",
                                               new Object[0] );
            IQueryResult<IInstallableUnit> cResult = repository.query( cQuery,
 monitor.newChild( 100 ) );
            IInstallableUnit iu = cResult.iterator().next();

            IQuery<IInstallableUnit> query = QueryUtil
                            .createQuery( "select( iu | $0.requirements.exists( rc | iu ~= rc))", new Object[] { iu } ); //$NON-NLS-1$
			IQueryResult<IInstallableUnit> result = repository.query(query, monitor.newChild(1));
			for (Iterator<IInstallableUnit> iter = result.iterator(); iter.hasNext();) {
				process(repository, iter.next());
			}
		}
	}

    public static boolean install( List<CatalogItem> descriptors,
                                   IRunnableContext context ) {

        try {
            IRunnableWithProgress runner = new DiscoveryInstallOperation(
                            descriptors ) {

                @Override
                protected IQuery<IInstallableUnit> createInstalledIUsQuery() {

                    return QueryUtil.createIUAnyQuery();
                }
            };
            context.run( true, true, runner );
        } catch ( InvocationTargetException e ) {
            IStatus status = new Status(
                                         IStatus.ERROR,
                                         DSModelsDiscoveryStrategy.ID_PLUGIN,
                                         e.getCause().getMessage(),
                                         e.getCause() );
            StatusManager.getManager()
                            .handle( status,
                                     StatusManager.SHOW | StatusManager.BLOCK
                                                     | StatusManager.LOG );
            return false;
        } catch ( InterruptedException e ) {
            // canceled
            return false;
        }
        return true;
    }
}
