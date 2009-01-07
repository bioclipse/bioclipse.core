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

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.ui.views.JsConsoleView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Contains general methods for interacting with the Javascript console.
 * 
 * @author masak
 *
 */
public class JsConsoleManager implements IJsConsoleManager {

    private JsConsoleView getJsConsoleView() {
        try {
            return (JsConsoleView)
                PlatformUI.getWorkbench()
                          .getActiveWorkbenchWindow()
                          .getActivePage()
                          .showView( "net.bioclipse.scripting.ui.views."
                                     + "JsConsoleView" );
        } catch ( PartInitException e ) {
            throw new RuntimeException(
                "The JavaScript view could not be opened"
            );
        }
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

    public void say(final String message) {
        print(message + "\n");
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

    public void executeFile( IFile file ) {
        executeFile(file, new NullProgressMonitor());
    }

    public void executeFile( String filePath ) {
        executeFile(
          ResourcePathTransformer.getInstance().transform( filePath )
        );
    }

    public void executeFile( IFile file, final IProgressMonitor monitor ) {
        String contents;
        
        getJsConsoleView().deactivatePrompt();

        monitor.beginTask( "read file", 1 );
        try {
            java.util.Scanner sc = new java.util.Scanner(file.getContents());
            StringBuffer sb = new StringBuffer();
            while ( sc.hasNextLine() ) {
                sb.append( sc.nextLine() );
            }
            contents = sb.toString();
        } 
        catch ( CoreException ce ) {
            throw new RuntimeException("Could not run the script "
                                       + file.getName(), ce);
        }
        monitor.worked( 1 );
        Activator.getDefault().JS_THREAD.enqueue(
            new JsAction(contents, new Hook() {
                public void run( String result ) {
                    monitor.done();
                    if ( !"undefined".equals( result ) ) {
                        message(result);
                    }
                    Display.getDefault().asyncExec( new Runnable() {
                        public void run() {
                            getJsConsoleView().activatePrompt();
                        }
                    } );
                }

                private void message(final String text) {

                    Display.getDefault().asyncExec( new Runnable() {
                        public void run() {
                            MessageDialog.openInformation( 
                                 PlatformUI.getWorkbench()
                                           .getActiveWorkbenchWindow()
                                           .getShell(),
                                 "Script finished",
                                 text ); 
                        }
                    } );
                }
            })
        );
    }
}
