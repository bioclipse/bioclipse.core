package net.bioclipse.jseditor.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;

import net.bioclipse.jseditor.Activator;
import net.bioclipse.jseditor.actions.RunRhinoScriptActionPulldown;
import net.bioclipse.jseditor.exceptions.ScriptException;
/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
public class JsEditor extends TextEditor {
	public static RunRhinoScriptActionPulldown runRhinoScriptActionPulldown = null;

	private JsColorManager colorManager;
	
	public JsEditor() {
		super();
		colorManager = new JsColorManager();
		setSourceViewerConfiguration(new JsEditorConfiguration(colorManager));
		setDocumentProvider(new JsDocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	/**
	 * Add action bars by overriding
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getEditorSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		if (manager.find(runRhinoScriptActionPulldown.getId()) == null) {
			manager.add(runRhinoScriptActionPulldown);
		}
	}

	/**
	 * Create actions by overriding
	 */
	@Override
	protected void createActions() {
		super.createActions();
		if (runRhinoScriptActionPulldown == null) {
			runRhinoScriptActionPulldown = new RunRhinoScriptActionPulldown();
			runRhinoScriptActionPulldown.setToolTipText("Run Javascript");
			runRhinoScriptActionPulldown.setImageDescriptor(Activator.getImageDescriptor("icons/png/jsfilerun.png"));
		}
	}
	
	public String getScriptString() throws ScriptException {
		// Get the command from the editor
		
		if (isDirty()) {
			boolean result = MessageDialog.openQuestion(getSite().getShell(),
					"Rhino Javascript Editor", "The script was modifed. " +
							"Save changes before running the script?");
			if (result) {
				doSave(null);
			}
		}

		String scriptString =
			getDocumentProvider().getDocument(getEditorInput()).get();
		
		if (scriptString == null) {
			showMessage("The script is null; invalide file?");
			throw new ScriptException("The script is null; invalide file?");
		}
		if (scriptString.compareTo("") == 0 ) {
			showMessage("The script is empty.");
			throw new ScriptException("The script is empty.");
		}
		
		return scriptString;
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Rhino Javascript Plug-In",
				message);
	}
}