/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 *******************************************************************************/

package net.bioclipse.pkpd.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import net.bioclipse.pkpd.builder.PKPDBuilder;
import net.bioclipse.pkpd.builder.PKPDNature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 *A NewWizard to create a new PKPDProject
 *
 * @author ola
 */
public class NewPKPDProjectWizard extends Wizard implements INewWizard {


	private WizardNewProjectCreationPage fFirstPage;

	private IWorkbench workbench;
	private IStructuredSelection selection;

	public NewPKPDProjectWizard() {
		super();
//		setDefaultPageImageDescriptor();
		setWindowTitle("New PKPD project"); 
	}

	/**
	 * Add WizardNewProjectCreationPage from IDE
	 */
	public void addPages() {

		super.addPages();
		fFirstPage = new WizardNewProjectCreationPage("New PKPD project");
		fFirstPage.setTitle("New PKPD project");
		fFirstPage.setDescription("Create a new PKPD project");
//		fFirstPage.setImageDescriptor(ImageDescriptor.createFromFile(getClass(),
//		"/org/ananas/xm/eclipse/resources/newproject58.gif"));        

		addPage(fFirstPage);

	}

	/**
	 * Create project and add PKPDNature
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
			x.printStackTrace();
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
		setWindowTitle("New PKPD Project");
//		setDefaultPageImageDescriptor(TBC);
	}


	/**
	 * Create project and add required natures, builders, folders, and files
	 * @param monitor
	 */
	protected void createProject(IProgressMonitor monitor)
	{
		monitor.beginTask("Creating PKPD project",50);
		try
		{

			//Get WS root
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			monitor.subTask("Creating directories");

			//Create the project
			IProject project = root.getProject(fFirstPage.getProjectName());

			//Add natures and builders
			IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
			if(!Platform.getLocation().equals(fFirstPage.getLocationPath()))
				description.setLocation(fFirstPage.getLocationPath());
			description.setNatureIds(new String[] { PKPDNature.NATURE_ID });
			ICommand command = description.newCommand();
			command.setBuilderName(PKPDBuilder.BUILDER_ID);
			description.setBuildSpec(new ICommand[] { command });
			project.create(description,monitor);

			monitor.worked(10);

			//Open project
			project.open(monitor);

			//Set persistent properties (not used here/yet)
//			project.setPersistentProperty(PluginConstants.SOURCE_PROPERTY_NAME,"src");
//			project.setPersistentProperty(PluginConstants.RULES_PROPERTY_NAME,"rules");
//			project.setPersistentProperty(PluginConstants.PUBLISH_PROPERTY_NAME,"publish");
//			project.setPersistentProperty(PluginConstants.BUILD_PROPERTY_NAME,"false");

			monitor.worked(10);
			
			//Create any desired folders
			IPath projectPath = project.getFullPath(),
			fol1Path = projectPath.append("folder1"),
			fol2Path = projectPath.append("folder2");
			IFolder fol1Folder = root.getFolder(fol1Path),
			fol2Folder = root.getFolder(fol2Path);
			createFolderHelper(fol1Folder,monitor);
			createFolderHelper(fol2Folder,monitor);

			monitor.worked(10);

			//Create files (pkpd.xml) in project root
			monitor.subTask("Creating files");
			IPath pkpdPath = projectPath.append("pkpd.xml");
			IFile pkpdFile = root.getFile(pkpdPath);
			InputStream pkpdIS = getClass().getResourceAsStream("/net/bioclipse/pkpd/resources/pkpd.xml");
			pkpdFile.create(pkpdIS,true,new SubProgressMonitor(monitor,10));
			pkpdIS.close();
		}
		catch(CoreException x)
		{
			x.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			monitor.done();
		}
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
			e.printStackTrace();
		}
	}

}
