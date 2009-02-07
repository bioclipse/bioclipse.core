package net.bioclipse.rhino.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener, Ola Spjuth
 */
public class RunRhinoScriptActionPulldown extends Action 
											implements IMenuCreator {
	private static final String ACTION_ID = "RunRhinoScriptActionPulldown";
	private MenuManager pulldownMenuManager = null;
	private IAction runAsJobAction = null,
							runInMainThreadAction = null;

	public RunRhinoScriptActionPulldown() {
		super("Run Javascript...", AS_DROP_DOWN_MENU);
		// set an id to have it maximum once in a manager
		this.setId(ACTION_ID);
		
		// keep a link to the actions
		runAsJobAction = new RunRhinoScriptAsJobAction();
		runInMainThreadAction = new RunRhinoScriptInThreadAction();
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
			pulldownMenuManager.add(runInMainThreadAction);
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
		runInMainThreadAction.run();
	}
}