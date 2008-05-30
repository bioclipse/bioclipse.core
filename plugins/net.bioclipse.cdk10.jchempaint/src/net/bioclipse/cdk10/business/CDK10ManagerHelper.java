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

import java.util.Properties;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.cml.MDMoleculeConvention;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.SDFFormat;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.nonotify.NNChemFile;

public class CDK10ManagerHelper {

    /**
     * Register all formats that we support for reading in Bioclipse
     * @param fac
     */
    public static void registerFormats(ReaderFactory fac) {
        if (fac.getFormats().contains(PDBFormat.getInstance())==false){
            fac.registerFormat((IChemFormatMatcher) PDBFormat.getInstance());
        }
        if (fac.getFormats().contains(SDFFormat.getInstance())==false){
            fac.registerFormat((IChemFormatMatcher) SDFFormat.getInstance());
        }
        if (fac.getFormats().contains(CMLFormat.getInstance())==false){
            fac.registerFormat((IChemFormatMatcher) CMLFormat.getInstance());
        }
        if (fac.getFormats().contains(MDLV2000Format.getInstance())==false){
            fac.registerFormat((IChemFormatMatcher) MDLV2000Format.getInstance());
        }
        if (fac.getFormats().contains(MDLV3000Format.getInstance())==false){
            fac.registerFormat((IChemFormatMatcher) MDLV3000Format.getInstance());
        }
        

    }
    
    public static void customizeReading(IChemObjectReader reader, IChemFile chemFile) {
        System.out.println("customingIO, reader found: " + reader.getClass().getName());
        System.out.println("Found # IO settings: " + reader.getIOSettings().length);
        if (reader instanceof PDBReader) {
            chemFile = new NNChemFile();

            Properties customSettings = new Properties();
            customSettings.setProperty("DeduceBonding", "false");

            PropertiesListener listener = new PropertiesListener(customSettings);
            reader.addChemObjectIOListener(listener);
        }

        if (reader instanceof CMLReader) {
            ((CMLReader)reader).registerConvention("md:mdMolecule", new MDMoleculeConvention(new ChemFile()));
            System.out.println("****** CmlReader, registered MDMoleculeConvention");

        }

    }


}
