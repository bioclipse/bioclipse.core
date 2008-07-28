/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *     
 ******************************************************************************/
package net.bioclipse.scripting.ui.business;

import net.bioclipse.scripting.ui.views.JsConsoleView;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

/**
 * Contains general methods for interacting with the Javascript console.
 * 
 * @author masak
 *
 */
public class JsConsoleManager implements IJsConsoleManager {

    PlatformUI platformUI;
    
    public void clear() {
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                JsConsoleView jsConsoleView = null;
                
                IViewReference[] viewRefs = PlatformUI.getWorkbench()
                          .getActiveWorkbenchWindow()
                          .getActivePage()
                          .getViewReferences();
                for ( IViewReference ref : viewRefs )
                    if ( ref.getView(true) instanceof JsConsoleView )
                        jsConsoleView = (JsConsoleView) (ref.getView(true));
                if ( jsConsoleView != null )
                    jsConsoleView.clearConsole();
                else
                    throw new IllegalStateException("Console not reacheable");
            }
        } );
        
    }

    public String getNamespace() {
        return "js";
    }
}
