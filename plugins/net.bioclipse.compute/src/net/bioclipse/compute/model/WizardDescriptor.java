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
import java.util.List;

import net.bioclipse.compute.WarningStatus;
import net.bioclipse.compute.wizards.IComputationWizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.osgi.framework.Bundle;

/**
 * @author Rob Schellhorn
 */
public class WizardDescriptor implements IPluginContribution, IWizardDescriptor {

	/**
	 * 
	 */
	private final IWizardCategory category;

	/**
	 * 
	 */
	private final IConfigurationElement element;

	/**
	 * 
	 */
	private Class[] types;

	/**
	 * Creates a new WizardDescriptor object which will describes the wizard
	 * configured in the given element.
	 * 
	 * @param element
	 *            The configuration element which describes the wizard.
	 * @param category
	 *            The category this wizard belongs to.
	 * @throws IllegalArgumentException
	 *             If either the element or the category is <code>null</code>.
	 */
	public WizardDescriptor(IConfigurationElement element,
			IWizardCategory category) {

		if (element == null || category == null) {
			throw new IllegalArgumentException();
		}

		this.category = category;
		this.element = element;

		assert invariant() : "POST: The invariant holds";
	}

	/**
	 * @param type
	 * @return <code>true</code> if this wizard accepts selections of the
	 *         given type.
	 */
	public boolean acceptsObject(Class type) {
		assert invariant() : "PRE: The invariant holds";

		if (types == null) {
			IConfigurationElement[] elements = element.getChildren("selection");

			List<Class> selections = new ArrayList<Class>();
			for (IConfigurationElement element : elements) {
				try {
					Bundle b = Platform.getBundle(element
							.getNamespaceIdentifier());
					Class c = b.loadClass(element.getAttribute("class"));
					selections.add(c);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO
				}
			}

			types = selections.toArray(new Class[selections.size()]);
		}

		if (types.length == 0) {
			return true;
		} else {
			for (Class<?> c : types) {
				if (c.isAssignableFrom(type)) {
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#adaptedSelection(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public IStructuredSelection adaptedSelection(IStructuredSelection selection) {
		assert invariant() : "PRE: The invariant holds";

		if (selection == null) {
			return StructuredSelection.EMPTY;
		}

		List<Object> newSelectionObjects = new ArrayList<Object>();
		for (Object o : selection.toArray()) {
			if (acceptsObject(o.getClass())) {
				newSelectionObjects.add(o);
			}
		}

		return new StructuredSelection(newSelectionObjects);
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#canFinishEarly()
	 */
	public boolean canFinishEarly() {
		assert invariant() : "PRE: The invariant holds";

		String value = element.getAttribute("canFinishEarly");
		return value == null || value.equals("true");
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#createWizard()
	 */
	public IComputationWizard createWizard() throws CoreException {
		assert invariant() : "PRE: The invariant holds";

		Object o = element.createExecutableExtension("class");
		if (o instanceof IComputationWizard) {
			return (IComputationWizard) o;
		} else {
			throw new CoreException(new WarningStatus(element
					.getNamespaceIdentifier(),
					"The wizard is not an instance of IComputationWizard"));
		}
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		assert invariant() : "PRE: The invariant holds";

		if (adapter == IPluginContribution.class) {
			IExtension extension = element.getDeclaringExtension();
			final String identifier = extension.getNamespaceIdentifier();

			return new IPluginContribution() {

				public String getLocalId() {
					return identifier;
				}

				public String getPluginId() {
					return identifier;
				}
			};
		}
		return null;
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#getCategory()
	 */
	public IWizardCategory getCategory() {
		assert invariant() : "PRE: The invariant holds";

		return category;
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#getDescription()
	 */
	public String getDescription() {
		assert invariant() : "PRE: The invariant holds";

		return element.getAttribute("description");
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#getDescriptionImage()
	 */
	public ImageDescriptor getDescriptionImage() {
		assert invariant() : "PRE: The invariant holds";

		String path = element.getAttribute("icon");
		if (path == null) {
			return null;
		}

		return AbstractUIPlugin.imageDescriptorFromPlugin(element
				.getNamespaceIdentifier(), path);
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#getHelpHref()
	 */
	public String getHelpHref() {
		assert invariant() : "PRE: The invariant holds";

		return element.getAttribute("helpHref");
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPartDescriptor#getId()
	 */
	public String getId() {
		assert invariant() : "PRE: The invariant holds";

		return element.getAttribute("id");
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		assert invariant() : "PRE: The invariant holds";

		String path = element.getAttribute("descriptionImage");
		if (path == null) {
			return null;
		}

		return AbstractUIPlugin.imageDescriptorFromPlugin(element
				.getNamespaceIdentifier(), path);
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#getLabel()
	 */
	public String getLabel() {
		assert invariant() : "PRE: The invariant holds";

		return element.getAttribute("name");
	}

	/*
	 * @see org.eclipse.ui.IPluginContribution#getLocalId()
	 */
	public String getLocalId() {
		assert invariant() : "PRE: The invariant holds";

		return getId();
	}

	/*
	 * @see org.eclipse.ui.IPluginContribution#getPluginId()
	 */
	public String getPluginId() {
		assert invariant() : "PRE: The invariant holds";

		return element.getNamespaceIdentifier();
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#getTags()
	 */
	public String[] getTags() {
		assert invariant() : "PRE: The invariant holds";

		return new String[0];
	}

	/*
	 * @see net.bioclipse.model.IWizardDescription#hasPages()
	 */
	public boolean hasPages() {
		assert invariant() : "PRE: The invariant holds";

		return "true".equals(element.getAttribute("hasPages"));
	}

	/**
	 * Checks whether this class satisfies the class invariant or not.
	 * WizardDescriptor instances always have:
	 * <ul>
	 * <li>A category</li>
	 * <li>A configuration element</li>
	 * </ul>
	 * 
	 * @return <code>true</code> if this instance satisfies the class
	 *         invariant, <code>false</code> otherwise.
	 */
	private boolean invariant() {
		return category != null && element != null;
	}
}