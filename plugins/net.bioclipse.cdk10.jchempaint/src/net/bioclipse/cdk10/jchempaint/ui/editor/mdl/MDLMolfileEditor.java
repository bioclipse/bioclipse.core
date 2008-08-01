/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor.mdl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.bioclipse.cdk10.jchempaint.ui.editor.AbstractJCPEditor;
import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;

/**
 * JChemPaint-based editor for MDL molfile V2000 files.
 * 
 * @author egonw, ola
 */
public class MDLMolfileEditor extends AbstractJCPEditor{

    public static final String EDITOR_ID 
        = "net.bioclipse.cdk10.jchempaint.ui.editor.mdl.MDLMolfileEditor";


    public String asText() {
        StringWriter stringWriter = new StringWriter(2000);
        MDLWriter mdlWriter = new MDLWriter(stringWriter);
        IChemModel model = this.getJcpModel().getChemModel();
        try {
            mdlWriter.write(model);
        } catch (CDKException e) {
            e.printStackTrace(new PrintWriter(stringWriter));
        }
        return stringWriter.toString();
    }


    /**
     * Get the IChemModel from the parsedResource
     * @return
     * @throws BioclipseException 
     */
    public IChemModel getModelFromEditorInput() throws BioclipseException{

        IFile file = (IFile)getEditorInput().getAdapter(IFile.class);
        if (!(file instanceof IFile)) {
            throw new BioclipseException(
                    "Invalid editor input: Does not provide an IFile");
        }
        if(file!=null && !file.exists())
            return null;
        
        IFile inputFile = (IFile) file;
        
        try {
            InputStream instream=inputFile.getContents();
            
            MDLV2000Reader reader = new MDLV2000Reader(instream);
            return (IChemModel)reader.read(new ChemModel());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
