/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package net.bioclipse.ui.install.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;

/**
 * @author Steffen Pingel
 */
public class ReleaseRepositoryDiscoveryStrategy extends
                BasicRepositoryDiscoveryStrategy {

    private String[] bundles = {

                             "net.bioclipse.core", "nu.psnet.quickimage",
                    "bioclipse.cheminformatics_feature", "bioclipse.balloon",
                    "bioclipse.statistics", "org.openscience.cdk",
                    "bioclipse.ons", "cdk_feature", "bioclipse.data",
                    "bioclipse.xws4j", "bioclipse.rdf", "bioclipse.qsar",
                    "bioclipse.joelib", "bioclipse.metaprint2d",
                    "bioclipse.ds", "bioclipse.hivdrc",
                    "bioclipse.experimental", "bioclipse.vscreen",
                    "bioclipse.structuredb", "bioclipse.social",
                    "bioclipse.bioinformatics", "bioclipse.opentox",
                    "bioclipse.moss", "bioclipse.chembl", "bioclipse.swipl",
                    "bioclipse.blipkit", "bioclipse.rdf",
                    "bioinformatics.rdf_feature",
                    "cheminformatics.rdf_feature", "rdf.ui_feature",
                    "bioclipse.update", "bioclipse.metabolomics",
                    "bioclipse.speclipse", "bioclipse.taverna",
                    "bioclipse.medea" };

    protected void queryInstallableUnits( SubMonitor monitor,
                                        List<IMetadataRepository> repositories ) {
		monitor.setWorkRemaining(repositories.size());
		for (final IMetadataRepository repository : repositories) {
			checkCancelled(monitor);
			IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(//
                            "id ~= /*.feature.group/ ? providedCapabilities.exists(p | p.namespace == 'org.eclipse.equinox.p2.iu' "
                                            + "&& p.name ~= /*.feature.group/) : properties['org.eclipse.equinox.p2.type.category'] == false" ); //$NON-NLS-1$
			IQueryResult<IInstallableUnit> result = repository.query(query, monitor.newChild(1));
            List<IInstallableUnit> res = filterIU( result.iterator() );
            for ( Iterator<IInstallableUnit> iter = res.iterator(); iter
                            .hasNext(); ) {
				process(repository, iter.next());
			}
		}
	}

    /*
     * Filter IUs for the ones we want
     */
    private List<IInstallableUnit> filterIU( Iterator<IInstallableUnit> iter ) {

        List<IInstallableUnit> filteredList = new ArrayList<IInstallableUnit>();
        while ( iter.hasNext() ) {
            IInstallableUnit iu = iter.next();
            for ( String id : bundles ) {
                if ( iu.getId().contains( id ) ) {
                    filteredList.add( iu );
                    break;
                }
            }
        }
        return filteredList;
    }

}
