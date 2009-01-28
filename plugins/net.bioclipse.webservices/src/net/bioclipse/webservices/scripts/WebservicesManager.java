package net.bioclipse.webservices.scripts;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.webservices.ResourceCreator;
import net.bioclipse.webservices.services.WSDbfetch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class WebservicesManager implements IWebservicesManager{

    public String getNamespace() {
        return "webservices";
	}
    
    public void downloadPDB(String pdbid, String filename) throws BioclipseException, CoreException{

		if (pdbid == null || pdbid.equals(""))
			throw new BioclipseException("Error, please provide a PDB ID.");
		
		if (filename == null || filename.equals(""))
			filename = pdbid + ".pdb";
		
		if (filename.endsWith(".pdb") == false)
			filename = filename+".pdb";
		
		if (ResourceCreator.resourceExists(filename))
			throw new BioclipseException("Error, the file "
					+ filename + " exists already.");
		
		try {
			WSDbfetch wsdbfetch = new WSDbfetch();
			
			pdbid = "pdb:" + pdbid;
			
			String pdb_file = wsdbfetch.fetchData(pdbid,
					"pdb", "raw", new NullProgressMonitor());

			if (pdb_file==null || pdb_file.equals("")){
				throw new BioclipseException("Error, no PDB found with id: " + pdbid);
			}
			
			ResourceCreator.createResource(filename, pdb_file,
					new NullProgressMonitor());
		} catch (Exception e) {
			throw new BioclipseException(e.getMessage());
		}
	}

}
