package net.bioclipse.webservices.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.webservices.business.IWebservicesManager;
import net.bioclipse.webservices.business.WebservicesManager;

public class WebservicesManagerPluginTest {

	IWebservicesManager ws;
	
	public WebservicesManagerPluginTest() {
		ws=new WebservicesManager();
	}

	@Test
	public void testDownloadPDB() throws BioclipseException, CoreException, IOException{

		ICDKMolecule mol= ws.downloadPDB("1d66");
		assertNotNull(mol);
		
		//FIXME: correct no atoms
		assertEquals(12345678, mol.getAtomContainer().getAtomCount());
		
	}

	@Test
	public void testDownloadPDBFile() throws BioclipseException, CoreException, IOException{

		//String tempPath="/Virtual/";
		String path= ws.downloadPDBAsFile("1d66");
		assertNotNull(path);
		System.out.println("Path: " + path);
		ICDKMolecule mol = Activator.getDefault().getCDKManager().loadMolecule(path);
		assertNotNull(mol);

		//FIXME: correct no atoms
		assertEquals(12345678, mol.getAtomContainer().getAtomCount());
		
	}

}
