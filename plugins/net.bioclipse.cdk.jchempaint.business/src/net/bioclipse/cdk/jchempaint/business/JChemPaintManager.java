/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Egon Willighagen
 *     Ola Spjuth
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.business;

import javax.vecmath.Point2d;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.editor.JChemPaintEditor;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.scripting.ui.Activator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @author egonw
 */
public class JChemPaintManager implements IJChemPaintManager {

    /** Not to be used by manager method directly, but is just needed for the syncRun() call. */
    private JChemPaintEditor jcpEditor;

    public IAtom getClosestAtom(Point2d worldCoord) {
        JChemPaintEditor editor = findActiveEditor();
        if (editor != null) {
            IChemModelRelay relay = editor.getControllerHub();
            return relay.getClosestAtom(worldCoord);
        } else {
            Activator.getDefault().getJsConsoleManager().say("No opened JChemPaint editor");
            return null;
        }
    }

    public String getNamespace() {
        return "jcp";
    }

    protected void setActiveEditor(JChemPaintEditor activeEditor) {
        jcpEditor = activeEditor;
    }

    private JChemPaintEditor findActiveEditor() {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        setActiveEditor(null);
        display.syncExec( new Runnable() {
            public void run() {
                IEditorPart activeEditor 
                    = PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow()
                                .getActivePage()
                                .getActiveEditor();
                System.out.println("Editor: " + activeEditor.getClass().getName());
                if (activeEditor instanceof JChemPaintEditor) {
                    setActiveEditor((JChemPaintEditor)activeEditor);
                }
            }
        });
        return jcpEditor;
    }

    public ICDKMolecule getModel() throws BioclipseException {
        JChemPaintEditor editor = findActiveEditor();
        if (editor == null) {
            throw new BioclipseException("No active JChemPaint editor found.");
        }
        return editor.getCDKMolecule();
    }

    public void setModel(ICDKMolecule molecule) throws BioclipseException {
        if (molecule == null) {
            throw new BioclipseException("Input is null.");
        }
        JChemPaintEditor editor = findActiveEditor();
        if (editor == null) {
            throw new BioclipseException("No active JChemPaint editor found.");
        }
        editor.setInput(molecule);
    }

    public void addAtom(String atomType, Point2d worldcoord) {
        Activator.getDefault().getJsConsoleManager().say("No implemented yet");
    }

    public IBond getClosestBond(Point2d worldCoord) {
        Activator.getDefault().getJsConsoleManager().say("No implemented yet");
        return null;
    }

    public void removeAtom(IAtom atomToRemove) throws BioclipseException {
        ICDKMolecule molecule = getModel();
        IAtomContainer container = molecule.getAtomContainer();
        for (IAtom atom : container.atoms()) {
            if (atom == atomToRemove) {
                container.removeAtomAndConnectedElectronContainers(atom);
                return;
            }
        }
    }

    public void updateView() {
        Activator.getDefault().getJsConsoleManager().say("No implemented yet");
    }

}
