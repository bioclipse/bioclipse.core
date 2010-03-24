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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.webservices.ResourceCreator;
import net.bioclipse.webservices.services.WSDbfetch;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * A manager to interact with various webservices, currently WSDBfetch is 
 * the only supported.
 * 
 * See http://www.ebi.ac.uk/Tools/webservices/WSDbfetch.html for more info.
 * 
 * @author ola
 *
 */
public class WebservicesManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(WebservicesManager.class);

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "ws";
    }
    
    /**
     * 
     * @param db
     * @param query
     * @param format
     * @param filename
     * @return
     * @throws BioclipseException
     * @throws CoreException
     */
    public String downloadDbEntryAsFile(String db, String query,
                                        String format, String filename)
    throws BioclipseException, CoreException {

        if (filename == null || filename.equals(""))
            filename = query + "." + format;

        String ent = downloadDbEntry(db, query, format);

        if (ResourceCreator.resourceExists(filename)){
            //Remove PDB
            String noext=filename.substring( 0,filename.length()-4 );
            int cnt=2;
            while(ResourceCreator.resourceExists(noext+"_" + cnt + ".pdb")){
                cnt++;
            }
            filename=noext+"_" + cnt + ".pdb";

        }

        try {
            IFile file = ResourceCreator.createResource(filename, ent,
                                                     new NullProgressMonitor());
            return file.getLocation().toOSString();
        } catch (Exception e) {
            throw new BioclipseException(e.getMessage());
        }
    }

    /**
     * 
     * @param db
     * @param query
     * @param format
     * @return
     * @throws BioclipseException
     */
    public String downloadDbEntry(String db, String query,
                                  String format)
    throws BioclipseException{
        if (db == null || db.equals(""))
           throw new BioclipseException("Error, please provide a database ID.");

        if (query == null || query.equals(""))
            throw new BioclipseException("Error, please provide a query ID.");

        if (format == null || format.equals(""))
            throw new BioclipseException("Error, please provide a format ID.");

        try {
            WSDbfetch wsdbfetch = new WSDbfetch();

            String dbq = db + ":" + query;

            String ent = wsdbfetch.fetchData(dbq,
                                      format, "raw", new NullProgressMonitor());

            if (ent==null || ent.equals("")){
                throw new BioclipseException("Error, no entry found with id: " 
                                             + ent);
            }

            return ent;
        } catch (Exception e) {
            throw new BioclipseException(e.getMessage());
        }
    }

    /**
     * 
     * @param db
     * @param query
     * @param format
     * @return
     * @throws BioclipseException
     * @throws CoreException
     */
    public String downloadDbEntryAsFile(String db, String query,
                                        String format)
    throws BioclipseException, CoreException {
        return downloadDbEntryAsFile(db, query, format, "");
    }

    
    /**
     * Download a list of PDBs by ID to a specified location
     * @param pdbids A comma-separated list of PDB IDs to download
     * @param path A workspace-relative path to an IContainer
     * @return 
     * @return
     * @throws BioclipseException 
     * @throws URISyntaxException 
     */
    public List<String> queryPDB( String pdbids, String path )
                                                      throws BioclipseException{
        
        IPath ipath=new Path(ResourcesPlugin.getWorkspace()
                            .getRoot().getLocation().toOSString() );
        ipath=ipath.append( path );
        IContainer container = ResourcesPlugin.getWorkspace()
            .getRoot().getContainerForLocation( ipath );
            
        if (container==null || container.exists()==false)
            throw new BioclipseException( "The location: " + ipath + 
                                          " does not exist." );
        
        return queryPDB( pdbids, container );
        
    }

    /**
     * Download a list of PDBs by ID to a specified IContainer
     * @param pdbids A comma-separated list of PDB IDs to download
     * @param container IContainer where to store results
     * @throws BioclipseException
     */
    public List<String> queryPDB( String pdbids, IContainer container ) 
                                                      throws BioclipseException{
        
        String response = downloadDbEntry( "pdb" , pdbids , "pdb");

        if (response==null || response.length()<=0)
            throw new BioclipseException( "Downloading PDBs query: '" + pdbids 
                                          + "' returned empty content." );

        //CDKManager does not support reading PDBs, we need to split ourselves
        return splitAndSavePDBString(response, container);


        
    }
    
    /**
     * Download a list of PDBs by ID and save in Virtual folder
     * @param pdbids A comma-separated list of PDB IDs to download
     * @return
     * @throws BioclipseException
     */
    public List<String> queryPDB(String pdbids) throws BioclipseException{

      return queryPDB(pdbids, net.bioclipse.core.Activator.getVirtualProject());
        
    }


    private List<String> splitAndSavePDBString( String response, 
                                                IContainer container ) {

        //Split by END
        String[] molstrings = response.split( "END\\s+[\\n|\\r]+" );
        
        logger.debug("Split string into " + molstrings.length + " PDB parts");

        //Store paths here
        List<String> paths=new ArrayList<String>();

        for (String molstring : molstrings){
            //Save this string to a file in Virtual
            
            //Extract first line
            String[] p_firstline=molstring.split( "\n" );
            if (p_firstline.length<=1){
                logger.error( "NO LINES FOUND" );
                return null;
            }
            //get first line
            String firstline=p_firstline[0];
            
            
            
            Pattern myPattern = Pattern.compile(" ([^ ]*([ ])?)$");
            Matcher r = myPattern.matcher( firstline );
            
            //Extract last word from first line = PDBID = filename
            String[] ar = firstline.split( "\\s+", 0 );
            String name = ar[ar.length - 1];
                                                  
            IFile file = container.getFile( new Path( name + ".pdb") );
            
            ByteArrayInputStream bis=new ByteArrayInputStream( 
                                                         molstring.getBytes() );
            
            try {
                if (file.exists()){
                    //Overwrite
                    file.setContents( bis, true, false, new NullProgressMonitor());
                }
                else{
                    file.create( bis, true, new NullProgressMonitor() );
                }
                paths.add( file.getFullPath().toOSString());
            } catch ( CoreException e ) {
                e.printStackTrace();
            }
            
        } 
        
        try {
            container.refreshLocal( 1, new NullProgressMonitor() );
        } catch ( CoreException e ) {
            e.printStackTrace();
        }
        
        return paths;
    }
    

}
