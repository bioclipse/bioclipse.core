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
package net.bioclipse.cdk10.ui.content;

import net.bioclipse.cdk10.business.CDK10Molecule;
import net.bioclipse.cdk10.ui.Activator;
import net.bioclipse.core.domain.IMolecule;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.openscience.cdk.geometry.GeometryTools;

/**
 * This Provider provides text, image, and description for Molecules
 * @author ola
 *
 */
public class MoleculeLabelProvider implements ILabelProvider, IDescriptionProvider {

    public Image getImage(Object element) {
        if (element instanceof CDK10Molecule) {
            CDK10Molecule mol = (CDK10Molecule) element;

            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//            imageKey = ISharedImages.IMG_OBJ_FOLDER;

            ImageDescriptor descriptor=null;
            if (GeometryTools.has3DCoordinates(mol.getAtomContainer())){
                descriptor=Activator.imageDescriptorFromPlugin( 
                             Activator.PLUGIN_ID, "icons/molecule3D.gif");
            }
            else if (GeometryTools.has2DCoordinates(mol.getAtomContainer())){
                descriptor=Activator.imageDescriptorFromPlugin( 
                              Activator.PLUGIN_ID, "icons/molecule2D.png");
            }
            if (descriptor!=null){
                return descriptor.createImage();
            }
            
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
        return null;
    }

    public String getText(Object element) {
        if (element instanceof CDK10Molecule) {
            CDK10Molecule mol = (CDK10Molecule) element;
            return mol.getName();
        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public boolean isLabelProperty(Object element, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    public String getDescription(Object anElement) {
        // TODO Auto-generated method stub
        return null;
    }

}
