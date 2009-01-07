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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.bioclipse.compute.IComputationContextIds;
import net.bioclipse.compute.model.ComputationCollectionSorter;
import net.bioclipse.compute.model.ComputationContentProvider;
import net.bioclipse.compute.model.ComputationLabelProvider;
import net.bioclipse.compute.model.WizardDescriptor;
import net.bioclipse.compute.model.WizardFilter;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardContainer2;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * NewWizardNewPage
 * 
 * @author Rob Schellhorn
 */
public class SelectComputationPage extends WizardSelectionPage {

	// id constants
	static final String DIALOG_SETTING_SECTION = "SelectComputationPage.";

	static final String STORE_EXPANDED_CATEGORIES_ID = DIALOG_SETTING_SECTION
			+ "STORE_EXPANDED_CATEGORIES_ID";

	static final String STORE_SELECTED_ID = DIALOG_SETTING_SECTION
			+ "STORE_SELECTED_ID";

	private boolean canFinishEarly = false, hasPages = true;

	private FilteredTree filteredTree;

	private ToolItem helpButton;

	private final IWizardDescriptor[] primaryWizards;

	private IWizardDescriptor selectedElement;

	private final Map<IWizardDescriptor, ComputationWizardNode> selectedWizards = new Hashtable<IWizardDescriptor, ComputationWizardNode>();

	private SelectComputationWizard wizard;

	private final IWizardCategory wizardCategories;

	private String wizardHelpHref;

	/**
	 * @param wizard
	 * @param wizardCategories
	 * @param primaryWizards
	 */
	public SelectComputationPage(SelectComputationWizard wizard,
			IWizardCategory wizardCategories, IWizardDescriptor[] primaryWizards) {
		super("pageName");

		this.wizard = wizard;
		this.wizardCategories = wizardCategories;
		this.primaryWizards = primaryWizards;

		setTitle("Select a wizard");
	}

	/**
	 * 
	 */
	public void advanceToNextPageOrFinish() {
		if (canFlipToNextPage()) {
			getContainer().showPage(getNextPage());
		} else if (canFinishEarly()) {
			if (getWizard().performFinish()) {
				((WizardDialog) getContainer()).close();
			}
		}
	}

	/**
	 * Answers whether the currently selected page, if any, advertises that it
	 * may finish early.
	 * 
	 * @return whether the page can finish early
	 */
	public boolean canFinishEarly() {
		return canFinishEarly;
	}

