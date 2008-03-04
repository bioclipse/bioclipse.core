package net.bioclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public class ConsoleEchoer {
	
	private List<EchoListener> listeners
		= new ArrayList<EchoListener>();
	
	public void addListener(EchoListener l) {
		listeners.add(l);
	}
	
	public void removeListener(EchoListener l) {
		listeners.remove(l);
	}
	
	public void echo(String message) {
		final EchoEvent e = new EchoEvent(message);
		
		Display.getDefault().asyncExec( new Runnable() {
		    public void run() {
		    	for (EchoListener l : listeners)
					l.receiveLogEvent(e);
		    }
		} );
	}
}
