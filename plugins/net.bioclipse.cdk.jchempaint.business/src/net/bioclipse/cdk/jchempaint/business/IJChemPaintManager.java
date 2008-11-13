/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Egon Willighagen
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.business;

import javax.vecmath.Point2d;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;

import org.openscience.cdk.controller.IController2DModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Manager for the JChemPaintEditor scripting language.
 * 
 * @author egonw
 */
@PublishedClass ("Manager for the JChemPaintEditor scripting language." )
@TestClasses("net.bioclipse.cdk.jchempaint.business.test.JChemPaintManagerTest")
public interface IJChemPaintManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod ( params = "Point2d worldCoordinate", 
                       methodSummary = "Returns the IAtom closest to the world coordinate." )
    public IAtom getClosestAtom(Point2d worldCoord);

    @Recorded
    @PublishedMethod ( methodSummary = "Returns the ICDKMolecule of the active JChemPaint editor." )
    public ICDKMolecule getModel() throws BioclipseException;
    
    @Recorded
    @PublishedMethod ( methodSummary = "Sets the ICDKMolecule of the active JChemPaint editor." )
    public void setModel(ICDKMolecule molecule) throws BioclipseException;

    public void removeAtom(IAtom atom) throws BioclipseException;

    public IBond getClosestBond(Point2d worldCoord);

    public void updateView();

    public void addAtom(String atomType, Point2d worldcoord);

}
