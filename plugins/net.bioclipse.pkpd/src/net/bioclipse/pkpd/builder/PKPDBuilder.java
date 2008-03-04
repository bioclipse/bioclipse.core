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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import net.bioclipse.pkpd.Activator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * A Builder for PKPD projects that synchronizes the project file 
 * PKPD.xml and builds derived resources
 * 
 * @author ola
 *
 */
public class PKPDBuilder extends IncrementalProjectBuilder
{
	
	private static final Logger logger = Activator.getLogManager()
	.getLogger(PKPDBuilder.class.toString());

	
   public static final int MISSING_KEY_VIOLATION = 1;
   public static final int UNUSED_KEY_VIOLATION = 2;

   /**
    * The unique identifier for this builder
    */
	public static final String BUILDER_ID = "net.bioclipse.pkpd.PKPDBuilder";
   
   /**
    * The unique identifier for the audit marker
    */
	private static final String MARKER_ID = "net.bioclipse.pkpd.PKPDMarker";

   // auditMarker attributes
   public static final String KEY = "key";
   public static final String VIOLATION = "violation";
   
   /**
    * Simple data structure class containing a key and the location of
    * that key in a file
    */
   private class Location
   {
      IFile file;
      String key;
      int charStart;
      int charEnd;
   }
   
   /**
    * When called by Eclipse, this builder should perform an audit as
    * necessary. If the build kind is <code>INCREMENTAL_BUILD</code>
    * or <code>AUTO_BUILD</code>, the <code>getDelta</code>
    * method can be used during the invocation of this method to
    * obtain information about what changes have occurred since the
    * last invocation of this method. After completing a build, this
    * builder may return a list of projects for which it requires a
    * resource delta the next time it is run.
    * 
    * @param kind the kind of build being requested. Valid values are
    *           <ul>
    *           <li><code>FULL_BUILD</code>- indicates a full
    *           build.</li>
    *           <li><code>INCREMENTAL_BUILD</code>- indicates an
    *           incremental build. </li>
    *           <li><code>AUTO_BUILD</code>- indicates an
    *           automatically triggered incremental build
    *           (autobuilding on).</li>
    *           </ul>
    * @param args a table of builder-specific arguments keyed by
    *           argument name (key type: <code>String</code>, value
    *           type: <code>String</code>); <code>null</code> is
    *           equivalent to an empty map
    * @param monitor a progress monitor, or <code>null</code> if
    *           progress reporting and cancellation are not desired
    * @return the list of projects for which this builder would like
    *         deltas the next time it is run or <code>null</code> if
    *         none
    * @exception CoreException if this build fails.
    * @see IProject#build(int, String, Map, IProgressMonitor)
    */
   protected IProject[] build(
      int kind, 
      Map args,
      IProgressMonitor monitor
   ) throws CoreException
   {
      if (shouldBuild(kind)) {
         ResourcesPlugin.getWorkspace().run(
            new IWorkspaceRunnable() {
               public void run(IProgressMonitor monitor)
                  throws CoreException
               {
                  buildPKPDfile(monitor);
               }
            }, 
            monitor
         );
      }
      return null;
   }
   
   /**
    * Clean is an opportunity for a builder to discard any additional state that has 
    * been computed as a result of previous builds. It is recommended that builders 
    * override this method to delete all derived resources created by previous builds, 
    * and to remove all markers of type <code>IMarker.PROBLEM</code> that 
    * were created by previous invocations of the builder. The platform will
    * take care of discarding the builder's last built state (there is no need
    * to call <code>forgetLastBuiltState</code>).
    * 
    * In our case, we already scrub the project as part of the FULL_BUILD
    * so no additional work needed.
    */
   protected void clean(IProgressMonitor monitor) throws CoreException {
      // no additional work needed here
   }

