/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: Tasktop
 * Technologies - initial API and implementation Arvid Berg
 *******************************************************************************/

package net.bioclipse.ui.install.commands;

import static org.osgi.framework.FrameworkUtil.getBundle;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.discovery.AbstractCatalogSource;
import org.eclipse.equinox.internal.p2.discovery.compatibility.BundleDiscoverySource;
import org.eclipse.equinox.internal.p2.discovery.model.Icon;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 */
public class ReleaseRepositoryDiscoveryStrategy extends
                BasicRepositoryDiscoveryStrategy {

    Map<String, String> bundleToIcon = new HashMap<String, String>();
    private String[] bundles = {
    		
    		"bioclipse.qsar",
    		"bioclipse.metaprint2d",
    		"bioclipse.ds",
    		"bioclipse.bioinformatics",
    		"bioclipse.opentox_",
    		"bioclipse.opentox.qsar_",
    		"bioclipse.hivdrc",
    		"bioclipse.balloon",
    		"bioclipse.social",
    		"bioclipse.r_"

//                    "bioclipse.cheminformatics_feature",
//                    "bioclipse.statistics", "org.openscience.cdk",
//                    "bioclipse.ons", "cdk_feature", "bioclipse.data",
//                    "bioclipse.xws4j", "bioclipse.rdf", "bioclipse.qsar",
//                    "bioclipse.joelib", "bioclipse.metaprint2d",
//                    "bioclipse.ds", "bioclipse.hivdrc",
//                    "bioclipse.experimental", "bioclipse.vscreen",
//                    "bioclipse.structuredb", "bioclipse.social",
//                    "bioclipse.bioinformatics", "bioclipse.opentox",
//                    "bioclipse.moss", "bioclipse.chembl", "bioclipse.swipl",
//                    "bioclipse.blipkit", "bioclipse.rdf",
//                    "bioinformatics.rdf_feature",
//                    "cheminformatics.rdf_feature", "rdf.ui_feature",
//                    "bioclipse.update", "bioclipse.metabolomics",
//                    "bioclipse.speclipse", "bioclipse.taverna",
//                    "bioclipse.medea" 
                    };

    protected void queryInstallableUnits( SubMonitor monitor,
                                        List<IMetadataRepository> repositories ) {

        monitor.setWorkRemaining( repositories.size() );
		for (final IMetadataRepository repository : repositories) {
			checkCancelled(monitor);
            IQuery<IInstallableUnit> query = QueryUtil
                            .createQuery(
                                                                   
                            //.createMatchQuery( 
                                               "select( iu | iu.id ~= /*.feature.group/ && iu.properties['org.eclipse.equinox.p2.type.group'] == true && !(iu.id ~= /*.source.feature.group/ ) ).latest()" 
                                               );
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

        initIconMapping();

        List<IInstallableUnit> filteredList = new ArrayList<IInstallableUnit>();
        while ( iter.hasNext() ) {
            IInstallableUnit iu = iter.next();
            String id = iu.getId();
            if ( bundleToIcon.get( id ) != null ) {
                filteredList.add( iu );
            } else {
                for ( String bId : bundles ) {
                    if ( iu.getId().contains( bId ) ) {
                        filteredList.add( iu );
                        break;
                    }
                }
            }
        }
        return filteredList;
    }

    @Override
    protected AbstractCatalogSource getSource( IMetadataRepository repository ) {

        if ( defaultCatalogSource == null ) {
            Bundle bundle = getBundle( BasicRepositoryDiscoveryStrategy.class );
            defaultCatalogSource = new BundleDiscoverySource( bundle );
        }
        return defaultCatalogSource;
    }

    protected void initIconMapping() {

        if ( bundleToIcon.size() != 0 )
            return;
        Bundle bundle = getBundle( ReleaseRepositoryDiscoveryStrategy.class );
        Enumeration<String> files = bundle.getEntryPaths( "icons/" );

        for ( String file; files.hasMoreElements(); ) {
            file = files.nextElement();
            if ( file.endsWith( "/" ) )
                continue;
            String bundleId = file.replaceAll( "icons/(.*?)\\d+\\..{3}", "$1" );
            bundleToIcon.put( bundleId + ".feature.group", file );
        }
    }
    @Override
    protected Icon getIcon( String id ) {

        initIconMapping();
        Icon icon = new Icon();
        String iconString = null;
        if ( (iconString = bundleToIcon.get( id )) != null ) {
            icon.setImage32( iconString );
        } else {
            icon.setImage32( "icons/default/icon32.png" );
            icon.setImage48( "icons/default/icon48.png" );
            icon.setImage64( "icons/default/icon64.png" );
            icon.setImage128( "icons/default/icon128.png" );
        }
        return icon;
    }
}
