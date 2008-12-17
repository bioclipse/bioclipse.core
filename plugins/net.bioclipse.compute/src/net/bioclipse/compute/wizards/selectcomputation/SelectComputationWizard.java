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
package net.bioclipse.compute.wizards.selectcomputation;
import static net.bioclipse.compute.Activator.getDefault;
import net.bioclipse.compute.wizards.IComputationWizard;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
/**
 * @author Rob Schellhorn
 */
public class SelectComputationWizard extends Wizard implements
                IComputationWizard {
        /**
         * 
         */
        private static final String CATEGORY_SEPARATOR = "/";
        /**
         * The id of the category to show or <code>null</code> to show all the
         * categories.
         */
        private String categoryId = null;
        /**
         * 
         */
        private SelectComputationPage mainPage;
        /**
         * 
         */
        private IStructuredSelection selection;
        /**
         * 
         */
        private IWorkbench workbench;
        /*
         * @see org.eclipse.jface.wizard.Wizard#addPages()
         */
        public void addPages() {
                IWizardRegistry registry = getDefault().getWizardRegistry();
                IWizardCategory root = registry.getRootCategory();
                IWizardDescriptor[] primary = registry.getPrimaryWizards();
                if (categoryId != null) {
                        IWizardCategory categories = root;
                        for (String id : categoryId.split(CATEGORY_SEPARATOR)) {
                                categories = getChildWithID(categories, id);
                                if (categories == null) {
                                        break;
                                }
                        }
                        if (categories != null) {
                                root = categories;
                        }
                }
                mainPage = new SelectComputationPage(this, root, primary);
                addPage(mainPage);
        }
        /*
         * @see org.eclipse.jface.wizard.Wizard#canFinish()
         */
        public boolean canFinish() {
                IWizardPage currentPage = getContainer().getCurrentPage();
                if (currentPage == mainPage) {
                        return mainPage.canFinishEarly();
                } else {
                        return currentPage.isPageComplete();
                }
        }
        /**
         * Returns the id of the category of wizards to show or <code>null</code>
         * to show all categories.
         * 
         * @return String
         */
        public String getCategoryId() {
                return categoryId;
        }
        /**
         * Returns the child collection element for the given id.
         * 
         * @param parent
         * @param id
         */
        private IWizardCategory getChildWithID(IWizardCategory parent, String id) {
                for (IWizardCategory child : parent.getCategories()) {
                        if (child.getId().equals(id)) {
                                return child;
                        }
                }
                return null;
        }
        /**
         * @return
         */
        public Job getComputationJob() {
                IWizardNode node = mainPage.getSelectedNode();
                if (node != null) {
                        return ((IComputationWizard) node.getWizard()).getComputationJob();
                }
                return null;
        }
        /**
         * @return
         */
        public IStructuredSelection getSelection() {
                return selection;
        }
        /**
         * @return
         */
        public IWorkbench getWorkbench() {
                return workbench;
        }
        /*
         * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
         *      org.eclipse.jface.viewers.IStructuredSelection)
         */
        public void init(IWorkbench workbench, IStructuredSelection selection) {
                this.workbench = workbench;
                this.selection = selection;
                setWindowTitle("Compute");
                setNeedsProgressMonitor(true);
        }
        /*
         * @see org.eclipse.jface.wizard.Wizard#performFinish()
         */
        public boolean performFinish() {
                if (!canFinish()) {
                        throw new IllegalStateException();
                }
                mainPage.saveWidgetValues();
                return true;
        }
        /**
         * Sets the id of the category of wizards to show or <code>null</code> to
         * show all categories.
         * 
         * @param id
         */
        public void setCategoryId(String id) {
                categoryId = id;
        }
}