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
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
/**
 * ComputationCollectionSorter sorts the tree of wizards. For every category
 * first all wizards are shown in alphabetically order and then the sub
 * categories.
 * 
 * @author Rob Schellhorn
 */
public class ComputationCollectionSorter extends ViewerSorter {
        /*
         * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
         */
        public int category(Object element) {
                if (element instanceof IWizardDescriptor) {
                        return -1;
                } else if (element instanceof IWizardCategory) {
                        return 1;
                }
                return super.category(element);
        }
        /*
         * Return true if this sorter is affected by a property change of
         * propertyName on the specified element.
         * 
         * @see org.eclipse.jface.viewers.ViewerComparator#isSorterProperty(java.lang.Object,
         *      java.lang.String)
         */
        public boolean isSorterProperty(Object object, String propertyId) {
                if (propertyId == null) {
                        throw new IllegalArgumentException();
                }
                return propertyId.equals(IBasicPropertyConstants.P_TEXT);
        }
}