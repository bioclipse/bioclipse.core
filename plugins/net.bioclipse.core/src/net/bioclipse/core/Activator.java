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
import java.net.URL;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.core";

    // Virtual project
    public static final String VIRTUAL_PROJECT_NAME = "Virtual";
    
    // The shared instance
    private static Activator plugin;
    
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    public Activator() {
    }
    
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // fetch the Location that we will be modifying 
        Location instanceLoc = Platform.getInstanceLocation();
        // if the location is already set, we start from eclipse
        if(!instanceLoc.isSet()){
          // startedFromWorkspace=true means we do a restart from "switch workspace" and therefore
          // get the workspace location from dialog, if not, we use the default one or the remembered one set by user.
          boolean startedFromWorkspace = PickWorkspaceDialog.isStartedFromSwitchWorkspace();
          if(startedFromWorkspace){
              instanceLoc.set(new URL("file", null, PickWorkspaceDialog.getLastSetWorkspaceDirectory()), false);
              PickWorkspaceDialog.setStartedFromSwitchWorkspace( false );
          }else{
              // get what the user last said about remembering the workspace location 
              boolean remember = PickWorkspaceDialog.isRememberWorkspace();
              if(remember){
                  // get the last used workspace location 
                  String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();  
                  instanceLoc.set(new URL("file", null, lastUsedWs), false);
              }else{
                  instanceLoc.set(new URL("file", null, PickWorkspaceDialog.getWorkspacePathSuggestion()), false);
              }
          }
   

        }
        deleteVirtualProject();
        getVirtualProject();
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
    protected static void  deleteVirtualProject(){
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(VIRTUAL_PROJECT_NAME);
        try {
            project.delete(true, null);
        } catch (CoreException e) {            
            LogUtils.debugTrace( logger, e );
        }
    }
}
