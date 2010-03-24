/* *****************************************************************************
 * Copyright (c) 2009  Ola Spjuth <ola@bioclipse.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.webservices.business;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Contains webservices related methods"
)
public interface IWebservicesManager extends IBioclipseManager {

      /**
       * Retrieves an entry from a database with the WSDbfetch EBI Web service.
       * The result is dumped in the Webservice's result folder.
       * @param db The database identifier
       * @param query The entry identifier
       * @param format The format
       * @param filename The name of the file to create
       * @return The resource that contains the result
       * @throws BioclipseException
       * @throws CoreException
       */
      @Recorded
      @PublishedMethod( 
          params = "String db, String query, String format, String filename", 
          methodSummary = "Fetches an entry from the EBI databases and saves " +
          		          "it under the specified filename" )
      public String downloadDbEntryAsFile( String db, String query,
                                           String format, String filename )
                    throws BioclipseException, CoreException;
      
      /**
       * Retrieves an entry from a database with the WSDbfetch EBI Web service.
       * The result is dumped in the Webservice's result folder.
       * @param db The database identifier
       * @param query The entry identifier
       * @param format The format
       * @return The resource that contains the result
       * @throws BioclipseException
       * @throws CoreException
       */
      @Recorded
      @PublishedMethod( 
          params = "String db, String query, String format", 
          methodSummary = "Fetches an entry from the EBI databases and " +
          		          "saves it in the Webservices result folder" )
      public String downloadDbEntryAsFile( String db, String query,
                                           String format )
                    throws BioclipseException, CoreException;
      
      /**
       * Retrieves an entry from a database with the WSDbfetch EBI Web service. 
       * The result is dumped in the virtual folder.
       * @param db The database identifier
       * @param query The entry identifier
       * @param format The format
       * @return The database entry
       * @throws BioclipseException
       * @throws CoreException
       */
      @Recorded
      @PublishedMethod( 
          params = "String db, String query, String format", 
          methodSummary = "Fetches an entry from the EBI databases and " +
          		          "saves it in the Virtual folder" )
      public String downloadDbEntry(String db, String query, String format)
                    throws BioclipseException, CoreException;
      
      /**
       * 
       * @param pdbids A comma-separated list of PDB IDs to download
       * @return
       * @throws BioclipseException
       */
      @Recorded
      @PublishedMethod(
          params = "String pdbids",
          methodSummary = "Query and retrieve PDB files using the" +
                          "WSDbfetch service at EBI." )
      public List<String> queryPDB(String pdbids)
                          throws BioclipseException;
      
      /**
       * 
       * @param pdbids A comma-separated list of PDB IDs to download
       * @param path Workspace-relative path to a folder where to save
       * @return
       * @throws BioclipseException
       */
      @Recorded
      @PublishedMethod(
          params = "String pdbids, String path ",
          methodSummary = "Query and retrieve PDB files using the" +
                          "WSDbfetch service at EBI to a specified folder.")
      public List<String> queryPDB( String pdbids, String path ) 
                          throws BioclipseException;

      /**
       * 
       * @param pdbids A comma-separated list of PDB IDs to download
       * @param container A container in the workspace where resources should 
       * be saved
       * @return
       * @throws BioclipseException
       */
      public List<String> queryPDB( String pdbids, IContainer container ) 
                          throws BioclipseException;
}
