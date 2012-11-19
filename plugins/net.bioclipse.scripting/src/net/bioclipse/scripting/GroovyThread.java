/* *****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.managers.MonitorContainer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("serial")
public class GroovyThread extends ScriptingThread {

    private final static Object semaphore = new Object();

    private static volatile boolean firstTime = true;

    public static GroovyEnvironment groovy;
    private LinkedList<ScriptAction> actions= new LinkedList<ScriptAction>();

	private static Writer outputWriter;

    private static final Logger logger = Logger.getLogger(GroovyEnvironment.class);
    private static boolean busy;

    public static Map<String, String[]> topLevelCommands
        = new HashMap<String, String[]>() {{
            // { "fn name" => [ "parameters", "fn body" ] }
            // put("clear", new String[] { "",        "js.clear()"        } );
            // put("print", new String[] { "message", "js.print(message)" } );
            // put("say",   new String[] { "message", "js.say(message)"   } );
        }};

    private static void initJs() {
        busy = true;
        groovy = new GroovyEnvironment();
        if (outputWriter != null) {
        	// if we already have an output writer, set it
        	groovy.setOutputWriter(outputWriter);
        }

//        for (Map.Entry<String, String[]> e : topLevelCommands.entrySet())
//            js.eval( "function " + e.getKey()
//                     + "(" + e.getValue()[0] + ")"
//                     + " { " + e.getValue()[1] + " }" );
    }

    /**
     * Overwrites the Writer of the {@link ScriptContext}.
     * 
     * @param outputWriter write to which script context output is written
     */
    public void setOutputWriter(Writer outputWriter) {
    	GroovyThread.outputWriter = outputWriter;
    	if (groovy != null) {
    		// if we already have a groovy environment, set the output writer
    		groovy.setOutputWriter(outputWriter);
    	}
    }
    
    public void run() {
        initJs();

        synchronized (actions) {
            busy = false;
            while (true) {
                try {
                    while ( actions.isEmpty() )
                        actions.wait();
                } catch ( InterruptedException e ) {
                    break;
                }

                final ScriptAction nextAction = actions.removeFirst();
                final Object[] result = new Object[1];
                busy = true;
                final Boolean[] groovyRunning = { true };
                final Boolean[] monitorIsSet = { false };
                final IProgressMonitor[] monitor
                    = { new NullProgressMonitor() };

                Job job = new Job("Groovy-script") {
                    @SuppressWarnings("deprecation")
                    @Override
                    protected IStatus run( IProgressMonitor pm ) {

                        pm.beginTask( "Running Groovy",
                                     IProgressMonitor.UNKNOWN );

                        IProgressMonitor m = new SubProgressMonitor(pm, 1);

                        monitor[0] = m;

                        nextAction.runPreCommandHook();
                        synchronized ( monitorIsSet ) {
                            monitorIsSet[0] = true;
                            monitorIsSet.notifyAll();
                        }
                        synchronized ( groovyRunning ) {
                            while (groovyRunning[0]) {
                                try {
                                    groovyRunning.wait(500);
                                    if ( m.isCanceled() &&
                                         groovyRunning[0] ) {
                                        groovyRunning.wait(5000);
                                        if (!groovyRunning[0]) {
                                            break;
                                        }
                                        // This is the only way we've found out
                                        // that actually stops the thread in
                                        // its tracks. It's deprecated (and
                                        // with good reason, at that), and
                                        // there doesn't seem to be any better
                                        // non-deprecated way to do it.
                                        GroovyThread.this.stop();
                                        groovyRunning[0] = false;
                                        if (firstTime)
                                            popUpWarning();
                                        return Status.CANCEL_STATUS;
                                    }
                                }
                                catch ( InterruptedException e ) {
                                    break;
                                }
                            }
                        }
                        return Status.OK_STATUS;
                    }
                };
                job.setUser( true );
                job.schedule();
                synchronized ( monitorIsSet ) {
                    while ( !monitorIsSet[0] ) {
                        try {
                            monitorIsSet.wait();
                        }
                        catch ( InterruptedException e ) {
                            break;
                        }
                    }
                }
                try {
                    MonitorContainer.getInstance().addMonitor(monitor[0]);
                    result[0] = groovy.eval( nextAction.getCommand() );
                    if (result[0] instanceof String) {
                        String s = (String)result[0];
                        if (s.startsWith( "Wrapped " ))
                            result[0] = "Something went wrong. The complete"
                                + " error message has been written to the"
                                + " logs.";
                    }
                }
                catch (Exception e) {
                    LogUtils.errorTrace(logger, e);
                    result[0] = e;
                }
                synchronized ( groovyRunning ) {
                    groovyRunning[0] = false;
                    groovyRunning.notifyAll();
                }
                try {
                    job.join();
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                busy = false;
                synchronized ( semaphore ) {
                    semaphore.notifyAll();
                }
                nextAction.runPostCommandHook(result[0]);
            }
        }
    }

    public synchronized void enqueue(ScriptAction action) {
        synchronized (actions) {
            actions.addLast( action );
            actions.notifyAll();
        }
    }

    public static synchronized boolean isBusy() {
        return busy;
    }

    public void enqueue(String command) {
        enqueue( new ScriptAction( command,
                               new Hook() { public void run(Object s) {} } ) );
    }

    private void popUpWarning() {
        firstTime = false;
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                MessageDialog.openWarning(
                  PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getShell(),
                  "Restart recommended after cancelling Groovy script",
                  "The cancelling of the running Groovy script may have " +
                    "left your data in an inconsistent state, depending upon " +
                    "what the Groovy script execution was working on. " +
                    "You are recommended to restart Bioclipse." );
            }
        } );
    }

    public static void waitUntilNotBusy() {
        while (busy) {
            try {
                synchronized ( semaphore ) {
                    semaphore.wait(30*1000);
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
