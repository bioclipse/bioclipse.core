/* *****************************************************************************
 * Copyright (c) 2007-2009 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.ui;


import java.io.File;
import java.net.URL;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ui.dialogs.PickWorkspaceDialog;
import net.bioclipse.ui.prefs.IPreferenceConstants;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * WorkbenchAdvisor that installs the default perspective as initial.
 * @author ola
 *
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisorHack {

    private static final Logger logger = Logger.getLogger(ApplicationWorkbenchAdvisor.class);

    private boolean abort;

    public ApplicationWorkbenchAdvisor() {
        // fetch the Location that we will be modifying 
        Location instanceLoc = Platform.getInstanceLocation();
        // if the location is already set, we start from eclipse
        if(!instanceLoc.isSet()){
          logger.debug( "The instance location was not set" );
          // startedFromWorkspace=true means we do a restart from "switch workspace" and therefore
          // get the workspace location from dialog, if not, we use the default one or the remembered one set by user.
          boolean startedFromWorkspace = PickWorkspaceDialog.isStartedFromSwitchWorkspace();
          logger.debug( "We are " + (startedFromWorkspace? " " : "not ") + "starting from switch workspace" );
          try {
            if(startedFromWorkspace){
              instanceLoc.set(new URL("file", null, PickWorkspaceDialog.getLastSetWorkspaceDirectory()), false);
              PickWorkspaceDialog.setStartedFromSwitchWorkspace( false );
              logger.debug("Workspace set to: " + PickWorkspaceDialog.getLastSetWorkspaceDirectory());
            }else{
              // get what the user last said about remembering the workspace location 
              boolean remember = PickWorkspaceDialog.isRememberWorkspace();
              if (!remember) {
                  logger.debug( "The user wants to do new starts with default workspace" );
              }
              else {
                  logger.debug( "The user wants to do new starts with non-default workspace" );
              }
              if(remember){
                  // get the last used workspace location 
                  String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();  
                  instanceLoc.set(new URL("file", null, lastUsedWs), false);
                  logger.debug("Workspace set to: " + lastUsedWs);
              }else{
                  instanceLoc.set(new URL("file", null, PickWorkspaceDialog.getWorkspacePathSuggestion()), false);
                  logger.debug("Workspace set to: " + PickWorkspaceDialog.getLastSetWorkspaceDirectory());
              }
            }
          } catch (Exception exception) {
            throw new RuntimeException("Failed to set up the Bioclipse " +
                "workspace: " + exception.getMessage(), exception);
          }
        }
        
        
    }
    
    public boolean isAbort() {
        return abort;
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);

    }

    @Override
    public void postStartup() {

        abort=false;
        
        //TODO Perhaps allow Bioclpse to be started without the Navigator 
        // (Right now Bioclipse refuses to start if the Navigator 
        //  view has been closed)
        IWorkbenchPage activePage = PlatformUI.getWorkbench()
        		.getActiveWorkbenchWindow()
        		.getActivePage();
		try {
			final String BIOCLIPSE_NAVIGATOR = "net.bioclipse.navigator";
        	boolean foundNavigator = false;
            IViewReference[] parts = activePage.getViewReferences();
            for(IViewReference part : parts ) {
            	if(part.getId().equals( BIOCLIPSE_NAVIGATOR ) ) {
            		foundNavigator = true;
            		break;
            	}
            }
            if(!foundNavigator) {
            	PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            	    .getActivePage().showView(BIOCLIPSE_NAVIGATOR);
            }
            
        }
        catch ( PartInitException e1 ) {
            LogUtils.handleException( e1, logger, "net.bioclipse.ui" );
        }
        
        CommonNavigator nav = (CommonNavigator) activePage
                                     .findView( "net.bioclipse.navigator" );

        nav.getCommonViewer().setInput( getDefaultPageInput() );

        //Read update prefs from store
        IPreferenceStore prefsStore=Activator.getDefault().getPreferenceStore();
        boolean skipUpdate=prefsStore.getBoolean(
                                  IPreferenceConstants.SKIP_UPDATE_ON_STARTUP );

        
        //If we said do not update in the earlier dialog, skip check.
        if (skipUpdate){
            logger.debug("Skipped updating due to preference setting.");
            return;
        }

        if (!Activator.getDefault().checkForUpdates){
            logger.debug("Skipped updating due to -noupdate argument.");
            return;
        }

        //Ok, check for updates if not turned off by arg -noupdate

        if(isRunFromDMG()) {
        	MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Running from DMG",
        			"Bioclipse has detected that you are running Bioclipse from inside a volume named Bioclipse. " +
        			"Bioclipse is not ment to be run from inside the dmg containing Bioclipse that you download. " +
        			"This is not supported and will cause problems in some parts of Bioclipse." +
        			"\nPlease drag the Bioclipse app into your Application folder " +
        			"or another place in your filesystem.");
        }
    }

    private boolean isRunFromDMG() {
    	File currentDir = new File(".");
    	logger.debug("Runing dir is "+currentDir.getAbsolutePath());
    	return currentDir.getAbsolutePath().startsWith("/Volumes/Bioclipse");
    }

    public String getInitialWindowPerspectiveId() {
        return DefaultPerspective.ID_PERSPECTIVE;
    }

    private void showMessage(final String message) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openInformation(
                                              PlatformUI.getWorkbench()
                                              .getActiveWorkbenchWindow()
                                              .getShell(),
                                              "Automatic updates",
                                              message);
            }
        });

    }


}
