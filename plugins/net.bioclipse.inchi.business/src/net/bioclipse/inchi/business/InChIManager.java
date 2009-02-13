/*******************************************************************************
 * Copyright (c) 2007  Jonathan Alvarsson
 *               2008  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.inchi.business;

import java.security.InvalidParameterException;

import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.core.domain.IMolecule;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

public class InChIManager implements IInChIManager {

    protected InChIGeneratorFactory factory;

    protected InChIGeneratorFactory getFactory() throws Exception {
        if (factory == null) {
            factory = new InChIGeneratorFactory();
        }
        return factory;
    }

    public String getNamespace() {
        return "inchi";
    }

    public String generate(IMolecule molecule) throws Exception {
        if (molecule instanceof CDKMolecule) {
            IAtomContainer container = ((CDKMolecule)molecule).getAtomContainer();
            InChIGenerator gen = getFactory().getInChIGenerator(container);
            INCHI_RET status = gen.getReturnStatus();
            if (status == INCHI_RET.OKAY) {
                return gen.getInchi();
            } else if (status == INCHI_RET.WARNING) {
                return gen.getInchi();
            } else {
                throw new InvalidParameterException(
                    "Error while generating InChI: " +
                    gen.getMessage()
                );
            }
        } else {
            throw new InvalidParameterException(
                "Given molecule must be a CDKMolecule"
            );
        }
    }

    public String generateKey(IMolecule molecule) throws Exception {
        if (molecule instanceof CDKMolecule) {
            IAtomContainer container = ((CDKMolecule)molecule).getAtomContainer();
            InChIGenerator gen = getFactory().getInChIGenerator(container);
            INCHI_RET status = gen.getReturnStatus();
            if (status == INCHI_RET.OKAY) {
                return gen.getInchiKey();
            } else if (status == INCHI_RET.WARNING) {
                return gen.getInchi();
            } else {
                throw new InvalidParameterException(
                    "Error while generating InChI: " +
                    gen.getMessage()
                );
            }
        } else {
            throw new InvalidParameterException(
                "Given molecule must be a CDKMolecule"
            );
        }
    }
}
