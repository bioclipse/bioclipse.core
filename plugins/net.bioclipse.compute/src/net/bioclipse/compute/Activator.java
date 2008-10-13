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

package net.bioclipse.compute;

import net.bioclipse.compute.business.IComputeManager;
import net.bioclipse.compute.model.ComputationWizardRegistry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Rob Schellhorn
 */
public class Activator extends AbstractUIPlugin {

	public static final String ID = "net.bioclipse.compute";

	private ServiceTracker finderTracker;

	private static Activator plugin;


	public static Activator getDefault() {
		return plugin;
	}

	private final IWizardRegistry wizardRegistry
					= new ComputationWizardRegistry();

	public IWizardRegistry getWizardRegistry() {
		return wizardRegistry;
	}

	public IComputeManager getComputeManager() {
		IComputeManager manager = null;
		try {
			manager = (IComputeManager) finderTracker.waitForService(1000*10);
		} catch (InterruptedException e) {
			// TODO : log(e)
		}
		if (manager == null) {
			throw new IllegalStateException("Could not get the Compute Manager");
		}
		return manager;
	}

	/*
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		if (JFaceResources.getImage(Dialog.DLG_IMG_HELP) == null) {
			ImageDescriptor image = imageDescriptorFromPlugin(ID,
					"icons/help_contents.gif");

			JFaceResources.getImageRegistry().put(Dialog.DLG_IMG_HELP, image);
		}

		finderTracker = new ServiceTracker(context,
				IComputeManager.class.getName(),
				null);
		finderTracker.open();
	}

	/*
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}