/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

public class CDK10Manager{

    private static final Logger logger = Logger.getLogger(CDK10Manager.class);
    
    ReaderFactory readerFactory;

    public String getNamespace() {
        return "cdk";
    }



    /**
     * Load a molecule from an InputStream. If many molecules, just return
     * first. To return list of molecules, use loadMolecules(...)
     */
    public CDK10Molecule loadMolecule(InputStream instream)
        throws IOException, BioclipseException {

        if (readerFactory==null){
            readerFactory=new ReaderFactory();
            CDK10ManagerHelper.registerFormats(readerFactory);
        }

        //Create the reader
        IChemObjectReader reader= readerFactory.createReader(instream);

        if (reader==null) {
            throw new BioclipseException("Could not create reader in CDK.");
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
        System.out.println("Rad CDK chemfile with format: "
                           + chemFormat.getFormatName());

        List<IAtomContainer> atomContainersList
            = ChemFileManipulator.getAllAtomContainers(chemFile);
        int nuMols=atomContainersList.size();
        System.out.println("This file contained: " + nuMols + " molecules");

        //If we have one AtomContainer, return a CDKMolecule with this ac
        //If we have more than one AtomContainer, return a list of the molecules
        //FIXME: requires common interface for CDKImplementations
        
        if (atomContainersList.size()==1){
            CDK10Molecule retmol
                = new CDK10Molecule((IAtomContainer)atomContainersList.get(0));
            return retmol;
        }
        
        List moleculesList=new BioList<CDK10Molecule>();

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
        
        //Just return the first molecule. To return all, use loadMolecules(..)
        return (CDK10Molecule) moleculesList.get(0);
    }

    /**
     * Load one or more molecules from an InputStream and return a CDKMoleculeList.
     */
    public List loadMolecules(InputStream instream) throws IOException, BioclipseException {

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

        List moleculesList=new BioList<IBioObject>();
//        CDKMolecule[] moleculesData = new CDKMolecule[atomContainersList.size()];

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
                org.openscience.cdk.interfaces.IMolecule imol = (org.openscience.cdk.interfaces.IMolecule) ac;
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

    public String calculateSmiles(IMolecule molecule) throws BioclipseException {
        return molecule.getSmiles();
    }

    /**
     * Save the molecule to specified file
     * @param molecule
     * @param file
     * @throws IllegalStateException
     */
    public void saveMolecule(CDK10Molecule molecule, IFile file) throws IllegalStateException {
        // TODO Auto-generated method stub

    }

    /**
     * Create molecule from SMILES.
     * @throws BioclipseException 
     */
    public CDK10Molecule createMoleculeFromSMILES(String SMILES) throws BioclipseException {
        SmilesParser parser=new SmilesParser(DefaultChemObjectBuilder.getInstance());
        try {
            org.openscience.cdk.interfaces.IMolecule mol=parser.parseSmiles(SMILES);
            return new CDK10Molecule(mol);
        } catch (InvalidSmilesException e) {
            throw new BioclipseException("SMILES string is invalid");
        }
        
    }

    public Iterator<IMolecule> creatMoleculeIterator(InputStream instream) {
        return new IteratingBioclipseMDLReader(instream, NoNotificationChemObjectBuilder.getInstance());
    }

    class IteratingBioclipseMDLReader implements Iterator<IMolecule> {

        IteratingMDLReader reader;
        
        public IteratingBioclipseMDLReader(InputStream input, IChemObjectBuilder builder) {
            reader = new IteratingMDLReader(input, builder);
        }

        public boolean hasNext() {
            return reader.hasNext();
        }

        public IMolecule next() {
            org.openscience.cdk.interfaces.IMolecule cdkMol = (org.openscience.cdk.interfaces.IMolecule)reader.next();
            IMolecule bioclipseMol = new CDK10Molecule(cdkMol);
            return bioclipseMol;
        }

        public void remove() {
            reader.remove();
        }
    }

    public void saveMoleculesAsSDF( final List<CDK10Molecule> mols,
                                    final String filename )
                        throws InvocationTargetException, InterruptedException {

        //Get 
        
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation(){

            @Override
            protected void execute( IProgressMonitor monitor )
                      throws CoreException, InvocationTargetException,
                                                         InterruptedException {

                monitor.beginTask( "Saving file: " 
                                   + filename, 4 );
                
                //Create File to write
                File writeFile=new File(filename);
                if (writeFile.exists()){
                    //TODO: confirm overwrite
                }
                else {
                    try {
                        writeFile.createNewFile();
                    } catch ( IOException e ) {
                        logger.error("Error creating file: " + filename);
                        throw new InvocationTargetException(e);
                    }
                }

                
                monitor.subTask( "Serializing molecules" );
                monitor.worked( 1 );
                
                //Collect all ACs in chemfile
                IMoleculeSet ms= new MoleculeSet();
                for (CDK10Molecule cdk10mol : mols){
                    ms.addAtomContainer( cdk10mol.getAtomContainer() );
                }

//                IChemModel model=new ChemModel();
//                model.setMoleculeSet( ms );

                monitor.worked( 1 );

                //Serialize ChemFile to SDF as byte[]
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                SDFWriter writer=new SDFWriter();
                try {
                    writer.setWriter( bos );
                } catch ( CDKException e ) {
                    logger.error("Error creating writer for SDFWriter file: " 
                                 + filename + ". MSG: " + e.getMessage());
                    throw new InvocationTargetException(e);
                }
                
//                MDLWriter writer=new MDLWriter(bos);
                //FIXME: CDK don't save properties, so need workaround
                
                
                try {
                    writer.write( ms );
                } catch ( CDKException e ) {
                    logger.debug("Error serializing using MDLWriter: " + filename);
                    throw new InvocationTargetException(e);
                }

                //Get result from outputStream
                byte[] buffer = bos.toByteArray();

                monitor.subTask( "Writing to file" );
                monitor.worked( 1 );

                
                //Write contents to file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(writeFile);
                    fos.write( buffer );
                } catch ( FileNotFoundException e ) {
                    logger.debug("File not found: " + filename);
                    throw new InvocationTargetException(e);
                } catch ( IOException e ) {
                    logger.debug("Error writing file: " + filename + ". " 
                                 + e.getMessage());
                    throw new InvocationTargetException(e);
                }finally{
                    try {
                        fos.close();
                    } catch ( IOException e ) {
                        logger.debug("Error closing file: " + filename + ". " 
                                     + e.getMessage());
                        throw new InvocationTargetException(e);
                    }
                }
                
                //We need to refresh the workspace for the project containing
                //the created file.
                //FIXME
  
                //Using eclipse resources below. Not used currently.
                //Set up an inputStream
//                ByteArrayInputStream bis=new ByteArrayInputStream(buffer);
//                file.setContents( bis, false, false, monitor );

                
                logger.debug( "Wrote file: " +filename );
                monitor.done();

            }
            
        };

        PlatformUI.getWorkbench().getProgressService().run(true,false,op);

    }



    /**
     * Create molecule from String
     * @throws BioclipseException 
     * @throws BioclipseException 
     * @throws IOException 
     */
    public CDK10Molecule fromString( String molstring ) throws BioclipseException, IOException {

        if (molstring==null) throw new BioclipseException("Input cannot be null");

        ByteArrayInputStream bais=new ByteArrayInputStream(molstring.getBytes());
        
        return loadMolecule( bais );
        
    }



    public String getMolFormat( CDK10Molecule cdk10mol ) {

        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        
        try {
            MDLWriter writer=new MDLWriter(bos);
            writer.write( cdk10mol.getAtomContainer() );
            writer.close();
            String retString=bos.toString();
            System.out.println("RET: " + retString);
            return retString;
        } catch ( CDKException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        
        //Not working
        return null;
    }



    /**
     * Write the <i>molecule</i> to the WS-relative <i>path</i> in
     * a supported <i>format</i>
     * @param cdk10mol
     * @param name
     * @param format
     * @throws CDKException 
     * @throws IOException 
     */
    public File write( CDK10Molecule molecule, String path, String format ) 
                                    throws CDKException, IOException {

            File file=new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            //For now, hardcoded to mol-format

            MDLWriter writer=new MDLWriter(fos);
            writer.write( molecule.getAtomContainer() );
            writer.close();

            return file;
    }


    /**
     * Load molecule from path
     * @param path
     * @return 
     * @throws BioclipseException 
     * @throws IOException 
     */
    public CDK10Molecule loadMolecule( String path ) throws IOException, BioclipseException {

        FileInputStream is=new FileInputStream(path);
        return loadMolecule( is );

    }
}
