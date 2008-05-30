package net.bioclipse.cdk10.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;


public class CDK10Manager {

    private static final Logger logger = Logger.getLogger(CDK10Manager.class);
    
    ReaderFactory readerFactory;

    public String getNamespace() {
        return "cdk10";
    }

    
    /**
     * Load one or more molecules from an InputStream and return a CDKMoleculeList.
     */
/*    public List loadMolecules(InputStream instream) throws IOException, BioclipseException {

        if (readerFactory==null){
            readerFactory=new ReaderFactory();
            CDK10ManagerHelper.registerFormats(readerFactory);
        }

        System.out.println("no formats supported: " + readerFactory.getFormats().size());
//        System.out.println("format guess: " + readerFactory.guessFormat(instream).getFormatName());

        //Create the reader
        IChemObjectReader reader= readerFactory.createReader(instream);

        if (reader==null){
            throw new BioclipseException("Could not create reader in CDK. ");
        }

        IChemFile chemFile = new org.openscience.cdk.ChemFile();

        // Do some customizations...
        CDK10ManagerHelper.customizeReading(reader, chemFile);

        //Read file
        try {
            chemFile=(IChemFile)reader.read(chemFile);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            LogUtils.debugTrace(logger, e);
        }

        //Store the chemFormat used for the reader
        IResourceFormat chemFormat=reader.getFormat();
        System.out.println("Rad CDK chemfile with format: " + chemFormat.getFormatName());

        List<IAtomContainer> atomContainersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        int nuMols=atomContainersList.size();
        System.out.println("This file contained: " + nuMols + " molecules");

        List moleculesList=new ArrayList();
//        CDKMolecule[] moleculesData = new CDKMolecule[atomContainersList.size()];

        for (int i=0; i<atomContainersList.size();i++){
            IAtomContainer ac=null;
            Object obj=atomContainersList.get(i);
            if (obj instanceof org.openscience.cdk.interfaces.IMolecule) {
                ac=(org.openscience.cdk.interfaces.IMolecule)obj;
            }else if (obj instanceof IAtomContainer) {
                ac=(IAtomContainer)obj;
            }
            
            moleculesList.add(ac);
        }
        
        return moleculesList;
    }
    
*/    
    /**
     * Load one or more molecules from an InputStream and return a CDKMoleculeList.
     */
    public BioList<CDK10Molecule> loadMolecules(InputStream instream)
        throws IOException, BioclipseException {

        if (readerFactory==null){
            readerFactory=new ReaderFactory();
            CDK10ManagerHelper.registerFormats(readerFactory);
        }

        System.out.println("no formats supported: "
                           + readerFactory.getFormats().size());
//        System.out.println("format guess: " + readerFactory.guessFormat(instream).getFormatName());

        //Create the reader
        IChemObjectReader reader= readerFactory.createReader(instream);

        if (reader==null){
            throw new BioclipseException("Could not create reader in CDK. ");
        }

        IChemFile chemFile = new org.openscience.cdk.ChemFile();

        // Do some customizations...
        CDK10ManagerHelper.customizeReading(reader, chemFile);

        //Read file
        try {
            chemFile=(IChemFile)reader.read(chemFile);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            LogUtils.debugTrace(logger, e);
        }

        //Store the chemFormat used for the reader
        IResourceFormat chemFormat=reader.getFormat();
        System.out.println("Rad CDK chemfile with format: " + chemFormat.getFormatName());

        List<IAtomContainer> atomContainersList
            = ChemFileManipulator.getAllAtomContainers(chemFile);
        int nuMols=atomContainersList.size();
        System.out.println("This file contained: " + nuMols + " molecules");

        BioList<CDK10Molecule> moleculesList=new BioList<CDK10Molecule>();

        for (int i=0; i<atomContainersList.size();i++){
            IAtomContainer ac=null;
            Object obj=atomContainersList.get(i);
            if (obj instanceof org.openscience.cdk.interfaces.IMolecule) {
                ac=(org.openscience.cdk.interfaces.IMolecule)obj;
            }else if (obj instanceof IAtomContainer) {
                ac=(IAtomContainer)obj;
            }

            CDK10Molecule mol=new CDK10Molecule(ac);
            String moleculeName="Molecule " + i; 
            if (ac instanceof IMolecule) {
                org.openscience.cdk.interfaces.IMolecule imol
                    = (org.openscience.cdk.interfaces.IMolecule) ac;
                String molName=(String) imol.getProperty(CDKConstants.TITLE);
                if (molName!=null && (!(molName.equals("")))){
                    moleculeName=molName;
                }
            }
            mol.setName(moleculeName);
            
            moleculesList.add(mol);
        }
        
        return moleculesList;
    }
}