   /**
    * Determines whether files should be built by checking for
    * FULL_BUILD, or if the pkpd.xml has changed.
    * 
    * @param kind the kind of build
    * @return <code>true</code> if file should be audited, else
    *         <code>false</code>.
    */
   private boolean shouldBuild(int kind) {
      if (kind == FULL_BUILD)
         return true;
      IResourceDelta delta = getDelta(getProject());
      if (delta == null)
         return false;
      IResourceDelta[] children = delta.getAffectedChildren();
      for (int i = 0; i < children.length; i++) {
         IResourceDelta child = children[i];
         String fileName = child.getProjectRelativePath().lastSegment();
         if (fileName.equals("pkpd.xml"))
            return true;
      }
      return false;
   }
   
   /**
    * Scan the plugin.xml and plugin.properties files and correlate
    * the key/value pairs; any keys appearing in plugin.xml should
    * have a corresponding key/value pair in plugin.properties. Before
    * each lengthy operation, we check to see if the build has been
    * interrupted or canceled. After each lengthy operation, we report
    * progress to the user; while this is not strictly necessary, it
    * is certainly polite. If you do prematurely exit your build
    * process, you may need to call forgetLastBuildState() before
    * exiting so that a full rebuild will be performed the next time.
    * 
    * @param monitor the progress monitor
    */
   private void buildPKPDfile(IProgressMonitor monitor) {
      monitor.beginTask("Building PKPD project", 4);

      if (!deleteAuditMarkers(getProject()))
         return; 
      
      if (checkCancel(monitor))
         return;
      
      //Get and parse pkpd.xml from file
      Map pluginKeys = scanPlugin(getProject().getFile("pkpd.xml"));
      monitor.worked(1);
      
      if (checkCancel(monitor))
         return;
      
      System.out.println("Build PKPD derived resources here!");
      
      monitor.done();
   }

   /**
    * Check to see if the build operation in progress was canceled by
    * the user or should be canceled because another builder needs
    * access to the workspace.
    * 
    * @param monitor the progress monitor
    * @return <code>true</code> if the build operation should stop
    */
   private boolean checkCancel(IProgressMonitor monitor) {
      if (monitor.isCanceled()) {
         // Discard build state if necessary.
         throw new OperationCanceledException();
      }

      if (isInterrupted()) {
         // Discard build state if necessary.
         return true;
      }
      return false;
   }
   
   /**
    * Scan the specified pkpd.xml file
    * 
    * @param file the pkpd.xml file to be scanned
    * @return ???
    */
   private Map scanPlugin(IFile file) {
      Map keys = new HashMap();
      String content = readFile(file);
      int start = 0;
      
      //TODO: fix this for a pkpd.xml file!!
      
      while (true) {
         start = content.indexOf("\"%", start);
         if (start < 0)
            break;
         int end = content.indexOf('"', start + 2);
         if (end < 0)
            break;
         Location loc = new Location();
         loc.file = file;
         loc.key = content.substring(start + 2, end);
         loc.charStart = start + 1;
         loc.charEnd = end;
         keys.put(loc.key, loc);
         start = end + 1;
      }
      return keys;
   }

   
   /**
    * Read the content of the specified file into memory.
    * 
    * @param file the file to be read
    * @return the file content as a string
    */
   private String readFile(IFile file) {
      if (!file.exists())
         return "";
      InputStream stream = null;
      try {
         stream = file.getContents();
         Reader reader =
            new BufferedReader(
               new InputStreamReader(stream));
         StringBuffer result = new StringBuffer(2048);
         char[] buf = new char[2048];
         while (true) {
            int count = reader.read(buf);
            if (count < 0)
               break;
            result.append(buf, 0, count);
         }
         return result.toString();
      }
      catch (Exception e) {
         logger.error(e);
         return "";
      }
      finally {
         try {
            if (stream != null)
               stream.close();
         }
         catch (IOException e) {
        	 logger.error(e);
            return "";
         }
      }
   }
   

