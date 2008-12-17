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
import net.bioclipse.compute.wizards.IComputationWizard;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
/**
 * @author Rob Schellhorn
 */
public class WizardFilter extends PatternFilter {
        /*
         * @see org.eclipse.ui.internal.dialogs.PatternFilter#isElementSelectable(java.lang.Object)
         */
        public boolean isElementSelectable(Object element) {
                return element instanceof IComputationWizard;
        }
        /*
         * @see org.eclipse.ui.internal.dialogs.PatternFilter#isElementMatch(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object)
         */
        protected boolean isLeafMatch(Viewer viewer, Object element) {
                if (element instanceof WizardCategory) {
                        return false;
                } else if (element instanceof WizardDescriptor) {
                        WizardDescriptor desc = (WizardDescriptor) element;
                        String text = desc.getLabel();
                        if (wordMatches(text)) {
                                return true;
                        }
                }
                return false;
        }
}