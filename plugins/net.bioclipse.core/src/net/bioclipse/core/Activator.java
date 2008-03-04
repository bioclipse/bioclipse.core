package net.bioclipse.core;

import net.bioclipse.recording.IHistory;
import net.bioclipse.recording.IRecordingAdvice;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.core";

	// The shared instance
	private static Activator plugin;
	

	private ServiceTracker historyTracker;
	private ServiceTracker recordingAdviceTracker;
	
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		historyTracker = new ServiceTracker( context, 
                IHistory.class.getName(), 
                null );
		historyTracker.open();
		recordingAdviceTracker = new ServiceTracker( context, 
                IRecordingAdvice.class.getName(), 
                null );
		recordingAdviceTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	
	
	public IHistory getHistory() {
		IHistory history = null;
		try {
			history = (IHistory) historyTracker.waitForService(1000*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(history == null) {
			throw new IllegalStateException("Could not get the history");
		}
		return history;
	}
	
	public IRecordingAdvice getRecordingAdvice() {
		IRecordingAdvice recordingAdvice = null;
		try {
			recordingAdvice = (IRecordingAdvice) recordingAdviceTracker.waitForService(1000*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(recordingAdvice == null) {
			throw new IllegalStateException("Could not get the recordingAdvice");
		}
		return recordingAdvice;
	}
}
