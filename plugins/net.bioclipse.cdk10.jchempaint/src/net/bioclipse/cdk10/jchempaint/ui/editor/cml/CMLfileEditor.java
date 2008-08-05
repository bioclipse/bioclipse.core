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

package net.bioclipse.cdk10.jchempaint.ui.editor.cml;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage;
import net.bioclipse.cdk10.jchempaint.ui.editor.AbstractJCPEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.DrawingPanel;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPComposite;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPMultiPageEditorContributor;
import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * JChemPaint-based editor for CML files.
 * 
 * @author ola
 */
public class CMLfileEditor extends AbstractJCPEditor{
	
    public static final String EDITOR_ID 
        = "net.bioclipse.cdk10.jchempaint.ui.editor.cml.CMLfileEditor";

    public String asText() {
        StringWriter stringWriter = new StringWriter(2000);
        
        CMLWriter cmlWriter=new CMLWriter(stringWriter);
        
        //FIXME: add pretty printing
        
        IChemModel model = this.getJcpModel().getChemModel();
        try {
            cmlWriter.write(model);
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
            
            CMLReader reader = new CMLReader(instream);
            IChemFile cf=(IChemFile)reader.read(new ChemFile());
            
            IChemModel cm=cf.getChemSequence( 0 ).getChemModel( 0 );
            
            return cm;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
