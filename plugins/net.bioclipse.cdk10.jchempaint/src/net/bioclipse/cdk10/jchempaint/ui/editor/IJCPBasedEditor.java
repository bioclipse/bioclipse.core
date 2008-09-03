/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Color;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.commands.operations.IUndoContext;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.color.IAtomColorer;

/**
 * Common interface for editors based on JCP.
 * @author ola
 *
 */
public interface IJCPBasedEditor {

    public JChemPaintModel getJcpModel();

    public DrawingPanel getDrawingPanel();

    public IUndoContext getUndoContext();

    public JCPComposite getJcpComposite();
    
    public JCPPage getJCPPage();

    /**
     * Editor can provide a colorer
     * @return
     */
    public IAtomColorer getColorer();

    /**
     * Editor must provide a way to parse the input to an IChemfile
     * @return
     */
    public IChemModel getModelFromEditorInput() throws BioclipseException;

    public IChemModel getChemModel();

	public void setMoleculeColorProperties(List<Color> molColors);

	public void setMoleculeTooltips(List<String> molTooltips);
    
}
