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

import net.bioclipse.ui.actions.UpdateAction;
import net.bioclipse.ui.dialogs.IDialogConstants;
import net.bioclipse.ui.dialogs.UpdatesAvailableDialog;
import net.bioclipse.ui.prefs.IPreferenceConstants;
import net.bioclipse.ui.prefs.UpdateSitesPreferencePage;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.ErrorSupportProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
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

    //Get settings for dialogs
    IDialogSettings settings = Activator.getDefault().getDialogSettings();

    private boolean abort;

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
        super.postStartup();

        abort=false;

        //If we said do not update in the earlier dialog, skip check.
        if (settings.getBoolean(IDialogConstants.SKIP_UPDATE_ON_STARTUP)){
            logger.debug("Skipped updating due to preference setting.");
            return;
        }

        if (!Activator.getDefault().checkForUpdates){
            logger.debug("Skipped updating due to -noupdate argument.");
            return;
        }

        //Ok, check for updates if not turned off by arg -noupdate
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

    public String getInitialWindowPerspectiveId() {
        return DefaultPerspective.ID_PERSPECTIVE;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    private void lauchAutomaticUpdates(IProgressMonitor monitor)
    throws MalformedURLException, CoreException, InvocationTargetException {

        //    	IProgressMonitor monitor = new NullProgressMonitor();

        IPreferenceStore prefsStore=Activator.getDefault().getPreferenceStore();
        String entireString=prefsStore.getString(IPreferenceConstants.UPDATE_SITES);
        List<String[]> sites=UpdateSitesPreferencePage.convertPreferenceStringToArraylist(entireString);

        List<IFeatureReference> refs=new ArrayList<IFeatureReference>();
        for (String[] updateSite : sites){

            if (updateSite!=null && updateSite.length==2){
                logger.debug("Contacting update site: " + updateSite[1]);

                //Contact each site
                ISite rs = SiteManager.getSite(new URL(updateSite[1]),
                                               monitor);

                //Add it's features to list
                for (IFeatureReference pref : rs.getFeatureReferences()){
                    logger.debug("Found available feature: " + pref.getName());
                    refs.add(pref);
                }
            }
            else{
                logger.debug("Skipped update site: " + updateSite);
            }
        }

        IFeatureReference[] frs = refs.toArray(new IFeatureReference[0]);

        ILocalSite ls = SiteManager.getLocalSite();
        if (ls.getCurrentConfiguration().getConfiguredSites()==null || 
                ls.getCurrentConfiguration().getConfiguredSites().length<=0){
            logger.error("Bioclispe seems not to have a local site. This should not happen.");
            throw new CoreException(new IStatus(){

                public IStatus[] getChildren() {
                    return null;
                }

                public int getCode() {
                    return 0;
                }

                public Throwable getException() {
                    return null;
                }

                public String getMessage() {
                    return "Bioclispe seems not to have a local site. This should not happen.";
                }

                public String getPlugin() {
                    return Activator.PLUGIN_ID;
                }

                public int getSeverity() {
                    return IStatus.ERROR;
                }

                public boolean isMultiStatus() {
                    return false;
                }

                public boolean isOK() {
                    return false;
                }

                public boolean matches(int severityMask) {
                    return false;
                }

            });
        }

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

            //Ask to install, if we have not prev said don't bother us
            if (settings.getBoolean(IDialogConstants.SKIP_UPDATE_DIALOG_ON_STARTUP)){
                logger.debug("Updates dialog skipped.");
            }else{
                logger.debug("Updates dialog should open.");

                Display.getDefault().syncExec(new Runnable(){

                    public void run() {
                        Dialog dlg=new UpdatesAvailableDialog(PlatformUI.getWorkbench().
                                                              getActiveWorkbenchWindow().getShell());

                        int ret=dlg.open();
                        if (ret==Window.CANCEL){
                            setAbort(true);
                        }
                    }

                });

                if (abort==true) return;

            }

            /*        	
        	if (settings.getBoolean(IDialogConstants.REVIEW_UPDATES)){

        		Display.getDefault().syncExec(new Runnable(){

					public void run() {
		        		IAction action=new UpdateAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		          		action.run();
					}

        		});

          		return;
        	}
        	else{
             */
            //Automatically download in background
            for (Iterator iter = installOps.iterator(); iter.hasNext();) {
                IInstallFeatureOperation op
                = (IInstallFeatureOperation) iter.next();
                logger.debug("** Installing feature: "
                             + op.getFeature().getLabel());
                op.execute(monitor, null);
            }

            boolean restartRequired = ls.save();
            logger.debug("Restart required (seems always true): " + restartRequired);
            if (restartRequired){

                //Create new runnable for UI thread sync and confirm dialog
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {

                        if (PlatformUI.getWorkbench()!=null){
                            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()!=null){
                                if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()!=null){


                                    boolean answer=MessageDialog.openQuestion(
                                                                              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                                                              "Restart required",
                                                                              "You are advised to restart Bioclipse in order for all " +
                                                                              "updates to be active.\n\n" +
                                    "Would you like to restart Bioclipse now?");
                                    if (answer){
                                        Activator.getDefault().getWorkbench().restart();
                                    }

                                }else{
                                    Activator.getDefault().getWorkbench().restart();
                                }

                            }else{
                                Activator.getDefault().getWorkbench().restart();
                            }

                        }else{
                            Activator.getDefault().getWorkbench().restart();
                        }
                    }
                });
            }else{
                showMessage("Updates for Bioclipse have been "
                            + "downloaded and installed.");
            }
            //        	}

            /*
        	 //Automatically download in background
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

             */
        }
        else {
            logger.debug("** No features found on update site");
        }

        monitor.done();
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
