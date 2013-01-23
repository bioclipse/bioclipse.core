
package net.bioclipse.ui.install.discovery;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSModelsDiscoveryStrategy extends
                BasicRepositoryDiscoveryStrategy {

    public static final String ID_PLUGIN = "net.bioclipse.ui.install";

    protected void queryInstallableUnits( SubMonitor monitor,
                                          List<IMetadataRepository> repositories ) {
		monitor.setWorkRemaining(repositories.size());
		for (final IMetadataRepository repository : repositories) {
			checkCancelled(monitor);
            Object[] features = new Object[] { "net.bioclipse.ds.models_feature.feature.group" };// ,"net.bioclipse.ds.models.r_feature.feature.group"
                                                                                                 // };
            IQuery<IInstallableUnit> cQuery = QueryUtil
                            .createQuery( "select( iu | $0.exists( id | id == iu.id)) ",
                                               new Object[]{features} );
            IQueryResult<IInstallableUnit> cResult = repository.query( cQuery,
 monitor.newChild( 100 ) );
            List<IInstallableUnit> resultIUnits = new ArrayList<IInstallableUnit>();
            for(Iterator<IInstallableUnit> ius = cResult.iterator();ius.hasNext();) {
                resultIUnits.add(ius.next());
            }


            Logger logger = LoggerFactory.getLogger(DSModelsDiscoveryStrategy.class);
            if(logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                for(Iterator<IInstallableUnit> ius = cResult.iterator();ius.hasNext();) {
                    IInstallableUnit iu = ius.next();
                    sb.append(iu.toString());
                    sb.append(", ");
                }
                logger.debug("Found features: "+sb.toString());
            }
            // IInstallableUnit iu = cResult.iterator().next();

            IQuery<IInstallableUnit> query = QueryUtil
                            .createQuery( "select( iu | ( $0.collect( su | su.requirements).flatten().exists( rc | iu ~= rc) && iu.providedCapabilities.exists( pc | pc.namespace == 'org.eclipse.equinox.p2.eclipse.type' && pc.name == 'bundle') ) || iu.id == 'net.bioclipse.ds.models.r_feature.feature.group' || iu.id == 'net.bioclipse.opentox.ds_feature.feature.group'|| iu.id == 'net.bioclipse.smartcyp_feature.feature.group')", new Object[] { resultIUnits.toArray() } ); //$NON-NLS-1$
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
