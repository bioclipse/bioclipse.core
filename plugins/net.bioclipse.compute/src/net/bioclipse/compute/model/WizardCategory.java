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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * WizardCategory is an adapted version WizardCollectionElement, which is an
 * internal workbench class and not accessible for plugins.
 *
 * @author Rob Schellhorn
 */
public class WizardCategory implements IWizardCategory {

	/**
	 * The sub categories of this category.
	 */
	private final List<WizardCategory> categories = new ArrayList<WizardCategory>();

	/**
	 * The identifier of this category.
	 */
	private final String id;

	/**
	 * The name of this category.
	 */
	private final String name;

	/**
	 * The parent category or <code>null</code> if this is a root category.
	 */
	private final IWizardCategory parent;

	/**
	 * The wizards that directly belong to this category.
	 */
	private final List<IWizardDescriptor> wizards = new ArrayList<IWizardDescriptor>();

	/**
	 * Creates a new <code>WizardCollection</code> for the given id and name.
	 * Parent can be <code>null</code>.
	 *
	 * @param id
	 *            The id of this collection.
	 * @param name
	 *            The name for this collection.
	 * @param parent
	 *            The parent category of this collection or <code>null</code>.
	 * @throws IllegalArgumentException
	 *             If the id or the name are not valid.
	 */
	public WizardCategory(String id, String name, IWizardCategory parent) {

		if (id == null || name == null) {
			throw new IllegalArgumentException();
		}

		this.id = id;
		this.name = name;
		this.parent = parent;
	}

	/**
	 * Adds a wizard to this category.
	 *
	 * @param wizard
	 *            The wizard to add.
	 */
	public void add(IWizardDescriptor wizard) {
		assert invariant() : "PRE: The invariant holds";
		assert wizard != null : "PRE: The wizard is an instance";
		assert wizard.getCategory() == this : "PRE: This is the wizard's category";

		wizards.add(wizard);

		assert wizards.contains(wizard) : "POST: This collection contains the wizard";
		assert invariant() : "POST: The invariant holds";
	}

	/**
	 * Adds the given category to this category as a sub category.
	 *
	 * @param category
	 *            The category to add.
	 */
	public void add(WizardCategory category) {
		assert invariant() : "PRE: The invariant holds";
		assert category != null : "PRE: The category is an instance";
		assert category.getParent() == this : "PRE: This is the category's parent";

		categories.add(category);

		assert categories.contains(category) : "POST: This collection contains the category";
		assert invariant() : "POST: The invariant holds";
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#findCategory(org.eclipse.core.runtime.IPath)
	 */
	public IWizardCategory findCategory(IPath path) {
		assert invariant() : "PRE: The invariant holds";

		return path != null ? findChildCollection(path) : null;
	}

	/**
	 * Returns the wizard category corresponding to the passed id, or
	 * <code>null</code> if such an object could not be found. This recurses
	 * through child categories.
	 *
	 * @param id
	 *            the id for the child category.
	 * @return the category, or <code>null</code> if not found
	 */
	protected WizardCategory findCategory(String id) {
		assert invariant() : "PRE: The invariant holds";

		// First check if this is the category we are looking for.
		if (this.id.equals(id)) {
			return this;
		}

		// If not, check the children.
		for (WizardCategory currentCategory : categories) {

			WizardCategory childCategory = currentCategory.findCategory(id);
			if (childCategory != null) {
				return childCategory;
			}
		}

		return null;
	}

	/**
	 * Returns the wizard collection child object corresponding to the passed
	 * path (relative to this object), or <code>null</code> if such an object
	 * could not be found.
	 *
	 * @param searchPath
	 *            org.eclipse.core.runtime.IPath
	 * @return WizardCollectionElement
	 * @throws IllegalArgumentException
	 *             If the searchPath is <code>null</code>.
	 */
	protected IWizardCategory findChildCollection(IPath searchPath) {
		assert invariant() : "PRE: The invariant holds";

		if (searchPath == null) {
			throw new IllegalArgumentException();
		}

		String searchString = searchPath.segment(0);
		for (WizardCategory category : categories) {
			if (category.getId().equals(searchString)) {
				if (searchPath.segmentCount() == 1) {
					return category;
				}

				return category.findChildCollection(searchPath
						.removeFirstSegments(1));
			}
		}

		return null;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#findWizard(java.lang.String)
	 */
	public IWizardDescriptor findWizard(String id) {
		assert invariant() : "PRE: The invariant holds";

		return findWizard(id, true);
	}

	/**
	 * Returns this collection's associated wizard object corresponding to the
	 * passed id, or <code>null</code> if such an object could not be found.
	 *
	 * @param searchId
	 *            the id to search on
	 * @param recursive
	 *            whether to search recursivly
	 * @return the element
	 */
	protected IWizardDescriptor findWizard(String searchId, boolean recursive) {
		assert invariant() : "PRE: The invariant holds";

		// First check the wizards in this category.
		for (IWizardDescriptor currentWizard : wizards) {
			if (currentWizard.getId().equals(searchId)) {
				return currentWizard;
			}
		}

		// If the wizard does not belong to this category and we are not
		// searching recursive, it could not be found.
		if (!recursive) {
			return null;
		}

		// If we are searching recursively, look in the sub categories.
		for (WizardCategory category : categories) {
			IWizardDescriptor result = category.findWizard(searchId, true);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getCategories()
	 */
	public IWizardCategory[] getCategories() {
		assert invariant() : "PRE: The invariant holds";

		return categories.toArray(new IWizardCategory[categories.size()]);
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getId()
	 */
	public String getId() {
		assert invariant() : "PRE: The invariant holds";

		return id;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getLabel()
	 */
	public String getLabel() {
		assert invariant() : "PRE: The invariant holds";

		return name;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getParent()
	 */
	public IWizardCategory getParent() {
		assert invariant() : "PRE: The invariant holds";

		return parent;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getPath()
	 */
	public IPath getPath() {
		assert invariant() : "PRE: The invariant holds";

		return parent != null ? parent.getPath().append(getId()) : Path.EMPTY;
	}

	/*
	 * @see org.eclipse.ui.wizards.IWizardCategory#getWizards()
	 */
	public IWizardDescriptor[] getWizards() {
		assert invariant() : "PRE: The invariant holds";

		return wizards.toArray(new IWizardDescriptor[wizards.size()]);
	}

	/**
	 * @return True if this instance satisfies the class invariant, false
	 *         otherwise.
	 */
	private boolean invariant() {
		return categories != null && id != null && name != null
				&& wizards != null;
	}

	/**
	 * Removes the given wizard from this category. The wizard is only if it is
	 * a child of this category, subcategories are not checked.
	 *
	 * @param wizard
	 *            The wizard to remove.
	 */
	public void remove(IWizardDescriptor wizard) {
		assert invariant() : "PRE: The invariant holds";

		wizards.remove(wizard);

		assert !wizards.contains(wizard) : "POST: This collection does not contain the wizard";
		assert invariant() : "POST: The invariant holds";
	}

	/**
	 * Removes the given category from this category. The category is only
	 * removed if it is a child of this category. Subcategories are not checked.
	 *
	 * @param category
	 *            The category to remove.
	 */
	public void remove(WizardCategory category) {
		assert invariant() : "PRE: The invariant holds";

		categories.remove(category);

		assert !categories.contains(category) : "POST: This collection does not contain the category";
		assert invariant() : "POST: The invariant holds";
	}
}