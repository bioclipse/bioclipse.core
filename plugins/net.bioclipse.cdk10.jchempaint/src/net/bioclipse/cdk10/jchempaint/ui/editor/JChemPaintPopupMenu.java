/*
 *  $RCSfile$
 *  $Author: shk3 $
 *  $Date: 2006-07-26 21:58:52 +0200 (Wed, 26 Jul 2006) $
 *  $Revision: 1503 $
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Component;
import java.util.MissingResourceException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.openscience.cdk.applications.jchempaint.StringHelper;
import org.openscience.cdk.controller.CDKPopupMenu;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  A pop-up menu for JChemPaint
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class JChemPaintPopupMenu extends CDKPopupMenu
{

    private LoggingTool logger;
    private JCPMultiPageEditorContributor contributor;
    private static DrawingPanel drawingPanel;


    /**
     *  Constructor for the JChemPaintPopupMenu object
     * @param contributor 
     *
     *@param  jcpPanel  Description of the Parameter
     *@param  type      Description of the Parameter
     */
    public JChemPaintPopupMenu(JCPMultiPageEditorContributor contributor, String type)
    {
        logger = new LoggingTool(this);
        this.contributor = contributor;
        this.drawingPanel = ((IJCPBasedEditor)contributor.getActiveEditorPart()).getDrawingPanel();
        createPopupMenu(type);
    }


    /**
     *  Description of the Method
     * @param contributor 
     *
     *@param  jcpPanel  Description of the Parameter
     *@param  type      Description of the Parameter
     */
    protected void createPopupMenu(String type)
    {
        String[] menuKeys = StringHelper.tokenize(getMenuResourceString(type + "popup"));
        String menuTitle = JCPLocalizationHandler.getInstance().getString(type + "MenuTitle");
        JMenuItem titleMenuItem = new JMenuItem(menuTitle);
        titleMenuItem.setEnabled(false);
        titleMenuItem.setArmed(false);
        this.add(titleMenuItem);
        this.addSeparator();
        for (int i = 0; i < menuKeys.length; i++)
        {
            String menuKey = menuKeys[i];
            if (menuKey.equals("-"))
            {
                this.addSeparator();
            } else if (menuKey.startsWith("@"))
            {
                JMenu me = createMenu(contributor, menuKey.substring(1));
                this.add(me);
            } else
            {
                JMenuItem item = createMenuItem(menuKey,false);
                if (item != null)
                {
                    this.add(item);
                }
            }
        }
    }


    /**
     *  Craetes a JMenuItem given by a String and adds the right ActionListener to
     *  it.
     *
     *@param  cmd       String The Strin to identify the MenuItem
     *@param  jcpPanel  Description of the Parameter
     *@return           JMenuItem The created JMenuItem
     */
    protected JMenuItem createMenuItem(String cmd, boolean withCheckBox)
    {
        logger.debug("Creating menu item: ", cmd);
        String translation = "***" + cmd + "***";
        try
        {
            translation = JCPLocalizationHandler.getInstance().getString(cmd);
        } catch (MissingResourceException mre)
        {
            logger.error("Could not find translation for: " + cmd);
        }
        JMenuItem mi = new JMenuItem(translation);
        if(withCheckBox)
            mi=new JCheckBoxMenuItem(translation);
        String astr = JCPPropertyHandler.getInstance().getResourceString(cmd + JCPAction.actionSuffix);
        if (astr == null)
        {
            astr = cmd;
        }
        mi.setActionCommand(astr);
        JCPAction a = new JCPAction().getAction(astr, true,withCheckBox);
        a.setContributor(contributor);
        if (a != null)
        {
            mi.addActionListener(new PopUpListener(contributor, a));
            mi.setEnabled(a.isEnabled());
        } else
        {
            logger.warn("Could not find JCPAction class for:" + astr);
            mi.setEnabled(false);
        }
        return mi;
    }


    /**
     *  Description of the Method
     * @param contributor 
     *
     *@param  jcpPanel  Description of the Parameter
     *@param  key       Description of the Parameter
     *@return           Description of the Return Value
     */
    protected JMenu createMenu( JCPMultiPageEditorContributor contributor, String key)
    {
        logger.debug("Creating menu: ", key);
        String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key));
        String translation = "***" + key + "***";
        try
        {
            translation = JCPLocalizationHandler.getInstance().getString(key);
        } catch (MissingResourceException mre)
        {
            logger.error("Could not find translation for: " + key);
        }
        JMenu menu = new JCPMenu(translation);
        for (int i = 0; i < itemKeys.length; i++)
        {
            if (itemKeys[i].equals("-"))
            {
                menu.addSeparator();
            } else if (itemKeys[i].startsWith("@"))
            {
                String menuTitle = itemKeys[i].substring(1);
                JMenu me = createMenu(contributor, menuTitle);
                menu.add(me);
            }else if (itemKeys[i].endsWith("+")) {
                menu.add(createMenuItem(itemKeys[i].substring(0,itemKeys[i].length()-1),true));
            }else
            {
                JMenuItem mi = createMenuItem(itemKeys[i],false);
                menu.add(mi);
            }
        }
        return menu;
    }


    /**
     *  Gets the menuResourceString attribute of the JChemPaint object
     *
     *@param  key  Description of the Parameter
     *@return      The menuResourceString value
     */
    public String getMenuResourceString(String key)
    {
        String str;
        try
        {
            str = JCPPropertyHandler.getInstance().getGUIDefinition().getString(key);
        } catch (MissingResourceException mre)
        {
            str = null;
        }
        return str;
    }


    @Override
    public void show(Component arg0, int arg1, int arg2) {
        super.show(arg0, arg1, arg2);
        this.repaint();
    }


    public static DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }
}

