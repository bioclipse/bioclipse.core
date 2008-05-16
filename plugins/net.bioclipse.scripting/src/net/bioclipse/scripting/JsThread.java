package net.bioclipse.scripting;

import java.util.LinkedList;

import org.apache.log4j.Logger;

public class JsThread extends Thread {
    public static JsEnvironment js;
    
    private LinkedList<JsAction> actions;
    
    private Logger logger = Logger.getLogger( this.getClass() );
    
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
                
                String result = js.eval( nextAction.getCommand() );
                
                nextAction.runPostCommandHook(result);
            }
        }
    }
    
    public synchronized void enqueue(JsAction action) {
        int tries = 10;
        while (actions == null)  // BIG UGLY HACK!
            try {
                if (tries-- <= 0) {
                    
                    logger.error( "Timed out waiting for scripting to load" );
                    break;
                }
                
                Thread.sleep( 500 );
            } catch ( InterruptedException e ) {
            }
        
        synchronized (actions) {
            actions.addLast( action );
            actions.notifyAll();
        }
    }
    
    public void enqueue(String command) {
        enqueue( new JsAction( command,
                               new Hook() { public void run(String s) {} } ) );
    }
}
