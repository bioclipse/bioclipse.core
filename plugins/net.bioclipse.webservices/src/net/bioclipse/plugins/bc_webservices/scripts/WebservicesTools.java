package net.bioclipse.plugins.bc_webservices.scripts;

import java.lang.reflect.Array;

import uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchServiceServiceLocator;

public class WebservicesTools {

	public String downloadPDB(String pdbid){

		if (pdbid==null || pdbid.length()<=0){
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("Please provide a PDB ID.");
			return null;
		}
		
		DbfetchServiceServiceLocator wsdbfetch = new DbfetchServiceServiceLocator();
		try {
			
			String name="pdb:" + pdbid;
			//BioclipseConsole.writeToConsole("Downloading pdb...");
			String[] strarray = wsdbfetch.getUrnDbfetch().fetchData(name, "pdb", "raw");
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("Download finished.");

			if (strarray==null || strarray.length<=0){
				net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("No PDB found with id: " + pdbid);
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
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("There was an error during retrieval: " + e.getMessage());
		}

		return null;
	}

}
