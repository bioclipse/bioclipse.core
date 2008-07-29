/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.colorers.actions;

import java.awt.Font;
import java.util.HashMap;

import net.bioclipse.cdk10.business.ICDK10Constants;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.Renderer2DModel;


public class CPKAction extends Action implements IAction{

    @Override
    public void run() {

        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getActiveEditor();
        

        if (!( part instanceof IJCPBasedEditor )) {
            return;
        }

        IJCPBasedEditor editor = (IJCPBasedEditor) part;
        IChemModel chemModel= editor.getChemModel();
        if (chemModel==null) return;
        if (chemModel.getMoleculeSet()==null) return;

        
        //Get AtomContainer from model
        IAtomContainer ac = chemModel.getMoleculeSet().getMolecule( 0 );
        
        //Do GUI customizations
        Renderer2DModel model=editor.getDrawingPanel().getRenderer2D()
        .getRenderer2DModel();            

        //Color by CPK, so set props for color to NULL
        model.getColorHash().clear();
        model.getToolTipTextMap().clear();

        for (int i=0; i< ac.getAtomCount(); i++){
            IAtom atom=ac.getAtom( i );
            atom.removeProperty( ICDK10Constants.COLOR_PROPERTY);
        }
        
        //Configure JCP
        model.setKekuleStructure( false );
        model.setShowAtomTypeNames( false );
        model.setShowImplicitHydrogens( false );
        model.setShowExplicitHydrogens(  false );
        Font font = new Font("courier", Font.PLAIN, 14);
        model.setFont(font);
        
        //Update drawing
        model.fireChange();

        
    }
    
}
