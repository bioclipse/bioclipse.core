package net.bioclipse.webservices.scripts;

import java.lang.reflect.Array;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import net.bioclipse.core.business.BioclipseException;
import uk.ac.ebi.www.ws.services.urn.Dbfetch.DbfetchServiceServiceLocator;

public class WebservicesManager implements IWebservicesManager{

    public String getNamespace() {
        return "webservices";
    }

    public String downloadPDB(String pdbid) throws BioclipseException{

		if (pdbid==null || pdbid.length()<=0){
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("Please provide a PDB ID.");
			return null;
		}
		
		DbfetchServiceServiceLocator wsdbfetch = new DbfetchServiceServiceLocator();
		try {
			
			String name="pdb:" + pdbid;
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
			
			
		} catch (ServiceException e) {
			throw new BioclipseException(e.getMessage());
		} catch (RemoteException e) {
			throw new BioclipseException(e.getMessage());
		}
	}

}
