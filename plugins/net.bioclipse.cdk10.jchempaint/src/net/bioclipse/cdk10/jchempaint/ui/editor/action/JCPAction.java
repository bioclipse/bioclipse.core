/*
 *  $RCSfile$
 *  $Author: tohel $
 *  $Date: 2006-11-28 17:19:36 +0100 (Tue, 28 Nov 2006) $
 *  $Revision: 2188 $
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
package net.bioclipse.cdk10.jchempaint.ui.editor.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPMultiPageEditorContributor;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.controller.CDKPopupMenu;

/**
 * Superclass of all JChemPaint GUI actions
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class JCPAction extends Action {

	public org.apache.log4j.Logger logger = Logger.getLogger(JCPAction.class);
	
    /**
     *  Description of the Field
     */
    public final static String actionSuffix = "Action";
    /**
     *  Description of the Field
     */
    public final static String imageSuffix = "Image";
    /**
     *  Description of the Field
     */
    public final static String labelSuffix = "Label";


    private Hashtable actions = null;
    private Hashtable popupActions = null;

    /**
     *  Description of the Field
     */
    protected String type;


    /**
     *  Is this popup action assiociated with a PopupMenu or not.
     */
    private boolean isPopupAction;
    private JCPMultiPageEditorContributor contributor;


    /**
     *  Constructor for the JCPAction object
     *
     *@param  jcpPanel       Description of the Parameter
     *@param  type           Description of the Parameter
     *@param  isPopupAction  Description of the Parameter
     */
    public JCPAction(String type, boolean isPopupAction)
    {
        super();
        if (this.actions == null)
        {
            this.actions = new Hashtable();
        }
        if (this.popupActions == null)
        {
            this.popupActions = new Hashtable();
        }
        this.type = "";
        this.isPopupAction = isPopupAction;
    }


    /**
     *  Constructor for the JCPAction object
     *
     *@param  jcpPanel       Description of the Parameter
     *@param  isPopupAction  Description of the Parameter
     */
    public JCPAction( boolean isPopupAction)
    {
        this("", isPopupAction);
    }


    /**
     *  Constructor for the JCPAction object
     *
     *@param  jcpPanel       Description of the Parameter
     *@param  isPopupAction  Description of the Parameter
     */
    public JCPAction(String text, int style)
    {
        super(text, style);
        if (this.actions == null)
        {
            this.actions = new Hashtable();
        }
        if (this.popupActions == null)
        {
            this.popupActions = new Hashtable();
        }
        this.type = "";
    }



    /**
     *  Constructor for the JCPAction object
     */
    public JCPAction()
    {
        this(false);
    }


    /**
     *  Sets the type attribute of the JCPAction object
     *
     *@param  type  The new type value
     */
    public void setType(String type)
    {
        this.type = type;
    }


    /**
     *  Is this action runnable?
     *
     *@return    The enabled value
     */
    public boolean isEnabled()
    {
        return true;
    }


    /**
     *  Gets the popupAction attribute of the JCPAction object
     *
     *@return    The popupAction value
     */
    public boolean isPopupAction()
    {
        return isPopupAction;
    }


    /**
     *  Sets the isPopupAction attribute of the JCPAction object
     *
     *@param  isPopupAction  The new isPopupAction value
     */
    public void setIsPopupAction(boolean isPopupAction)
    {
        this.isPopupAction = isPopupAction;
    }



    /**
     *  Gets the source attribute of the JCPAction object
     *
     *@param  event  Description of the Parameter
     *@return        The source value
     */
    public ChemObject getSource(ActionEvent event)
    {
        Object source = event.getSource();
//        logger.debug("event source: " +  source);
        if (source instanceof JMenuItem)
        {
            Container parent = ((JMenuItem) source).getComponent().getParent();
            // logger.debug("event source parent: " + parent);
            if (parent instanceof CDKPopupMenu)
            {
                return (ChemObject) ((CDKPopupMenu) parent).getSource();
            } else if (parent instanceof JPopupMenu)
            {
                // assume that the top menu is indeed a CDKPopupMenu
//                logger.debug("Submenu... need to recurse into CDKPopupMenu...");
                while (!(parent instanceof CDKPopupMenu))
                {
//                    logger.debug("  Parent instanceof " +  parent.getClass().getName());
                    if (parent instanceof JPopupMenu)
                    {
                        parent = ((JPopupMenu) parent).getInvoker().getParent();
                    } 
//                    else if (parent instanceof JChemPaintMenuBar)
//                    {
//                        logger.warn(" Source is MenuBar. MenuBar items don't know about the source");
//                        return null;
//                    } 
                    else
                    {
//                        logger.error(" Cannot get parent!");
                        return null;
                    }
                }
                return (ChemObject) ((CDKPopupMenu) parent).getSource();
            }
        }
        return null;
    }


    /**
     *  Gets the action attribute of the JCPAction class
     *
     *@param  jcpPanel       Description of the Parameter
     *@param  actionname     Description of the Parameter
     *@param  isPopupAction  Description of the Parameter
     *@return                The action value
     */
    public JCPAction getAction(String actionname, boolean isPopupAction, boolean asCheckBox)
    {
        // make sure logger and actions are instantiated
        JCPAction dummy = new JCPAction();

        // extract type
        String type = "";
        String classname = "";
        int index = actionname.indexOf("@");
        if (index >= 0)
        {
            classname = actionname.substring(0, index);
            // FIXME: it should actually properly check wether there are more chars
            // than just the "@".
            type = actionname.substring(index + 1);
        } else
        {
            classname = actionname;
        }
        classname = classname.replace("org.openscience.cdk.applications.", "net.bioclipse.cdk10.");
//        System.out.println("Action class: " + classname);
//        System.out.println("Action type:  " +  type);

        // now get actual JCPAction class
        if (!isPopupAction && actions.containsKey(actionname))
        {
//            System.out.println("Taking JCPAction from action cache for:" + actionname);
            return (JCPAction) actions.get(actionname);
        } else if (isPopupAction && popupActions.containsKey(actionname))
        {
//            System.out.println("Taking JCPAction from popup cache for:" + actionname);
            return (JCPAction) popupActions.get(actionname);
        } else
        {
//            System.out.println("Loading JCPAction class for:" + classname);
            Object o = null;
            try
            {
                // because 'this' is static, it cannot be used to get a classloader,
                // therefore use logger instead
                if(asCheckBox){
                    Class[] parameterTypes={String.class, Integer.TYPE};
                    Object[] initargs={"",asCheckBox? Action.AS_CHECK_BOX : Action.AS_PUSH_BUTTON};
                    o = dummy.getClass().getClassLoader().loadClass(classname).getConstructor(parameterTypes).newInstance(initargs);
                }else{
                    o = dummy.getClass().getClassLoader().loadClass(classname).newInstance();
                }
            } catch (Exception exc)
            {
//            	System.out.println("Could not find/instantiate class: " + classname);
//                logger.debug(exc);
//            	exc.printStackTrace();
                return dummy;
            }
            if (o instanceof JCPAction)
            {
                JCPAction a = (JCPAction) o;
                if (type.length() > 0)
                {
                    a.setType(type);
                }
                if (isPopupAction)
                {
                    popupActions.put(actionname, a);
                } else
                {
                    actions.put(actionname, a);
                }
                return a;
            } else
            {
//                System.out.println("Action is not a JCPAction!");
            }
        }
        return dummy;
    }


    /**
     *  Gets the action attribute of the JCPAction class
     *
     *@param  jcpPanel    Description of the Parameter
     *@param  actionname  Description of the Parameter
     *@return             The action value
     */
    public JCPAction getAction(String actionname)
    {
        return getAction(actionname, false, false);
    }


    public void setContributor(JCPMultiPageEditorContributor contributor) {
        this.contributor = contributor;
    }


    public JCPMultiPageEditorContributor getContributor() {
        return contributor;
    }


    public String getType() {
        return type;
    }


    public void run() {
    	System.out.println("button hit run()...");
    }

    public void run(ActionEvent e) {
    	System.out.println("button hit run(ActionEvent)...");
    }


}

