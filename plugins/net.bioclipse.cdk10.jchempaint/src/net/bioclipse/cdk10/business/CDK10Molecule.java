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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.smiles.SmilesGenerator;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IMolecule;

/**
 * The CKMolecule wraps an IAtomContainer and is able to cache SMILES
 * @author ola
 *
 */
public class CDK10Molecule extends BioObject implements IMolecule{

    private String name;
    private IAtomContainer atomContainer;
    private String cachedSMILES;
    private BitSet cachedFingerprint;

    public CDK10Molecule(IAtomContainer atomContainer) {
        super();
        this.atomContainer=atomContainer;
    }

    public Object getParsedResource() {
        return atomContainer;
    }

    public String getSmiles() throws BioclipseException {

        //TODO: wrap in job?

        if (cachedSMILES != null) {
            return cachedSMILES;
        }

        if (getAtomContainer() == null)
            throw new BioclipseException("Unable to calculate SMILES: Molecule is empty");

        if (!(getAtomContainer() instanceof org.openscience.cdk.interfaces.IMolecule))
            throw new BioclipseException("Unable to calculate SMILES: Not a molecule.");

        if (getAtomContainer().getAtomCount() > 100)
            throw new BioclipseException("Unable to calculate SMILES: Molecule has more than 100 atoms.");

        if (getAtomContainer().getBondCount() == 0)
            throw new BioclipseException("Unable to calculate SMILES: Molecule has no bonds.");

        org.openscience.cdk.interfaces.IMolecule molecule=(org.openscience.cdk.interfaces.IMolecule)getAtomContainer();

        // Create the SMILES
        SmilesGenerator generator = new SmilesGenerator();
        cachedSMILES = generator.createSMILES(molecule);

        return cachedSMILES;
    }

    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    public void setAtomContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCachedSMILES() {
        return cachedSMILES;
    }

    public void setCachedSMILES(String cachedSMILES) {
        this.cachedSMILES = cachedSMILES;
    }

    public String getCML() throws BioclipseException {

        if (atomContainer==null) throw new BioclipseException("No molecule to " +
        "get CML from!");

        ByteArrayOutputStream bo=new ByteArrayOutputStream();

        CMLWriter writer=new CMLWriter(bo);
        try {
            writer.write(atomContainer);
            writer.close();
        } catch (CDKException e) {
            throw new BioclipseException("Could not convert molecule to CML: "
                    + e.getMessage());
        } catch (IOException e) {
            throw new BioclipseException("Could not write molecule to CML: "
                    + e.getMessage());
        }

        if (bo==null) throw new BioclipseException("Convert to CML resulted in " +
        "empty String.");

        return bo.toString();
    }

    /**
     * Calculate CDK fingerprint and cache the result.
     * @param force if true, do not use cache but force calculation
     * @return
     * @throws BioclipseException
     */
    public BitSet getFingerprint(boolean force) throws BioclipseException {

        if (force==false){
            if (cachedFingerprint != null) {
                return cachedFingerprint;
            }
        }
        Fingerprinter fp=new Fingerprinter();
        try {
            BitSet fingerprint=fp.getFingerprint(atomContainer);
            cachedFingerprint=fingerprint;
            return fingerprint;
        } catch (Exception e) {
            throw new BioclipseException("Could not create fingerprint: "
                    + e.getMessage());
        }

    }

    public boolean has3dCoords() throws BioclipseException {
        if (atomContainer==null) throw new BioclipseException("Atomcontainer is null!");
        return GeometryTools.has3DCoordinates(atomContainer);
    }

    @Override
    public Object getAdapter( Class adapter ) {
    
        if (adapter == IMolecule.class){
            return this;
        }
        
        // TODO Auto-generated method stub
        return super.getAdapter( adapter );
    }

}
