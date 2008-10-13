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

package net.bioclipse.compute.actions;

import net.bioclipse.compute.Activator;
import net.bioclipse.compute.IComputationContextIds;
import net.bioclipse.compute.wizards.selectcomputation.SelectComputationWizard;
import net.bioclipse.core.domain.IBioObject;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Schellhorn
 */
public class SelectComputationAction implements
		IWorkbenchWindowPulldownDelegate {

	/**
	 *
	 */
	static final String DIALOG_SETTINGS = "SelectComputationAction";

	/**
	 * The wizard dialog height
	 */
	private static final int WIZARD_HEIGHT = 500;

	/**
	 * The wizard dialog width
	 */
	private static final int WIZARD_WIDTH = 500;

	/**
	 *
	 */
	private final IMenuCreator menuCreator = new IMenuCreator() {

		private MenuManager dropDownMenuMgr;

		/**
		 * Creates the menu manager for the drop-down.
		 */
		private void createDropDownMenuMgr() {
			if (dropDownMenuMgr == null) {
				dropDownMenuMgr = new MenuManager();
				dropDownMenuMgr.add(newWizardMenu);
				dropDownMenuMgr.add(new Separator("Additions"));
				dropDownMenuMgr.add(new Action("Other") {

					public void run() {
						SelectComputationAction.this.run(this);
					}
				});
			}
		}

		/*
		 * @see org.eclipse.jface.action.IMenuCreator#dispose()
		 */
		public void dispose() {
			if (dropDownMenuMgr != null) {
				dropDownMenuMgr.dispose();
				dropDownMenuMgr = null;
			}
		}

		/*
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
		 */
		public Menu getMenu(Control parent) {
			createDropDownMenuMgr();
			return dropDownMenuMgr.createContextMenu(parent);
		}

		/*
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
		 */
		public Menu getMenu(Menu parent) {
			createDropDownMenuMgr();
			Menu menu = new Menu(parent);

			for (IContributionItem item : dropDownMenuMgr.getItems()) {
				IContributionItem newItem = item;
				if (item instanceof ActionContributionItem) {
					newItem = new ActionContributionItem(
							((ActionContributionItem) item).getAction());
				}
				newItem.fill(menu, -1);
			}

			return menu;
		}
	};

	/**
	 *
	 */
	private final IContributionItem newWizardMenu = new ContributionItem() {

		public void fill(Menu menu, int index) {
			// TODO
		}
	};

	/**
	 * The current selection in the workbench.
	 */
	private ISelection selection;

	/**
	 * The parent workbench window of this action.
	 */
	private IWorkbenchWindow window;

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		newWizardMenu.dispose();
		menuCreator.dispose();
		window = null;
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		return menuCreator.getMenu(parent);
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		/*
		 * We will cache window object in order to be able to provide parent
		 * shell for the message dialog.
		 */
		this.window = window;
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		if (window == null) {
			// Disposed
			return;
		}

		// Create a valid (not null) selection
		IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
		if (selection instanceof IStructuredSelection) {
			selectionToPass = (IStructuredSelection) selection;
		} else {
			// Try to create a selection from an active editor's input
			IWorkbenchPart part = window.getPartService().getActivePart();
			if (part instanceof IEditorPart) {
				IEditorInput input = ((IEditorPart) part).getEditorInput();
				// TODO : FIXME
				Object resource = input.getAdapter(IBioObject.class);
				if (resource != null) {
					selectionToPass = new StructuredSelection(resource);
				}
			}
		}

		// Set up the wizard
		SelectComputationWizard wizard = new SelectComputationWizard();
		wizard.init(window.getWorkbench(), selectionToPass);

		// Retrieve the settings for this wizard
		IDialogSettings settings = Activator.getDefault()
				.getDialogSettings();
		IDialogSettings wizardSettings = settings.getSection(DIALOG_SETTINGS);
		if (wizardSettings == null) {
			wizardSettings = settings.addNewSection(DIALOG_SETTINGS);
		}

		wizard.setDialogSettings(wizardSettings);
		wizard.setForcePreviousAndNextButtons(true);
		wizard.setNeedsProgressMonitor(true);

		// Open the dialog and let it create a shell
		Dialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();

		// Configure the dialog's shell
		Shell shell = dialog.getShell();
		shell.setSize(Math.max(WIZARD_WIDTH, shell.getSize().x), WIZARD_HEIGHT);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(shell,
				IComputationContextIds.COMPUTATION_WIZARD);

		// Open the dialog, and wait until the user pressed OK. If so the wizard
		// should be able to give a ready-to-run computation job.
		if (dialog.open() == Dialog.OK) {
			Job computationJob = wizard.getComputationJob();
			computationJob.setUser(true);
			computationJob.schedule();
		}
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		/*
		 * We will cache selection object in order to be able read it when the
		 * action is activated.
		 */
		this.selection = selection;
	}
}
