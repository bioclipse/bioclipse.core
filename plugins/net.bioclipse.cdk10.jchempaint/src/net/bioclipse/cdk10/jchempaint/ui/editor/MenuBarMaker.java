/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.MissingResourceException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.openscience.cdk.applications.APIVersionTester;
import org.openscience.cdk.applications.jchempaint.StringHelper;

public class MenuBarMaker {
    private static ArrayList actionList = new ArrayList();
    private static String guiString = "bioclipse";
    private static JCPMultiPageEditorContributor contributor;
    
    public static ArrayList createMenuBar(JCPMultiPageEditorContributor theContributor, IMenuManager menuManager) {
        try{
        contributor = theContributor;
        String definition = getMenuResourceString("menubar");
        String[] menuKeys = StringHelper.tokenize(definition);
        for (int i = 0; i < menuKeys.length; i++) {
            String key = menuKeys[i];
            IMenuManager menu = createMenu(key);
            menuManager.add(menu);
        }
        return actionList;
        }catch(Throwable ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    private static IMenuManager createMenu(String key) {
        MenuManager submenu = new MenuManager(JCPLocalizationHandler.getInstance().getString(key));
        String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key));
        for (int i = 0; i < itemKeys.length; i++) {
            if (itemKeys[i].equals("-")) {
                Object separator = new Separator();
                actionList.add(separator);
            }
            else if (itemKeys[i].startsWith("@")) {
                IMenuManager menu = createMenu(itemKeys[i].substring(1));
                submenu.add(menu);
            }
            else if (itemKeys[i].endsWith("+")) {
                Action action=createAction(itemKeys[i].substring(0,itemKeys[i].length()-1),true);
                submenu.add(action);
                if(itemKeys[i].substring(0, itemKeys[i].length() - 1).equals("addImplHydrogen"))
                    action.setChecked(true);
            }
            else {
                submenu.add(createAction(itemKeys[i],false));
            }
        }
        return submenu;
    }

       
    private static JCPAction createAction(String key, boolean withCheckBox) {
        JCPAction jcpAction = null;
        String astr = JCPPropertyHandler.getInstance().getResourceString(key + JCPAction.actionSuffix);
        if (astr != null) {
            String translation = JCPLocalizationHandler.getInstance().getString(key);
            jcpAction = new JCPAction().getAction(astr, false, withCheckBox);
            jcpAction.setText(translation);
            jcpAction.setContributor(contributor);
            jcpAction.setEnabled(true);
        }
        return jcpAction;
    }

    private static String getMenuResourceString(String key) {
        String str;
        try {
            str = JCPPropertyHandler.getInstance().getGUIDefinition(guiString).getString(key);
        } catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }
    
    

}
