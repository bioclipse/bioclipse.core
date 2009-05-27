/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

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
import org.eclipse.core.runtime.jobs.Job;
import org.mozilla.javascript.EvaluatorException;

@SuppressWarnings("serial")
public class JsThread extends ScriptingThread {

    public static JsEnvironment js;
    private LinkedList<JsAction> actions= new LinkedList<JsAction>();
    
    private static final Logger logger = Logger.getLogger(JsEnvironment.class);
    private static boolean busy;

    public static Map<String, String[]> topLevelCommands
        = new HashMap<String, String[]>() {{
            // { "fn name" => [ "parameters", "fn body" ] }
            put("clear", new String[] { "",        "js.clear()"        } );
            put("print", new String[] { "message", "js.print(message)" } );
            put("say",   new String[] { "message", "js.say(message)"   } );
        }};

    private static void initJs() {
        js = new JsEnvironment();

        for (Map.Entry<String, String[]> e : topLevelCommands.entrySet())
            js.eval( "function " + e.getKey()
                     + "(" + e.getValue()[0] + ")"
                     + " { " + e.getValue()[1] + " }" );
    }

    public void run() {
        initJs();

        synchronized (actions) {
            while (true) {
                try {
                    while ( actions.isEmpty() )
                        actions.wait();
                } catch ( InterruptedException e ) {
                    break;
                }

                final JsAction nextAction = actions.removeFirst();
                final Object[] result = new Object[1];
                busy = true;
                final Boolean[] jsRunning = { true };
                final Boolean[] monitorIsSet = { false };
                final IProgressMonitor[] monitor 
                    = { new NullProgressMonitor() };
                
                Job job = new Job("JS-script") {
                    @Override
                    protected IStatus run( IProgressMonitor m ) {

                        m.beginTask( "Running Javascript", 
                                     IProgressMonitor.UNKNOWN );
                        
                        monitor[0] = m;
                        
                        synchronized ( monitorIsSet ) {
                            monitorIsSet[0] = true;
                            monitorIsSet.notifyAll();
                        }
                        synchronized ( jsRunning ) {
                            while (jsRunning[0]) {
                                try {
                                    jsRunning.wait(500);
                                    if (m.isCanceled()) {
                                        JsThread.this.stop();
                                        jsRunning[0] = false;
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
                    result[0] = js.eval( nextAction.getCommand() );
                    if (result[0] instanceof String) {
                        String s = (String)result[0];
                        if (s.startsWith( "Wrapped " ))
                            result[0] = "Something went wrong. The complete"
                                + " error message has been written to the"
                                + " logs.";
                    }
                }
                catch (EvaluatorException e) {
                    LogUtils.debugTrace(logger, e);
                    result[0] = e;
                }
                synchronized ( jsRunning ) {
                    jsRunning[0] = false;
                    jsRunning.notifyAll();
                }
                try {
                    job.join();
                } 
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                busy = false;
                
                nextAction.runPostCommandHook(result[0]);
            }
        }
    }
    
    public synchronized void enqueue(JsAction action) {
        synchronized (actions) {
            actions.addLast( action );
            actions.notifyAll();
        }
    }
    
    public static synchronized boolean isBusy() {
        return busy;
    }
    
    public void enqueue(String command) {
        enqueue( new JsAction( command,
                               new Hook() { public void run(Object s) {} } ) );
    }

    public String toJsString( Object o ) {
        return js.toJsString(o);
    }
}
