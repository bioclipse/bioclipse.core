/*
 *  $RCSfile$
 *  $Author: shk3 $
 *  $Date: 2007-05-29 16:45:26 +0200 (Tue, 29 May 2007) $
 *  $Revision: 3166 $
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

import java.net.URL;
import java.util.ArrayList;
import java.util.MissingResourceException;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.openscience.cdk.applications.jchempaint.StringHelper;
import org.openscience.cdk.controller.Controller2DModel;


/**
 *  This class makes the JCPToolBar
 *
 *@author        steinbeck
 *@cdk.created       16. Februar 2005
 *@cdk.module    jchempaint
 *@see           JChemPaintViewerOnlyPanel
 */
public class ToolBarMaker {
    private static ArrayList actionList = new ArrayList();


    public static ArrayList createToolbar(JCPMultiPageEditorContributor contributor) {
        actionList.clear();
        JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
        String[] toolKeys = StringHelper.tokenize(getToolbarResourceString("toolbar"));
        if (toolKeys.length != 0)
        {
            String[] sdiToolKeys = new String[(toolKeys.length)];
            for (int i = 0; i < toolKeys.length; i++)
            {
                int j = i - 0;
                sdiToolKeys[j] = toolKeys[i];
            }
            toolKeys = sdiToolKeys;
        }
        for (int i=0; i<toolKeys.length; i++) {
            String key = toolKeys[i];
            JCPAction jcpAction = null;
            String astr = jcpph.getResourceString(key + JCPAction.actionSuffix);
            if (astr != null) {
                jcpAction = new JCPAction().getAction(astr, false,false);
                jcpAction.setContributor(contributor);
                String tip = JCPLocalizationHandler.getInstance().getString(key + "Tooltip");
                if (tip != null)
                {
                    jcpAction.setToolTipText(tip);
                }
//                logger.debug("action: " + jcpAction);
            }
            if (jcpAction != null) {
                if (key.compareTo("-") == 0) {
                    Object separator = new Separator();
                    actionList.add(separator);
                }
                else {
                    URL url = jcpph.getResource(toolKeys[i] + JCPAction.imageSuffix);
                    ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
                    jcpAction.setImageDescriptor(imageDesc);
                    actionList.add(jcpAction);
                    if(toolKeys[i].equals("lasso")){
                        url = jcpph.getResource(toolKeys[i]+"active" + JCPAction.imageSuffix);
                        imageDesc = ImageDescriptor.createFromURL(url);
                        jcpAction.setImageDescriptor(imageDesc);
                        contributor.lastaction=jcpAction;
                    }
                }
            }
        }
        return actionList;
    }

    static String getToolbarResourceString(String key)
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
}

