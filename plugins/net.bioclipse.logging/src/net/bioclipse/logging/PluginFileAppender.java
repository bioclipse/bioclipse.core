package net.bioclipse.logging;

import java.io.IOException;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.Layout;
import org.eclipse.core.runtime.IPath;

/**
 * PluginFileAppender
 * This class is a custom Log4J appender that sends Log4J events to 
 * the Eclipse plug-in state location. It extends the RollingFileAppender class.
 * @author Manoel Marques
 */
public class PluginFileAppender extends RollingFileAppender {

	private IPath stateLocation;
	private boolean activateOptionsPending; 
	private boolean translatePath = true;
	
	/**
	 * Creates a new PluginFileAppender.
	 */
	public PluginFileAppender() {
		super();
	}
	
	/**
	 * Creates a new PluginFileAppender.
	 * @param layout layout instance.
	 * @param stateLocation IPath containing the plug-in state location 
	 */	
	public PluginFileAppender(Layout layout,IPath stateLocation) {
		super();
		setLayout(layout);
		setStateLocation(stateLocation);	
	}
	
	/**
	 * Creates a new PluginFileAppender.
	 * @param layout layout instance.
	 * @param stateLocation IPath containing the plug-in state location 
	 * @param file file name
	 * @param append true if file is to be appended
	 */	
	public PluginFileAppender(Layout layout,IPath stateLocation, String file, boolean append)
			throws IOException {
		super();
		setLayout(layout);
		setStateLocation(stateLocation);
		setFile(file);
		setAppend(append);
		activateOptions();
	}

	/**
	 * Creates a new PluginFileAppender.
	 * @param layout layout instance.
	 * @param stateLocation IPath containing the plug-in state location 
	 * @param file file name
	 */	
	public PluginFileAppender(Layout layout,IPath stateLocation, String file) throws IOException {
		super();
		setLayout(layout);
		setStateLocation(stateLocation);
		setFile(file);
		activateOptions();
	}
	
	/**
	 * Sets the state location. If activateOptions call is pending, translate the file name
	 * and call activateOptions 
	 * @param stateLocation IPath containing the plug-in state location 
	 */	
	void setStateLocation(IPath stateLocation) {
		this.stateLocation = stateLocation; 
		if (this.stateLocation != null && this.activateOptionsPending) {
			this.activateOptionsPending = false;
			setFile(getFile());
			activateOptions();
		}
	}
	
	/**
	 * Sets the file name.Translate it before setting. 
	 * @param file file name
	 */	
	public void setFile(String file) {
		super.setFile(getTranslatedFileName(file));
	}
	
	/**
	 * Set file options and opens it, leaving ready to write.
	 * @param file file name
	 * @param append true if file is to be appended
	 * @param bufferedIO true if file is to buffered
	 * @param bufferSize buffer size
	 * @throws IOException - IO Error happend or the state location was not set   
	 */	
	public void setFile(String fileName,boolean append,boolean bufferedIO,int bufferSize) throws IOException {
		if (this.stateLocation == null) 
			throw new IOException("Missing Plugin State Location.");			
		
		fileName = (this.translatePath) ?  getTranslatedFileName(fileName) : fileName;		
		super.setFile(fileName,append,bufferedIO,bufferSize);
	}
	
	/**
	 * Finishes instance initialization. If state location was not set, set activate as 
	 * pending and does nothing.
	 */	
	public void activateOptions() {
		if (this.stateLocation == null) { 
			this.activateOptionsPending = true;
			return;
		}	
		
		// base class will call setFile, don't translate the name
		// because it was already translated
		this.translatePath = false;
		super.activateOptions(); 
		this.translatePath = true;
	}
	
	/**
	 * Any path part of a file is removed and the state location is added to the name
	 * to form a new path. If there is not state location, returns the name unmodified.
	 * @param file file name
	 * @return translated file name 
	 */
	private String getTranslatedFileName(String file) {
	
		if (this.stateLocation == null || file == null)
			return file;
		
		file = file.trim();
		if (file.length() == 0)
			return file;
		
		int index = file.lastIndexOf('/');
		if (index == -1)
			index = file.lastIndexOf('\\');
		
		if (index != -1) 
			file = file.substring(index + 1);
					
		IPath newPath = this.stateLocation.append(file); 
		return newPath.toString();
	}
}