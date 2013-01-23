/* *****************************************************************************
 * Copyright (c) 2008-2009 The Bioclipse Project and others.
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

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.scripting.Activator;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.ScriptAction;
import net.bioclipse.scripting.JsThread;
import net.bioclipse.scripting.ui.views.JsConsoleView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Contains general methods for interacting with the JavaScript console.
 * 
 * @author masak
 *
 */
public class JsConsoleManager implements IBioclipseManager {

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

    public void delay(int seconds) {
        try {
            Thread.sleep( seconds*1000 );
        } catch ( InterruptedException e ) {
        }
    }

    public void say(final String message) {
        print(message + JsConsoleView.NEWLINE);
    }

    public String getManagerName() {
        return "js";
    }

    public String eval( String command ) {
        final String[] evalResult = new String[1];
        final JsThread jsThread = Activator.getDefault().JS_THREAD;
        jsThread.enqueue(
            new ScriptAction(command, new Hook() {
                public void run( Object result ) {
                    evalResult[0] = result.toString();
                }
            })
        );
        return evalResult[0];
    }
    
    public void execute( final IFile file ) {
        Job job = new Job("JavaScript execution") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                String contents;

                monitor.beginTask( "read file", 1 );
                try {
                    java.util.Scanner sc = new java.util.Scanner(file.getContents());
                    StringBuffer sb = new StringBuffer();
                    while ( sc.hasNextLine() ) {
                        sb.append( sc.nextLine() );
                        sb.append( "\r\n" ); // needed here because that seems
                                             // to be what the js env expects
                    }
                    contents = sb.toString();
                }
                catch ( CoreException ce ) {
                    throw new RuntimeException("Could not run the script "
                                               + file.getName(), ce);
                }
                monitor.worked( 1 );
                final JsThread jsThread = Activator.getDefault().JS_THREAD;
                jsThread.enqueue(
                    new ScriptAction(contents, new Hook() {
                        public void run( Object result ) {
                            monitor.done();
                            /* If the result is null and the user make an tab-
                             * completion, Bioclipse will hang. 
                             * TODO Find a better solution */
                            if (result != null) {
                                if ( !"org.mozilla.javascript.Undefined"
                                        .equals(result.getClass().getName() ) ) {
                                    message(result.toString());
                                }
                            }
                            else 
                                message("Script finished without any problems.");
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
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    
    }

    @Deprecated
    public void executeFile( final IFile file ) {
        execute(file);
    }

    public void printError( Throwable t ) {
        print( getJsConsoleView().getErrorMessage( t ) );
    }

    public void execute( List<?> files ) throws BioclipseException {
        int i = 0;
        for ( Object o : files) {
            if ( o instanceof IFile ) {
                execute( (IFile)o );
            }
            else if ( o instanceof String ) {
                execute( ResourcePathTransformer.getInstance()
                                                .transform( (String)o ) );
            }
            else {
                throw new BioclipseException(
                    "List of files given to the js.execute method contained " +
                    "an element at index " + i +  " which could not be " +
                    "identified as a file. The element was: " + o.toString() );
            }
            i++;
        }
    }
}