   ////////////////////////////////////////////////////////////////////////////
   //
   // Marker methods
   //
   ////////////////////////////////////////////////////////////////////////////
   
   /**
    * Delete all audit markers in the specified project
    * 
    * @param project the project to be modified
    * @return <code>true</code> if successful, else
    *         <code>false</code>
    */
   public static boolean deleteAuditMarkers(IProject project) {
      try {
         project.deleteMarkers(
            MARKER_ID, false, IResource.DEPTH_INFINITE);
         return true;
      }
      catch (CoreException e) {
    	  logger.error(e);
         return false;
      }
   }
   
   ////////////////////////////////////////////////////////////////////////////
   //
   // Utility methods
   //
   ////////////////////////////////////////////////////////////////////////////

   /**
    * Add this builder to the specified project if possible.
    * Do nothing if the builder has already been added.
    *  
    * @param project the project (not <code>null</code>)
    */
   @SuppressWarnings("unchecked")
public static void addBuilderToProject(IProject project) {
      
      // Cannot modify closed projects.
      if (!project.isOpen())
         return;

      // Get the description.
      IProjectDescription description;
      try {
         description = project.getDescription();
      }
      catch (CoreException e) {
    	  logger.error(e);
         return;
      }
      
      // Look for builder already associated.
      ICommand[] cmds = description.getBuildSpec();
      for (int j = 0; j < cmds.length; j++)
         if (cmds[j].getBuilderName().equals(BUILDER_ID))
            return;

      // Associate builder with project.
      ICommand newCmd = description.newCommand();
      newCmd.setBuilderName(BUILDER_ID);
      List newCmds = new ArrayList();
      newCmds.addAll(Arrays.asList(cmds));
      newCmds.add(newCmd);
      description.setBuildSpec(
         (ICommand[]) newCmds.toArray(
            new ICommand[newCmds.size()]));
      try {
         project.setDescription(description, null);
      }
      catch (CoreException e) {
    	  logger.error(e);
      }
   }
   
   /**
    * Determine if the specified project has the receiver associated
    * with it.
    * 
    * @param project the project to test
    * @return <code>true</code> if the specified project is open and
    *         accessible and has the builder associated with it, else
    *         <code>false</code>
    */
   public static boolean hasBuilder(IProject project) {
      
      // Cannot modify closed projects.
      if (!project.isOpen())
         return false;

      // Get the description.
      IProjectDescription description;
      try {
         description = project.getDescription();
      }
      catch (CoreException e) {
    	  logger.error(e);
         return false;
      }
      
      // Look for builder already associated.
      ICommand[] cmds = description.getBuildSpec();
      for (int j = 0; j < cmds.length; j++)
         if (cmds[j].getBuilderName().equals(BUILDER_ID))
            return true;
      return false;
   }
   
   /**
    * Remove this builder from the specified project if possible.
    * Do nothing if the builder has already been removed.
    *  
    * @param project the project (not <code>null</code>)
    */
   @SuppressWarnings("unchecked")
public static void removeBuilderFromProject(IProject project) {

      // Cannot modify closed projects.
      if (!project.isOpen())
         return;
      
      // Get the description.
      IProjectDescription description;
      try {
         description = project.getDescription();
      }
      catch (CoreException e) {
    	  logger.error(e);
         return;
      }
      
      // Look for builder.
      int index = -1;
      ICommand[] cmds = description.getBuildSpec();
      for (int j = 0; j < cmds.length; j++) {
         if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
            index = j;
            break;
         }
      }
      if (index == -1)
         return;
         
      // Remove builder from project.
      List newCmds = new ArrayList();
      newCmds.addAll(Arrays.asList(cmds));
      newCmds.remove(index);
      description.setBuildSpec(
         (ICommand[]) newCmds.toArray(
            new ICommand[newCmds.size()]));
      try {
         project.setDescription(description, null);
      }
      catch (CoreException e) {
    	  logger.error(e);
      }
   }
}