	/*
	 * @see org.eclipse.jface.wizard.WizardSelectionPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage() {
		return hasPages && super.canFlipToNextPage();
	}

	/*
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Font wizardFont = parent.getFont();
		// top level group
		Composite outerContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		outerContainer.setLayout(layout);

		Label wizardLabel = new Label(outerContainer, SWT.NONE);
		GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
		outerContainer.setLayoutData(data);
		wizardLabel.setFont(wizardFont);
		wizardLabel.setText("&Wizards:");

		Composite innerContainer = new Composite(outerContainer, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		innerContainer.setLayout(layout);
		innerContainer.setFont(wizardFont);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		innerContainer.setLayoutData(data);

		filteredTree = createFilteredTree(innerContainer);
		createOptionsButtons(innerContainer);

		updateDescription(null);
		restoreWidgetValues();

		wizard.getWorkbench().getHelpSystem().setHelp(outerContainer,
				IComputationContextIds.SELECT_COMPUTATION_PAGE);

		setControl(outerContainer);
	}

	/**
	 * @param parent
	 * @return
	 */
	protected FilteredTree createFilteredTree(Composite parent) {
		PatternFilter filteredTreeFilter = new WizardFilter();
		FilteredTree filterTree = new FilteredTree(parent, SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, filteredTreeFilter);

		final TreeViewer treeViewer = filterTree.getViewer();
		treeViewer.setAutoExpandLevel(2);
		treeViewer.setContentProvider(new ComputationContentProvider(wizard
				.getSelection()));
		treeViewer.setLabelProvider(new ComputationLabelProvider());
		treeViewer.setSorter(new ComputationCollectionSorter());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection s = (IStructuredSelection) event
						.getSelection();

				selectionChanged(new SelectionChangedEvent(treeViewer, s));

				Object element = s.getFirstElement();
				if (treeViewer.isExpandable(element)) {
					treeViewer.setExpandedState(element, !treeViewer
							.getExpandedState(element));
				} else if (element instanceof WizardDescriptor) {
					advanceToNextPageOrFinish();
				}
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				SelectComputationPage.this.selectionChanged(event);
			}
		});

		List<Object> inputArray = new ArrayList<Object>();

		for (IWizardDescriptor wizard : primaryWizards) {
			inputArray.add(wizard);
		}

		for (IWizardCategory category : wizardCategories.getCategories()) {
			inputArray.add(category);
		}

		treeViewer.setInput(new AdaptableList(inputArray));

		return filterTree;
	}

	/**
	 * Create the Show All and help buttons at the bottom of the page.
	 * 
	 * @param parent
	 *            the parent composite on which to create the widgets
	 */
	private void createOptionsButtons(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		helpButton = new ToolItem(toolBar, SWT.NONE);
		helpButton.setImage(JFaceResources.getImage(Dialog.DLG_IMG_HELP));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_END);
		data.horizontalSpan = 2;
		toolBar.setLayoutData(data);

		helpButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(
						wizardHelpHref);
			}
		});
	}

	/**
	 * Expands the wizard categories in this page's category viewer that were
	 * expanded last time this page was used. If a category that was previously
	 * expanded no longer exists then it is ignored.
	 * 
	 * @param settings
	 */
	protected void expandPreviouslyExpandedCategories(IDialogSettings settings) {
		String[] expandedCategoryPaths = settings
				.getArray(STORE_EXPANDED_CATEGORIES_ID);
		if (expandedCategoryPaths == null || expandedCategoryPaths.length == 0) {
			return;
		}

		List<IWizardCategory> categoriesToExpand = new ArrayList<IWizardCategory>(
				expandedCategoryPaths.length);

		if (wizardCategories != null) {
			for (String path : expandedCategoryPaths) {
				IWizardCategory category = wizardCategories
						.findCategory(new Path(path));
				if (category != null) {
					categoriesToExpand.add(category);
				}
			}
		}

		if (!categoriesToExpand.isEmpty()) {
			filteredTree.getViewer().setExpandedElements(
					categoriesToExpand.toArray());
		}

	}

	/*
	 * @see org.eclipse.jface.wizard.WizardSelectionPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		if (!hasPages) {
			return null;
		}
		return super.getNextPage();
	}

	/**
	 * Returns the single selected object contained in the passed
	 * selectionEvent, or <code>null</code> if the selectionEvent contains
	 * either 0 or 2+ selected objects.
	 */
	protected Object getSingleSelection(IStructuredSelection selection) {
		return selection.size() == 1 ? selection.getFirstElement() : null;
	}

	/**
	 * Set self's widgets to the values that they held last time this page was
	 * open
	 */
	protected void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			expandPreviouslyExpandedCategories(settings);
			selectPreviouslySelected(settings);
		}
	}

	/**
	 * Store the current values of self's widgets so that they can be restored
	 * in the next instance of self.
	 */
	protected void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			storeExpandedCategories(settings);
			storeSelectedCategoryAndWizard(settings);
		}
	}

	/**
	 * The user selected either new wizard category(s) or wizard element(s).
	 * Proceed accordingly.
	 * 
	 * @param selectionEvent
	 */
	public void selectionChanged(SelectionChangedEvent selectionEvent) {
		setErrorMessage(null);
		setMessage(null);

		IStructuredSelection selection = (IStructuredSelection) selectionEvent
				.getSelection();
		Object selectedObject = selection.isEmpty() ? null : selection
				.getFirstElement();

		if (selectedObject instanceof IWizardDescriptor) {
			if (selectedObject == selectedElement) {
				return;
			}
			updateWizardSelection((IWizardDescriptor) selectedObject);
		} else {
			selectedElement = null;
			canFinishEarly = false;
			hasPages = false;
			setSelectedNode(null);
			updateDescription(null);
		}
	}

	/**
	 * Selects the wizard category and wizard in this page that were selected
	 * last time this page was used. If a category or wizard that was previously
	 * selected no longer exists then it is ignored.
	 */
	protected void selectPreviouslySelected(IDialogSettings settings) {
		String selectedId = settings.get(STORE_SELECTED_ID);
		if (selectedId == null) {
			return;
		}

		if (wizardCategories == null) {
			return;
		}

		Object selected = wizardCategories.findCategory(new Path(selectedId));

		if (selected == null) {
			selected = wizardCategories.findWizard(selectedId);

			if (selected == null) {
				// if we cant find either a category or a wizard, abort.
				return;
			}
		}

		// work around for 62039
		final StructuredSelection selection = new StructuredSelection(selected);
		filteredTree.getViewer().getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						filteredTree.getViewer().setSelection(selection, true);
					}
				});
	}

	/**
	 * Stores the collection of currently-expanded categories in this page's
	 * dialog store, in order to recreate this page's state in the next instance
	 * of this page.
	 * 
	 * @param settings
	 */
	protected void storeExpandedCategories(IDialogSettings settings) {
		Object[] expandedElements = filteredTree.getViewer()
				.getExpandedElements();

		List<String> expandedElementPaths = new ArrayList<String>(
				expandedElements.length);
		for (Object o : expandedElements) {
			if (o instanceof IWizardCategory) {
				expandedElementPaths.add(((IWizardCategory) o).getPath()
						.toString());
			}
		}

		settings.put(STORE_EXPANDED_CATEGORIES_ID, expandedElementPaths
				.toArray(new String[expandedElementPaths.size()]));
	}

	/**
	 * Stores the currently-selected element in this page's dialog store, in
	 * order to recreate this page's state in the next instance of this page.
	 */
	protected void storeSelectedCategoryAndWizard(IDialogSettings settings) {
		Object selected = getSingleSelection((IStructuredSelection) filteredTree
				.getViewer().getSelection());

		if (selected != null) {
			if (selected instanceof IWizardCategory) {
				settings.put(STORE_SELECTED_ID, ((IWizardCategory) selected)
						.getPath().toString());
			} else {
				// else its a wizard
				settings.put(STORE_SELECTED_ID, ((IWizardDescriptor) selected)
						.getId());
			}
		}
	}

	/**
	 * Update the current description controls.
	 * 
	 * @param selectedObject
	 *            the new wizard
	 */
	private void updateDescription(IWizardDescriptor selectedObject) {
		if (selectedObject != null) {
			setDescription(selectedObject.getDescription());
		}

		if (selectedObject != null) {
			wizardHelpHref = selectedObject.getHelpHref();
		} else {
			wizardHelpHref = null;
		}

		if (wizardHelpHref != null) {
			helpButton.setEnabled(true);
			helpButton.setToolTipText("More help");
		} else {
			helpButton.setEnabled(false);
			helpButton.setToolTipText("No help");
		}

		IWizardContainer container = getWizard().getContainer();
		if (container instanceof IWizardContainer2) {
			((IWizardContainer2) container).updateSize();
		}
	}

	/**
	 * @param selectedObject
	 */
	private void updateWizardSelection(IWizardDescriptor selectedObject) {
		ComputationWizardNode selectedNode = null;
		selectedElement = selectedObject;
		if (selectedWizards.containsKey(selectedObject)) {
			selectedNode = (ComputationWizardNode) selectedWizards
					.get(selectedObject);
		} else {
			selectedNode = new ComputationWizardNode(selectedObject, wizard);
			selectedNode.getWizard(); // FIXME If not called NPE on pages
			selectedWizards.put(selectedObject, selectedNode);
		}

		canFinishEarly = selectedObject.canFinishEarly();
		hasPages = selectedObject.hasPages();
		setSelectedNode(selectedNode);

		updateDescription(selectedObject);
	}
}