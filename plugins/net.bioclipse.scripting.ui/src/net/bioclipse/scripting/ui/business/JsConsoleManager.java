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
import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;

/**
 * Contains general methods for interacting with the Javascript console.
 * 
 * @author masak
 *
 */
public class JsConsoleManager implements IJsConsoleManager {

    private JsConsoleView getJsConsoleView() {
        IViewReference[] viewRefs = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow()
            .getActivePage()
            .getViewReferences();
        for ( IViewReference ref : viewRefs )
            if ( ref.getView(true) instanceof JsConsoleView )
                return (JsConsoleView) (ref.getView(true));
        
        throw new IllegalStateException("Console not reachable.");
    }
    
    public void clear() {
        Display.getDefault().asyncExec( new Runnable() {
            public void run() { getJsConsoleView().clearConsole(); }
        } );
    }

    public void print(final String message) {
        Display.getDefault().asyncExec( new Runnable() {
            public void run() { getJsConsoleView().printMessage( message ); }
        } );
    }

    public String getNamespace() {
        return "js";
    }

    public String eval( String command ) {
        final String[] evalResult = new String[1];
        Activator.getDefault().JS_THREAD.enqueue(
            new JsAction(command, new Hook() {
                public void run( String result ) {
                    evalResult[0] = result;
                }
            })
        );
        return evalResult[0];
    }
}
