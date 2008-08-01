/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.ui;


import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.core.VersionedIdentifier;
import org.eclipse.update.operations.IInstallFeatureOperation;
import org.eclipse.update.operations.OperationsManager;

/**
 * WorkbenchAdvisor that installs the default perspective as initial.
 * @author ola
 *
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisorHack {

    private static final Logger logger = Logger.getLogger(ApplicationWorkbenchAdvisor.class);

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);

    }
    
    @Override
    public void postStartup() {
        super.postStartup();
    	
        if (Activator.getDefault().checkForUpdates) {
            Job updateJob=new Job("Online updates") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {

                    /*
		        	       * Launch automatic updates
		        	       */
                    try {
		        		
                        logger.debug("## Checking for updates... ");
                        lauchAutomaticUpdates(monitor);
                        logger.debug("## Finished checking for updates.");
                    } catch (MalformedURLException e) {
                        logger.debug("Automatic updates error: "
                                     + e.getMessage());
                    } catch (CoreException e) {
                        logger.debug("Automatic updates error: "
                                     + e.getMessage());
                    } catch (InvocationTargetException e) {
                        logger.debug("Automatic updates error: "
                                     + e.getMessage());
                    }

                    return Status.OK_STATUS;
                }
            };
    		
            updateJob.setUser(false);
            updateJob.schedule();
        }
        else {
            logger.debug("## -noUpdate detected, do not check for updates. ");
        }
    }

    public String getInitialWindowPerspectiveId() {
        return DefaultPerspective.ID_PERSPECTIVE;
    }
	
    @SuppressWarnings({ "deprecation", "unchecked" })
	private void lauchAutomaticUpdates(IProgressMonitor monitor)
        throws MalformedURLException, CoreException, InvocationTargetException {

        //    	IProgressMonitor monitor = new NullProgressMonitor();
        ISite rs = SiteManager.getSite(new URL(BioclipseConstants.UPDATE_SITE),
                                       monitor);
        IFeatureReference[] frs = rs.getFeatureReferences();
        ILocalSite ls = SiteManager.getLocalSite();
        IConfiguredSite ics
            = ls.getCurrentConfiguration().getConfiguredSites()[0];
        IFeatureReference[] lfrs = ics.getConfiguredFeatures();
        List<IInstallFeatureOperation> installOps
            = new ArrayList<IInstallFeatureOperation>();

        for (int i = 0; i < frs.length; i++) {

            //Default is not installed
            boolean installedFeature=false;

            //Add if feature and version > what is installed
            for (int j = 0; j < lfrs.length; j++) {

                VersionedIdentifier frsVi = frs[i].getVersionedIdentifier();
                VersionedIdentifier lfrsVi = lfrs[j].getVersionedIdentifier();

                if (frsVi.getIdentifier().equals(lfrsVi.getIdentifier())) {
                    //We have this feature installed
                    installedFeature=true;

                    //Only install feature if version is greater than installed
                    if (frsVi.getVersion().isGreaterThan(lfrsVi.getVersion())) {

                        installOps.add(
                            OperationsManager
                              .getOperationFactory()
                              .createInstallOperation(
                                ics,
                                frs[i].getFeature(monitor),
                                null,
                                null,
                                null)
                        );
                        logger.debug("** Added feature: "
                                     + frs[i].getName()
                                     + " to update list");
                    }
                }
            }
            //Found a not installed feature
            if (installedFeature==false){

                //Add if feature patch
                if (frs[i].isPatch()){
                    logger.debug("** Found and added remote feature patch: "
                                 + frs[i].getName());
                    installOps.add(
                        OperationsManager
                          .getOperationFactory()
                          .createInstallOperation(
                            ics,
                            frs[i].getFeature(monitor),
                            null,
                            null,
                            null)
                    );
                }

            }
        }

        if (installOps.size() > 0) {
            for (Iterator iter = installOps.iterator(); iter.hasNext();) {
                IInstallFeatureOperation op
                  = (IInstallFeatureOperation) iter.next();
                logger.debug("** Installing feature: "
                             + op.getFeature().getLabel());
                op.execute(monitor, null);
            }
            boolean restartRequired = ls.save();
            logger.debug("Restart required (seems always true): " + restartRequired);
            showMessage("Updates for Bioclipse have been "
                        + "downloaded and installed.");
        }
        else {
            logger.debug("** No features found on update site");
        }

        monitor.done();
    }


    private void showMessage(final String message) {
        // do not use async, we need the GUI!
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
