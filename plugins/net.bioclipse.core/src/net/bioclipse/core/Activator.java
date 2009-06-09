/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.util.ListFuncs;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.core.util.Predicate;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.core";

    // Virtual project
    public static final String VIRTUAL_PROJECT_NAME = "Virtual";
    
    private static final String EXTENDER_BUNDLE_NAME = 
        "org.springframework.bundle.osgi.extender";
    
    // The shared instance
    private static Activator plugin;
    
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    public Activator() {
    }
    
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        getVirtualProject();
        startBundleExtender();
    }
    
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    protected static void createVirtualProject(IProject project){
        
        IProjectDescription description = 
        		ResourcesPlugin.getWorkspace()
        		.newProjectDescription(VIRTUAL_PROJECT_NAME);
        try{
            description.setLocationURI(new URI("memory:/Virtual"));
            project.create(description,null);
            project.refreshLocal( IResource.DEPTH_ZERO, null );
            project.open(null);
        }catch(URISyntaxException use){
            logger.debug(use.getMessage(),use);
        }catch(CoreException x){
            logger.warn("Failed to create virtual project: "+x.getMessage());
        }
    }
    public static IProject getVirtualProject(){
    	
    	IWorkspaceRoot root=ResourcesPlugin.getWorkspace().getRoot();
    	IProject project=root.getProject(VIRTUAL_PROJECT_NAME);
    	if(!project.exists()){
    	    logger.debug("Could not insert Virtual project in MemoryFilesystem");
    		createVirtualProject(project);
    	}
    	if(!project.isOpen()) {
    	    try {
                project.open( null );
            } catch ( CoreException e ) {
                logger.debug( "Faild to open Virtual" );
            }
    	}
    	return project;
    }
    
    /* Attempts to start all resolved Spring Bundle Extender bundles.
     * Will log an error if it can't find or start the extender, and warns if
     * there is more than one extender bundle in the resolved state in the
     * system.
     * 
     * Assumes that the OSGI resolver has done its job and that each resolved
     * bundle whose symbolic name is that of Spring Bundle Extender
     * is separately required. 
     * 
     * (Moreover implicitly assumes that Spring appropriately labels its bundles
     * with the singleton directive, thus forcing the resolver to pick only 
     * one to resolve, if multiple extender bundles cannot coexist.) */
    
    private void startBundleExtender() {

        // How we define the bundles we consider startable. Ignores INSTALLED
        // bundles on the assumption that resolution just completed and there
        // must be a reason INSTALLED bundles were not resolved then.
        // (UNINSTALLED bundles are required never to be started.)
        
        Predicate<Bundle> isStartableAndHasBeenResolved = new Predicate<Bundle>() {
            public Boolean eval(Bundle b) {
                final int mask = Bundle.INSTALLED | Bundle.UNINSTALLED;
                return (b.getState() & mask) == 0;    // autoboxes to Boolean class
            }
        };
        
        // start all Spring Extender bundles that meet the condition set out 
        // in the predicate above
        
        List<Bundle> allExtenders = 
            Arrays.asList(Platform.getBundles(EXTENDER_BUNDLE_NAME, null));    
        List<Bundle> toStart = 
            ListFuncs.filter(allExtenders, isStartableAndHasBeenResolved);
       
        for (Bundle b : toStart)
            startTransiently(b);

        // and now for some warnings

        final int nToStart = toStart.size();
        if (nToStart > 1) {
            logger.warn("More than one resolved Spring Bundle Extender found, "
                    + "is this expected?");
        }
        else if (nToStart == 0) {
            
            // This is more problematic. The most likely reason for getting
            // here is that we no longer have the correct symbolic name of the
            // bundle extender, or the other bundles no longer express a 
            // dependency on the bundle extender.
            
            logger.warn("No resolved Spring bundle extender found, valiantly "
                    + "attempting to soldier on anyway.");
        }
        else {
            assert (nToStart == 1): 
                "Oops forgot a case when counting startable bundle extenders.";
        }
    }
    
    
    /* What we do with the bundles we want to start. Notes:
       - could easily factor out bundle name in logging comments to reuse    
       - retain transient start or else started bundle will autostart 
         next time, potentially causing confusion and/or races */
    
    private Boolean startTransiently(Bundle b) {
        logger.debug("Attempting to start Spring Bundle Extender...");
        try {
            b.start(Bundle.START_TRANSIENT);  
            logger.debug("Spring Bundle Extender started.");
            return true;
        }
        catch (BundleException e) {
            logger.error("Unable to start resolved Spring Bundle Extender: " + e);
            LogUtils.debugTrace(logger, e);
            return false;
        }
    }
}
