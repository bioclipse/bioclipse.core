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

import net.bioclipse.compute.model.WizardLoader;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * @author Rob Schellhorn
 */
public class ComputationWizardNode implements IWizardNode {

	/**
	 *
	 */
	private final IWizardDescriptor descriptor;

	/**
	 *
	 */
	private final SelectComputationWizard page;

	/**
	 *
	 */
	private IWorkbenchWizard wizard;

	/**
	 * Constructs a new ComputationWizard which retrieves its data from the
	 * given descriptor and fills the given page.
	 *
	 * @param description
	 * @param page
	 * @throws IllegalArgumentException
	 *             If either the decription or the page is <code>null</code>.
	 */
	public ComputationWizardNode(IWizardDescriptor description,
			SelectComputationWizard page) {

		if (description == null || page == null) {
			throw new IllegalArgumentException();
		}

		this.descriptor = description;
		this.page = page;
	}

	/*
	 * @see org.eclipse.jface.wizard.IWizardNode#dispose()
	 */
	public void dispose() {
		// No-op
	}

	/*
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
	public Point getExtent() {
		return new Point(-1, -1);
	}

	/*
	 * @see org.eclipse.jface.wizard.IWizardNode#getWizard()
	 */
	public IWizard getWizard() {
		if (wizard == null) {
			WizardLoader loader = new WizardLoader(descriptor);
			BusyIndicator.showWhile(page.getShell().getDisplay(), loader);

			wizard = loader.getWizard();
			if (wizard != null) {
				IStructuredSelection selection =
					descriptor.adaptedSelection(page.getSelection());

				wizard.init(page.getWorkbench(), selection);
			}
		}

		return wizard;
	}

	/*
	 * @see org.eclipse.jface.wizard.IWizardNode#isContentCreated()
	 */
	public boolean isContentCreated() {
		return wizard != null && wizard.getPageCount() > 0;
	}
}