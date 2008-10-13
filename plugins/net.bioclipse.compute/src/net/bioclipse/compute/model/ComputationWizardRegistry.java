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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bioclipse.compute.Activator;
import net.bioclipse.compute.WarningStatus;
import net.bioclipse.compute.internal.Messages;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

/**
 * @author Rob Schellhorn
 */
public class ComputationWizardRegistry implements IWizardRegistry {

	/**
	 *
	 */
	static final String ID = Activator.ID + ".computationWizards";

	/**
	 *
	 */
	private boolean initialized = false;

	/**
	 *
	 */
	private IWizardDescriptor[] primaryWizards;

	/**
	 *
	 */
	private WizardCategory root;

	/*
	 * @see org.eclipse.ui.wizards.IWizardRegistry#findCategory(java.lang.String)
	 */
	public IWizardCategory findCategory(String id) {
		initialize();

		if (id == null) {
			throw new IllegalArgumentException();
		}

		return root.findCategory(id);
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardRegistry#findWizard(java.lang.String)
	 */
	public IWizardDescriptor findWizard(String id) {
		initialize();
		return root.findWizard(id);
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardRegistry#getPrimaryWizards()
	 */
	public IWizardDescriptor[] getPrimaryWizards() {
		initialize();
		return primaryWizards;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardRegistry#getRootCategory()
	 */
	public IWizardCategory getRootCategory() {
		initialize();
		return root;
	}

	/**
	 * Read the contents of the registry if necessary.
	 */
	protected final synchronized void initialize() {
		if (initialized) {
			return;
		}

		assert root == null;
		assert primaryWizards == null;

		ILog log = Activator.getDefault().getLog();
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		List<IConfigurationElement> categoryElements = new ArrayList<IConfigurationElement>();
		List<IConfigurationElement> wizardElements = new ArrayList<IConfigurationElement>();
		List<IConfigurationElement> primaryWizardElements = new ArrayList<IConfigurationElement>();

		// First create the root category
		root = new WizardCategory("id", "root", null);

		// Split elements according to their type
		for (IConfigurationElement element : registry
				.getConfigurationElementsFor(ID)) {

			// Filter invalid elements
			if (!element.isValid()) {
				IStatus status = new WarningStatus(element
						.getNamespaceIdentifier(), "The element is not valid");
				log.log(status);
				continue;
			}

			final String name = element.getName();
			if ("category".equals(name)) {
				categoryElements.add(element);
			} else if ("wizard".equals(name)) {
				wizardElements.add(element);
			} else if ("primaryWizard".equals(name)) {
				primaryWizardElements.add(element);
			} else {
				IStatus status = new WarningStatus(element
						.getNamespaceIdentifier(), "Unknown element "
						+ element.getName());
				log.log(status);
			}
		}

		// First handle all categories without a parent category, which must be
		// added to the root.
		List<IConfigurationElement> handled = new ArrayList<IConfigurationElement>();
		for (IConfigurationElement element : categoryElements) {

			String parent = element.getAttribute("parentCategory");
			if (parent == null || "".equals(parent)) {
				String id = element.getAttribute("id");
				String name = element.getAttribute("name");

				root.add(new WizardCategory(id, name, root));

				// Mark the element as handled
				handled.add(element);
			}
		}

		// Remove all handled categories
		categoryElements.removeAll(handled);

		// Try adding the categories to their parent. The parent may be found as
		// long as at least one category could be placed each iteration.
		do {
			// Clear the list of handled configuration elements.
			handled.clear();

			for (IConfigurationElement element : categoryElements) {
				String parent = element.getAttribute("parentCategory");

				// Parent is not null, because those categories have already
				// been removed from the list.
				WizardCategory parentCategory = root.findCategory(parent);
				if (parentCategory != null) {
					// The parent is found, can handle this element now
					String id = element.getAttribute("id");
					String name = element.getAttribute("name");

					parentCategory.add(new WizardCategory(id, name,
							parentCategory));

					// This category is placed, thus mark as handled
					handled.add(element);
				}
			}

			// Remove all handled wizards.
			categoryElements.removeAll(handled);

		} while (!handled.isEmpty()); // No more elements can be handled

		// Create an other category
		WizardCategory other = new WizardCategory("other",
				Messages.ComputationWizardRegistry_0, root);
		root.add(other);

		// Give a warning for every category that could not be assigned to its
		// parent
		for (IConfigurationElement element : categoryElements) {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");

			other.add(new WizardCategory(id, name, other));

			IStatus status = new WarningStatus(
					element.getNamespaceIdentifier(), "No parent with id="
							+ element.getAttribute("parentCategory")
							+ " exists.");
			log.log(status);
		}

		// Add the wizards
		for (IConfigurationElement element : wizardElements) {
			String categoryId = element.getAttribute("category");

			// If the id is null add the category to 'other', else find the
			// category using its id.
			WizardCategory category = categoryId != null ? root
					.findCategory(categoryId) : other;

			if (category == null) {
				String pluginId = element.getNamespaceIdentifier();
				IStatus status = new WarningStatus(pluginId,
						"The category is undefined");
				log.log(status);

				category = other;
			}

			category.add(new WizardDescriptor(element, category));
		}

		// Create the primary wizards, each wizard this primary must be added to
		// the set only once.
		Set<IWizardDescriptor> validPrimaryWizard = new HashSet<IWizardDescriptor>();
		for (IConfigurationElement element : primaryWizardElements) {

			String wizardId = element.getAttribute("id");
			IWizardDescriptor descriptor = root.findWizard(wizardId);
			if (descriptor != null) {
				validPrimaryWizard.add(descriptor);
			} else {
				String pluginId = element.getNamespaceIdentifier();

				IStatus status = new WarningStatus(pluginId,
						"No wizard is defined with id " + wizardId
								+ ". The primary wizard could not be created.");
				log.log(status);
			}
		}

		primaryWizards = validPrimaryWizard
				.toArray(new IWizardDescriptor[validPrimaryWizard.size()]);

		initialized = true;

		assert root != null;
		assert primaryWizards != null;
	}
}