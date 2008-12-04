package net.bioclipse.webservices.scripts;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import uk.ac.ebi.www.ws.services.WSDbfetch.WSDbfetchSoapBindingStub;

public class WebservicesManager implements IWebservicesManager{

    public String getNamespace() {
        return "webservices";
    }

    public void downloadPDB(String pdbid, String filename) throws BioclipseException, CoreException{

		if (pdbid==null || pdbid.length()<=0){
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("Please provide a PDB ID.");
		}
		
		try {
			WSDbfetchSoapBindingStub wsdbfetch = new WSDbfetchSoapBindingStub();
			
			String name="pdb:" + pdbid;
			String pdb_file = wsdbfetch.fetchData(name, "pdb", "raw");
			net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("Download finished.");

			if (pdb_file==null || pdb_file.length()<=0){
				net.bioclipse.ui.Activator.getDefault().CONSOLE.echo("No PDB found with id: " + pdbid);
			}

			if(filename.indexOf(".pdb")==-1)
				filename=filename+".pdb";
			IFile target=ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filename));
			if(target.exists()){
				throw new BioclipseException("File already exists!");
			}
			IProgressMonitor monitor = new NullProgressMonitor();
			try {
				int ticks = 10000;
				monitor.beginTask("Writing file", ticks);
				try {
					target.create(new ByteArrayInputStream(pdb_file.getBytes("US-ASCII")), false,
						monitor);
				} catch (UnsupportedEncodingException e) {
					throw new BioclipseException(e.getMessage());
				}
				monitor.worked(ticks);
			} finally {
				monitor.done();
			}

			
			
		} catch (Exception e) {
			throw new BioclipseException(e.getMessage());
		}
	}

}
