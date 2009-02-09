/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *
 ******************************************************************************/
package net.bioclipse.ui;


import net.bioclipse.ui.actions.SoftwareUpdatesAction;
import net.bioclipse.ui.actions.UpdateAction;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

/**
 * The action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to the Bioclipse workbench
 *
 * @author ola
 */
@SuppressWarnings("restriction")
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    /* Actions - important to allocate these only in makeActions, and then use
     * them in the fill methods. This ensures that the actions aren't recreated
     * when fillActionBars is called with FILL_PROXY. */

    private IWorkbenchAction helpSearchAction;
    private IWorkbenchAction introAction;

    private IWorkbenchAction closeAction;

    private IWorkbenchAction closeAllAction;

    private IWorkbenchAction closeAllSavedAction;

    private IWorkbenchAction saveAction;
    
    private IWorkbenchAction switchWorkspaceAction;

    private IWorkbenchAction importAction;
    private IWorkbenchAction exportAction;

    private IWorkbenchAction saveAllAction;

    private IWorkbenchAction saveAsAction;

    private IWorkbenchAction undoAction;

    private IWorkbenchAction redoAction;

    private IWorkbenchAction cutAction;

    private IWorkbenchAction copyAction;

    private IWorkbenchAction pasteAction;

    private IWorkbenchAction selectAllAction;

    private IWorkbenchAction findAction;

    private IWorkbenchAction revertAction;

    private IWorkbenchAction quitAction;

    private IWorkbenchAction preferencesAction;

    private IWorkbenchAction helpAction;

    private IWorkbenchAction aboutAction, printAction, newAction;

    private IWorkbenchAction resetPerspectiveAction;

    private IAction softwareUpdatesAction;

    //TODO: Why is this an IContributionItem?
    private IContributionItem showViewItem;


    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
        removeUnwantedActions();
    }

    /**
     * Create and register actions
     */
    protected void makeActions(IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        // register(showViewItem);

        newAction = ActionFactory.NEW.create(window);
        newAction.setText("New...");
        register(newAction);

        // newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
        // newAction.setText("New...");
        // register(newAction);

        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);

        switchWorkspaceAction = IDEActionFactory.OPEN_WORKSPACE.create(window);
        register(switchWorkspaceAction);

        importAction = ActionFactory.IMPORT.create(window);
        register(importAction);

        exportAction = ActionFactory.EXPORT.create(window);
        register(exportAction);

        printAction = ActionFactory.PRINT.create(window);
        register(printAction);

        saveAsAction = ActionFactory.SAVE_AS.create(window);
        register(saveAsAction);

        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        register(saveAllAction);

        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);

        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);

        cutAction = ActionFactory.CUT.create(window);
        register(cutAction);

        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);

        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);

        selectAllAction = ActionFactory.SELECT_ALL.create(window);
        register(selectAllAction);

        findAction = ActionFactory.FIND.create(window);
        register(findAction);

        closeAction = ActionFactory.CLOSE.create(window);
        register(closeAction);

        closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        register(closeAllAction);

        closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(window);
        register(closeAllSavedAction);

        revertAction = ActionFactory.REVERT.create(window);
        register(revertAction);

        quitAction = ActionFactory.QUIT.create(window);
        register(quitAction);

        helpAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpAction);

        softwareUpdatesAction = new SoftwareUpdatesAction(window);
        register(softwareUpdatesAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);

        preferencesAction = ActionFactory.PREFERENCES.create(window);
        register(preferencesAction);
        introAction = ActionFactory.INTRO.create(window);
        register(introAction);
        {
            helpSearchAction = ActionFactory.HELP_SEARCH.create(window);
            register(helpSearchAction);
        }

    }


    /**
     * Fill the standard menus
     */
    protected void fillMenuBar(IMenuManager menuBar) {

        /*
         * File menu
         */
        MenuManager fileMenu = new MenuManager("&File",
                IWorkbenchActionConstants.M_FILE);
        fileMenu.add(new Separator());
        fileMenu.add(newAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(revertAction);
        fileMenu.add(new Separator("SWITCHWORKSPACE"));
        fileMenu.add(switchWorkspaceAction);
        fileMenu.add(new Separator("IMPORTandEXPORT"));
        fileMenu.add(importAction);
        fileMenu.add(exportAction);
        // menu.add(ContributionItemFactory.REOPEN_EDITORS.create(getWindow()));
        fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(new Separator());
        fileMenu.add(quitAction);


        /*
         * Edit menu
         */
        MenuManager editMenu = new MenuManager("&Edit",
                IWorkbenchActionConstants.M_EDIT);
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.UNDO_EXT));
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.CUT_EXT));
        editMenu.add(selectAllAction);
        editMenu.add(new Separator());
        editMenu.add(findAction);
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.ADD_EXT));
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
        editMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));


        /*
         * Window menu
         */
        MenuManager windowMenu = new MenuManager("&Window",
                IWorkbenchActionConstants.M_WINDOW);
        MenuManager showViewMenuMgr = new MenuManager("Show View", "showView");
        showViewMenuMgr.add(showViewItem);
        windowMenu.add(showViewMenuMgr);
        windowMenu.add(new Separator());
        windowMenu.add(resetPerspectiveAction);
        windowMenu.add(new Separator());
        windowMenu.add(preferencesAction);


        /*
         * Help menu
         */
        MenuManager helpMenu = new MenuManager(
                "&Help", IWorkbenchActionConstants.M_HELP);

        //Intro action
        helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
        helpMenu.add(introAction);
        helpMenu.add(helpAction);
        helpMenu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
        helpMenu.add(new Separator());
        helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        helpMenu.add(new Separator());
        helpMenu.add(softwareUpdatesAction);

        // About should always be at the bottom
        helpMenu.add(new Separator());
        helpMenu.add(aboutAction);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);


    }

    protected void fillCoolBar(ICoolBarManager coolBar) {

        ToolBarManager manager = new ToolBarManager(SWT.FLAT | SWT.WRAP);
        manager.add(newAction);
        manager.add(saveAction);
        manager.add(printAction);
        manager.add(new Separator());
        manager.add(cutAction);
        manager.add(copyAction);
        manager.add(pasteAction);
        manager.add(undoAction);
        manager.add(redoAction);

        coolBar.add(manager);

        manager.add(new Separator());

        manager.add(helpSearchAction);

        manager.add(helpAction);

    }


	private void removeUnwantedActions(){

    	ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
    	IActionSetDescriptor[] actionSets = reg.getActionSets();
    	// removing annoying gotoLastPosition Message.
    	String actionSetId = "org.eclipse.ui.edit.text.actionSet.navigation";
    	for (int i = 0; i <actionSets.length; i++)
    	{
    		if (!actionSets[i].getId().equals(actionSetId))
    			continue;
    		IExtension ext = actionSets[i].getConfigurationElement()
    		.getDeclaringExtension();
    		reg.removeExtension(ext, new Object[] { actionSets[i] });
    	}

    	// Removing convert line delimiters menu.
    	actionSetId = "org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo";
    	for (int i = 0; i <actionSets.length; i++)
    	{
    		if (!actionSets[i].getId().equals(actionSetId))
    			continue;
    		IExtension ext = actionSets[i].getConfigurationElement()
    		.getDeclaringExtension();
    		reg.removeExtension(ext, new Object[] { actionSets[i] });
    	}

    	// Removing convert line delimiters menu.
    	actionSetId = "org.eclipse.ui.actionSet.openFiles";
    	for (int i = 0; i <actionSets.length; i++)
    	{
    		if (!actionSets[i].getId().equals(actionSetId))
    			continue;
    		IExtension ext = actionSets[i].getConfigurationElement()
    		.getDeclaringExtension();
    		reg.removeExtension(ext, new Object[] { actionSets[i] });
    	}
    	
    }
    

}
