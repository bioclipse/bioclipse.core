/* *****************************************************************************
 * Copyright (c) 2009 Arvid Berg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Arvid Berg - Adaption of RepositoryDiscoveryStrategy
 ******************************************************************************/

package net.bioclipse.ui.install.discovery;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;

/**
 * @author Arvid Berg
 */
public class DataDiscoveryStrategy extends
                BasicRepositoryDiscoveryStrategy {

	private static final String queryString = "select( select( iu | iu.id ~=/net.bioclipse.data.*/ && !(iu.id ~= /*source*/ )) ,_ , { xu, parent | parent.properties['org.eclipse.equinox.p2.type.group'] == true && parent.requirements.exists( rq | xu.exists( x |  x ~= rq )) } )";
	private static final String oldQueryString = "select( parent | parent.properties['org.eclipse.equinox.p2.type.group'] == true " + "&& parent.requirements.exists(rc | everything.exists( iu | iu ~= rc && iu.id ~= /net.bioclipse.data.*/)))";
	
    protected void queryInstallableUnits( SubMonitor monitor,
                                          List<IMetadataRepository> repositories ) {
		monitor.setWorkRemaining(repositories.size());
		for (final IMetadataRepository repository : repositories) {
			checkCancelled(monitor);
            IQuery<IInstallableUnit> query = QueryUtil.createQuery( queryString );
			IQueryResult<IInstallableUnit> result = repository.query(query, monitor.newChild(1));
			for (Iterator<IInstallableUnit> iter = result.iterator(); iter.hasNext();) {
				process(repository, iter.next());
			}
		}
	}

}
