package net.bioclipse.plugins.bc_webservices.scripts;

import java.lang.reflect.Array;

import net.bioclipse.model.INamespaceProvider;
import net.bioclipse.util.BioclipseConsole;
import uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchServiceServiceLocator;

public class WebservicesTools implements INamespaceProvider {

	public String downloadPDB(String pdbid){

		if (pdbid==null || pdbid.length()<=0){
			BioclipseConsole.writeToConsole("Please provide a PDB ID.");
			return null;
		}
		
		DbfetchServiceServiceLocator wsdbfetch = new DbfetchServiceServiceLocator();
		try {
			
			String name="pdb:" + pdbid;
			BioclipseConsole.writeToConsole("Downloading pdb...");
			String[] strarray = wsdbfetch.getUrnDbfetch().fetchData(name, "pdb", "raw");
			BioclipseConsole.writeToConsole("Download finished.");

			if (strarray==null || strarray.length<=0){
				BioclipseConsole.writeToConsole("No PDB found with id: " + pdbid);
				return null;
			}

			String result="";
			for (int i = 1; i < Array.getLength(strarray); i++) {
				if (i != 1)
					result = result + ("\n");
				result = result + strarray[i];
			}

			return result;
			
			
		} catch (Exception e) {
			BioclipseConsole.writeToConsole("There was an error during retrieval: " + e.getMessage());
		}

		return null;
	}

}
