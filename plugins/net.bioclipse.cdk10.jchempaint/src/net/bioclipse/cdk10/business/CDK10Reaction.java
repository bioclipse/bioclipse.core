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

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IMolecule;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * The CDKReaction wraps an IReaction and is able to cache SMILES
 * 
 * @author miguelrojasch
 *
 */
public class CDK10Reaction extends BioObject implements net.bioclipse.core.domain.IReaction{

    private String name;
    private IReaction reaction;
    private String cachedSMILES;
    private BitSet cachedFingerprint;

    public CDK10Reaction(IReaction reaction) {
        super();
        this.reaction=reaction;
    }

    public Object getParsedResource() {
        return reaction;
    }

    public String getSmiles() throws BioclipseException {

        //TODO: wrap in job?

        if (cachedSMILES != null) {
            return cachedSMILES;
        }

        if (getReaction() == null)
            throw new BioclipseException("Unable to calculate SMILES: reaction is empty");

        if (!(getReaction() instanceof IReaction))
            throw new BioclipseException("Unable to calculate SMILES: Not a reaction.");

        if (getReaction().getProductCount() == 0)
            throw new BioclipseException("Unable to calculate SMILES: count of the product is 0.");

        if (getReaction().getReactantCount() == 0)
            throw new BioclipseException("Unable to calculate SMILES: count of the reactant is 0.");
        
        IReaction reaction = (IReaction)getReaction();

        // Create the SMILES
        SmilesGenerator generator = new SmilesGenerator();
        try {
			cachedSMILES = generator.createSMILES(reaction);
		} catch (CDKException e) {
			e.printStackTrace();
		}

        return cachedSMILES;
    }

    public IReaction getReaction() {
        return reaction;
    }

    public void setReaction(IReaction reaction) {
        this.reaction = reaction;
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

        if (reaction==null) throw new BioclipseException("No molecule to " +
        "get CML from!");

        ByteArrayOutputStream bo=new ByteArrayOutputStream();

        CMLWriter writer=new CMLWriter(bo);
        try {
            writer.write(reaction);
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
//            BitSet fingerprint=fp.getFingerprint(reaction);
        	BitSet fingerprint = null;
            cachedFingerprint=fingerprint;
            return fingerprint;
        } catch (Exception e) {
            throw new BioclipseException("Could not create fingerprint: "
                    + e.getMessage());
        }

    }

    public boolean has3dCoords() throws BioclipseException {
//        if (reaction==null) throw new BioclipseException("Reaction is null!");
//        return GeometryTools.has3DCoordinates(reaction);
        return false;
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
