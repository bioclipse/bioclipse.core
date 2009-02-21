package net.bioclipse.jseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

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
public class RunRhinoScriptActionPulldown extends Action 
											implements IMenuCreator {
	private static final String ACTION_ID = "RunRhinoScriptActionPulldown";
	private MenuManager pulldownMenuManager = null;
	private IAction runAsJobAction = null;

	public RunRhinoScriptActionPulldown() {
		super("Run Javascript...", AS_DROP_DOWN_MENU);
		// set an id to have it maximum once in a manager
		this.setId(ACTION_ID);
		
		// keep a link to the actions
		runAsJobAction = new RunRhinoScriptAsJobAction();
	}

	public void dispose() {
		if (pulldownMenuManager != null) {
			pulldownMenuManager.dispose();
			pulldownMenuManager = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (pulldownMenuManager == null) {
			// we have to create a new menu first!
			pulldownMenuManager = new MenuManager();
			pulldownMenuManager.createContextMenu(parent);
			//pulldownMenuManager.add(runInMainThreadAction);
			pulldownMenuManager.add(runAsJobAction);
		}
		return pulldownMenuManager.getMenu();
	}
	
	public Menu getMenu(Menu parent) {
		return null;
	}

	public IMenuCreator getMenuCreator() {
		return this;
	}

	public void run() {
		// run default way
		runAsJobAction.run();
	}
}