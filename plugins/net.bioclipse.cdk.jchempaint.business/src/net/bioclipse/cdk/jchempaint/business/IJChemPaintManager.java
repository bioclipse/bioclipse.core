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

    @Recorded
    @PublishedMethod ( params = "IAtom atom to remove",
                       methodSummary = "Removes an IAtom from the data model." )
    public void removeAtom(IAtom atom) throws BioclipseException;

    @Recorded
    @PublishedMethod ( params = "Point2d worldCoordinate",
                       methodSummary = "Returns the IBond closest to the world coordinate." )
    public IBond getClosestBond(Point2d worldCoord);

    @Recorded
    @PublishedMethod(
         methodSummary = "Refreshes the JChemPaint view screen."
    )
    public void updateView();

    @Recorded
    @PublishedMethod(
         params = "String element symbol, Point2d world coordinate",
         methodSummary = "Adds a new atom at the given coordinates."
    )
    public void addAtom(String elementSymbol, Point2d worldcoord);

    @Recorded
    @PublishedMethod(
         params = "String element symbol, IAtom atom to attach the new atom too",
         methodSummary = "Adds a new atom bonded to the given atom."
    )
    public void addAtom(String elementSymbol, IAtom atom);

    @Recorded
    @PublishedMethod(
         params = "double x coordinate, double y coordinate",
         methodSummary = "Creates a new javax.vecmath.Point2d."
    )
    public Point2d newPoint2d(double x, double y);

    @Recorded
    @PublishedMethod(
         methodSummary = "Updates the implicit hydrogen counts, given the " +
                         "given the current connectivity."
    )
    public void updateImplicitHydrogenCounts();

    @Recorded
    @PublishedMethod(
         params = "IAtom atom to move, Point2D point where to move to", 
         methodSummary = "Moves an atom to the given location."
    )
    public void moveTo(IAtom atom, Point2d point);

    @Recorded
    @PublishedMethod(
         params = "IAtom atom to change, Symbol new element symbol", 
         methodSummary = "Changes the element of this atom."
    )
    public void setSymbol(IAtom atom, String symbol);

    @Recorded
    @PublishedMethod(
         params = "IAtom atom to change, int new formal charge", 
         methodSummary = "Changes the formal charge of this atom."
    )
    public void setCharge(IAtom atom, int charge);

    @Recorded
    @PublishedMethod(
         params = "IAtom atom to change, int new mass number", 
         methodSummary = "Changes the mass number of this element."
    )
    public void setMassNumber(IAtom atom, int massNumber);

    @Recorded
    @PublishedMethod(
         params = "IAtom first atom in the bond, IAtom second atom in the bond", 
         methodSummary = "Create a new bond between the two given atoms."
    )
    public void addBond(IAtom fromAtom, IAtom toAtom);

    @Recorded
    @PublishedMethod(
         params = "IBond bond to move, Point2d point to move the atom to", 
         methodSummary = "Moves the center of the bond to the new point."
    )
    public void moveTo(IBond bond, Point2d point);

    @Recorded
    @PublishedMethod(
         params = "IBond bond to change, IBond.Order new bond order", 
         methodSummary = "Changes the order of the bond."
    )
    public void setOrder(IBond bond, IBond.Order order);

    @Recorded
    @PublishedMethod(
         params = "IBond bond to change, int new wedge type", 
         methodSummary = "Changes the wedge type of the bond."
    )
    public void setWedgeType(IBond bond, int type);
    
    @Recorded
    @PublishedMethod(
         params = "int order", 
         methodSummary = "Returns a IBond.Order matching the given order."
    )
    public IBond.Order getBondOrder(int order);

    @Recorded
    @PublishedMethod(
         methodSummary = "Deletes all atoms and bonds."
    )
    public void zap();

    @Recorded
    @PublishedMethod(
         methodSummary = "Recalculates 2D coordinates for the complete molecule."
    )
    public void cleanup();

    @Recorded
    @PublishedMethod(
         params = "IAtom atom to add the ring to, int ring size",
         methodSummary = "Adds a carbon ring of the given size to the given atom."
    )
    public void addRing(IAtom atom, int size);

}
