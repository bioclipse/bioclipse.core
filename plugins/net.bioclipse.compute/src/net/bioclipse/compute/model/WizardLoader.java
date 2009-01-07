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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * @author Rob Schellhorn
 */
public class WizardLoader extends SafeRunnable implements Runnable {

	private final IWizardDescriptor descriptor;

	private IWorkbenchWizard wizard;

	/**
	 * @param descriptor
	 */
	public WizardLoader(IWizardDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * @return
	 */
	public IWorkbenchWizard getWizard() {
		return wizard;
	}

	/*
	 * @see org.eclipse.jface.util.SafeRunnable#handleException(java.lang.Throwable)
	 */
	public void handleException(Throwable e) {
		IPluginContribution contribution = (IPluginContribution) descriptor
				.getAdapter(IPluginContribution.class);

		String message = e.getMessage() != null ? e.getMessage() : "";
		String pluginId = contribution != null ? contribution.getPluginId()
				: null;

		IStatus status = new Status(IStatus.ERROR, pluginId, IStatus.OK,
				message, e);
		ErrorDialog.openError(null, "Compute", "Wizard could not be created",
				status);
	}

	/*
	 * @see org.eclipse.core.runtime.ISafeRunnable#run()
	 */
	public void run() {
		try {
			wizard = descriptor.createWizard();
		} catch (CoreException e) {
			handleException(e);
		}
	}
}