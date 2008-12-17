/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rob Schellhorn
 ******************************************************************************/
package net.bioclipse.compute.model;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
/**
 * A label provider for IWizardcategory and IWizardDescriptor objects.
 * 
 * @author Rob Schellhorn
 */
public class ComputationLabelProvider extends LabelProvider {
        /*
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object element) {
                if (element instanceof IWizardCategory) {
                        return PlatformUI.getWorkbench().getSharedImages().getImage(
                                        ISharedImages.IMG_OBJ_FOLDER);
                } else if (element instanceof IWizardDescriptor) {
                        ImageDescriptor descriptor = ((IWizardDescriptor) element)
                                        .getDescriptionImage();
                        return descriptor == null ? null : descriptor.createImage();
                }
                return super.getImage(element);
        }
        /*
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
                if (element instanceof IWizardCategory) {
                        return ((IWizardCategory) element).getLabel();
                } else if (element instanceof IWizardDescriptor) {
                        return ((IWizardDescriptor) element).getLabel();
                }
                return super.getText(element);
        }
}