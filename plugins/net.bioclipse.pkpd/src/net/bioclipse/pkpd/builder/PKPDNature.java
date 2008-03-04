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
 ******************************************************************************/
package net.bioclipse.pkpd.builder;


import net.bioclipse.pkpd.Activator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A PKPDNature that registers the PKPDBuilder for a PKPDProject. 
 * Triggers build in background when run. 
 * 
 * @author ola
 */
public class PKPDNature
      implements IProjectNature
{
	
	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "net.bioclipse.pkpd.PKPDNature";

	private static final Logger logger = Activator.getLogManager()
	.getLogger(PKPDNature.class.toString());

	
   private IProject project;

   /**
    * Answer the associated project
    */
   public IProject getProject() {
      return project;
   }

   /**
    * Set the associated project
    */
   public void setProject(IProject project) {
      this.project = project;
   }
   
   /**
    * Configures this nature for its project. This is called by the
    * workspace when natures are added to the project using
    * <code>IProject.setDescription</code>. This nature adds our
    * builder to the projectís build spec and triggers a build in the
    * background when the project is configured.
    */
   public void configure() throws CoreException {
	   PKPDBuilder.addBuilderToProject(project);
      new Job("PBPD Builder") {
         protected IStatus run(IProgressMonitor monitor) {
            try {
               project.build(
            		   PKPDBuilder.FULL_BUILD,
                  PKPDBuilder.BUILDER_ID,
                  null,
                  monitor);
            }
            catch (CoreException e) {
               logger.error(e);
            }
            return Status.OK_STATUS;
         }
      }.schedule();
   }

   /**
    * De-configures this nature for its project. This is called by the
    * workspace when natures are removed from the project using
    * <code>IProject.setDescription</code>. When the nature is
    * removed from the project, the build spec is modified and all
    * audit markers are removed.
    */
   public void deconfigure() throws CoreException {
      PKPDBuilder.removeBuilderFromProject(project);
      PKPDBuilder.deleteAuditMarkers(project);
   }
}
