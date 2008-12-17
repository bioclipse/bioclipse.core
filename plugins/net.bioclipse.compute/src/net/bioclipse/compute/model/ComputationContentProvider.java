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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
/**
 * ComputationContentProvider fills the tree with the wizards and categories
 * applicable to the selection.
 * 
 * @author Rob Schellhorn
 */
public class ComputationContentProvider implements ITreeContentProvider {
        private final IStructuredSelection selection;
        /**
         * @param selection
         */
        public ComputationContentProvider(IStructuredSelection selection) {
                this.selection = selection;
        }
        /*
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
                // No-op
        }
        /*
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof IWizardCategory) {
                        List list = new ArrayList();
                        IWizardCategory element = (IWizardCategory) parentElement;
                        for (Object collection : element.getCategories()) {
                                handleChild(collection, list);
                        }
                        for (IWizardDescriptor childWizard : element.getWizards()) {
                                if (!childWizard.adaptedSelection(selection).isEmpty()) {
                                        handleChild(childWizard, list);
                                }
                        }
                        // flatten lists with only one category
                        if (list.size() == 1 && list.get(0) instanceof IWizardCategory) {
                                return getChildren(list.get(0));
                        }
                        return list.toArray();
                } else if (parentElement instanceof AdaptableList) {
                        AdaptableList aList = (AdaptableList) parentElement;
                        final List list = new ArrayList();
                        for (Object child : aList.getChildren()) {
                                handleChild(child, list);
                        }
                        // if there is only one category, return it's children directly
                        // (flatten list)
                        if (list.size() == 1) {
                                return getChildren(list.get(0));
                        }
                        return list.toArray();
                }
                return new Object[0];
        }
        /*
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
                return getChildren(inputElement);
        }
        /**
         * Adds the item to the list, unless it's a collection element without any
         * children.
         * 
         * @param element
         *            the element to test and add
         * @param list
         *            the <code>Collection</code> to add to.
         */
        @SuppressWarnings(value = "unchecked")
        private void handleChild(Object element, Collection list) {
                if (element instanceof WizardCategory) {
                        if (hasChildren(element)) {
                                list.add(element);
                        }
                } else {
                        list.add(element);
                }
        }
        /*
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent(Object element) {
                if (element instanceof WizardCategory) {
                        return ((WizardCategory) element).getParent();
                }
                return null;
        }
        /*
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren(Object element) {
                if (element instanceof WizardCategory) {
                        if (getChildren(element).length > 0) {
                                return true;
                        }
                }
                return false;
        }
        /*
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                // No-op
        }
}