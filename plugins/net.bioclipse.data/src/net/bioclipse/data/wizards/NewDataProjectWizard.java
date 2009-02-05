/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *
 ******************************************************************************/
package net.bioclipse.data.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.data.CopyTools;
import net.bioclipse.data.DummyProgressMonitor;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.osgi.framework.Bundle;

/**
 * Wizard for installing selected folders of data in a new Project.
 * @author ola
 *
 */
@SuppressWarnings("restriction")
public class NewDataProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage fFirstPage;
    private SelectDataFoldersPage folPage;

    private IWorkbench workbench;
    private IStructuredSelection selection;
    private String wizardID;

	private static final Logger logger =
        Logger.getLogger(NewDataProjectWizard.class);


    public NewDataProjectWizard() {
        super();
//        setDefaultPageImageDescriptor();
        setWindowTitle("New Sample Data project");

        fFirstPage = new WizardNewProjectCreationPage("New Sample Data project");
        boolean projectNamedSampleDataExists = false;
        String sampleData = "Sample Data";
        for ( IProject p : ResourcesPlugin.getWorkspace()
                                          .getRoot()
                                          .getProjects(
                                              Project.INCLUDE_HIDDEN) ) {
            if ( sampleData.equals( p.getName() ) ) {
                projectNamedSampleDataExists = true;
            }
        }
        if ( !projectNamedSampleDataExists ) {
            fFirstPage.setInitialProjectName( sampleData );
        }
        folPage=new SelectDataFoldersPage();

    }

    /**
     * Add WizardNewProjectCreationPage from IDE
     */
    public void addPages() {

        fFirstPage.setTitle("New Sample Data project");
        fFirstPage.setDescription("Create a new Project with " +
        "sample data installed");
//        fFirstPage.setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
//        "/org/ananas/xm/eclipse/resources/newproject58.gif"));
        addPage(fFirstPage);

        addPage(folPage);

    }

    
    @Override
    public boolean performCancel() {
    	logger.debug("Installation data wizard cancelled");
    	return true;
    }

    /**
     * Create project and install data
     */
    @Override
    public boolean performFinish() {

        try
        {
            WorkspaceModifyOperation op =
                new WorkspaceModifyOperation()
            {

                @Override
                protected void execute(IProgressMonitor monitor)
                throws CoreException, InvocationTargetException,
                InterruptedException {
                    createProject(monitor != null ?
                            monitor : new NullProgressMonitor());

                }
            };
            getContainer().run(false,true,op);
        }
        catch(InvocationTargetException x)
        {
            LogUtils.debugTrace(logger, x);
            return false;
        }
        catch(InterruptedException x)
        {
            return false;
        }
        return true;     }

    /**
     * Init wizard
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
        setWindowTitle("New Sample Data Project");
//        setDefaultPageImageDescriptor(TBC);
    }


    /**
     * Create project and add required natures, builders, folders, and files
     * @param monitor
     */
    protected void createProject(IProgressMonitor monitor)
    {
    	
        ArrayList<InstallableFolder> folders=folPage.getFolders();
        monitor.beginTask("Copying data",folders.size()+2);
        
        try
        {

            //Get WS root
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            monitor.worked(1);

            //Create the project
            IProject project = root.getProject(fFirstPage.getProjectName());

            //Add natures and builders
            IProjectDescription description = ResourcesPlugin.getWorkspace()
            .newProjectDescription(project.getName());
            if(!Platform.getLocation().equals(fFirstPage.getLocationPath()))
                description.setLocation(fFirstPage.getLocationPath());
            project.create(description,monitor);

            //Open project
            project.open(monitor);

            monitor.worked(1);

            //Copy folders into workspace
            for (InstallableFolder folder : folders){

                if (folder.isChecked()){

                    try {
                        monitor.subTask("Copying folder: " + folder.getName());
                        installFolder(folder, project);
                        monitor.worked(1);
                    } catch (BioclipseException e) {
                        logger.error("Could not copy folder: " +
                                folder.getName());
                    } catch (IOException e) {
                        logger.error("Could not read folder: " +
                                folder.getName());
                    }
                }
            }

            /*
             * Should use status line progress monitor, but how get viewSite?
             *
            IActionBars actionBars = getViewSite().getActionBars();
            IStatusLineManager statusLine = actionBars.getStatusLineManager();
            IProgressMonitor progressMonitor = statusLine.getProgressMonitor();
             */

            //Refresh project
            project.refreshLocal(2, new DummyProgressMonitor());

        }
        catch(CoreException x)
        {
            LogUtils.debugTrace(logger, x);
        }
        finally
        {
            monitor.done();
        }
    }

    /**
     * Copy the folder into the project root
     * @param folder
     * @param project
     * @throws BioclipseException
     * @throws IOException
     */
    private void installFolder(InstallableFolder folder, IProject project)
    throws BioclipseException, IOException {

        URL url=null;
        URL folderURL=null;
        try{
        	Bundle bun = Platform.getBundle(folder.getPluginID());
        	URL urll = bun.getEntry("/" + folder.getLocation());
//            url=Activator.getDefault().getBundle().getEntry("/" + folder.getLocation());
            folderURL=FileLocator.toFileURL(urll);
        }catch (Exception e){
            throw new BioclipseException(e.getMessage());
        }

        File folderFile=new File(folderURL.getFile());
        File destinationFile=new File(project.getLocation().toOSString()+ File.separator + folder.getName());

        if (logger.isDebugEnabled()) {
            logger.debug("Copying folder: " + folderURL.getFile() + " into "
                + project.getLocation().toOSString());
        }

        //Create folder
        if (destinationFile.exists()){
            //TODO: This should not be possible
        }else {
            if (destinationFile.mkdir()==false)
                throw new IOException("Could not make directory: " + folder.getName());
        }

        CopyTools.copy(destinationFile, folderFile);
    }

    /**
     * Create the folder in the closest parent which is a folder
     * @param folder
     * @param monitor
     */
    private void createFolderHelper (IFolder folder, IProgressMonitor monitor)
    {
        try {
            if(!folder.exists()) {
                IContainer parent = folder.getParent();

                if(parent instanceof IFolder
                        && (!((IFolder)parent).exists())) {

                    createFolderHelper((IFolder)parent, monitor);
                }

                folder.create(false,true,monitor);
            }
        } catch (Exception e) {
            LogUtils.debugTrace(logger, e);
        }
    }

    public void setInitializationData(IConfigurationElement config,
    		String propertyName, Object data) throws CoreException {

    	//Cache the ID so we know what ID this wizard has
    	wizardID=config.getAttribute("id");
    }

    public String getWizardID() {
		return wizardID;
	}
    public void setWizardID(String wizardID) {
		this.wizardID = wizardID;
	}


    
    @Override
    public boolean needsProgressMonitor() {
    	return true;
    }

}
