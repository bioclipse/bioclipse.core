/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

import java.util.LinkedList;

public class JsThread extends Thread {

    public static JsEnvironment js;
    private LinkedList<JsAction> actions;
    private static boolean busy;
    
    public void run() {
        js = new JsEnvironment();
        actions = new LinkedList<JsAction>();

        synchronized (actions) {
            while (true) {
                try {
                    while ( actions.isEmpty() )
                        actions.wait();
                } catch ( InterruptedException e ) {
                    break;
                }

                JsAction nextAction = actions.removeFirst();
                
                busy = true;
                String result = js.eval( nextAction.getCommand() );
                busy = false;
                
                nextAction.runPostCommandHook(result);
            }
        }
    }
    
    public synchronized void enqueue(JsAction action) {
        while (actions == null) { // BIG UGLY HACK!
            try {
                Thread.sleep( 500 );
            } catch ( InterruptedException e ) {
            }
        }
        
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
                               new Hook() { public void run(String s) {} } ) );
    }
}
