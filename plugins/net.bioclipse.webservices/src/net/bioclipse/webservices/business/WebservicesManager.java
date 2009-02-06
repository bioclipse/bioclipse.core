package net.bioclipse.webservices.business;

import java.io.IOException;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.webservices.ResourceCreator;
import net.bioclipse.webservices.services.WSDbfetch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class WebservicesManager implements IWebservicesManager{

    public String getNamespace() {
        return "webservices";
	}

    public ICDKMolecule downloadPDB(String pdbid)
    	throws BioclipseException, CoreException, IOException {

    	if (pdbid == null || pdbid.equals(""))
    		throw new BioclipseException("Error, please provide a PDB ID.");

    	String ent = downloadDbEntry("pdb", pdbid,
    			"pdb");
    	
    	ICDKMolecule mol= Activator.getDefault().getCDKManager().fromString(ent);
    	
    	return mol;
    }

    public String downloadPDBFile(String pdbid, String filename)
    	throws BioclipseException, CoreException {

		if (pdbid == null || pdbid.equals(""))
			throw new BioclipseException("Error, please provide a PDB ID.");
		
		IFile savedfile = downloadDbFile("pdb", pdbid,
	    		"pdb", filename);
		
		return "Downloaded PDB is available at: " + savedfile.getLocation();
		
	}


    public IFile downloadDbFile(String db, String query,
    		String format, String filename)
		throws BioclipseException, CoreException {

		if (filename == null || filename.equals(""))
			filename = query + "." + format;
    	
    	String ent=downloadDbEntry(db, query, format, filename);

		if (ResourceCreator.resourceExists(filename))
			throw new BioclipseException("Error, the file "
					+ filename + " exists already.");

    	try {
			
			return ResourceCreator.createResource(filename, ent,
					new NullProgressMonitor());
		} catch (Exception e) {
			throw new BioclipseException(e.getMessage());
		}
    }
    
    public String downloadDbEntry(String db, String query,
    		String format, String filename)
		throws BioclipseException, CoreException {
    	if (db == null || db.equals(""))
			throw new BioclipseException("Error, please provide a database ID.");
    	
    	if (query == null || query.equals(""))
			throw new BioclipseException("Error, please provide a query ID.");
    	
    	if (format == null || format.equals(""))
			throw new BioclipseException("Error, please provide a format ID.");
		
		
/*		if (filename.endsWith("." + format) == false)
			filename = filename + "." + format;*/
		
		try {
			WSDbfetch wsdbfetch = new WSDbfetch();
			
			String dbq = db + ":" + query;
			
			String ent = wsdbfetch.fetchData(dbq,
					format, "raw", new NullProgressMonitor());

			if (ent==null || ent.equals("")){
				throw new BioclipseException("Error, no entry found with id: " + ent);
			}
			
			return ent;
		} catch (Exception e) {
			throw new BioclipseException(e.getMessage());
		}
    }
    
    public String downloadDbEntry(String db, String query,
    		String format)
		throws BioclipseException, CoreException {
    	return downloadDbEntry(db, query, format, "");
    }

}
