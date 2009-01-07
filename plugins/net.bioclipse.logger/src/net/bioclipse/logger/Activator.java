/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Richard Klancer - initial implementation of Activator
 *     rpk@pobox.com 3/27/2008
 *     
 ******************************************************************************/
package net.bioclipse.logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;

// DO NOT IMPORT net.bioclipse packages here. Their bundle activators will
// run, probably calling getLogger before its configuration properties are
// properly defined by this plugin's static initializer.

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for the net.bioclipse.logger plugin.
 * 
 * This plugin exists to encapsulate the logger packages and contain their
 * configuration files. It ensures that various "bioclipse.*" system properties
 * are set so that log4j configuration files can use them for property 
 * subsitutions, BEFORE any log4j classes are used. (Using most log4j classes
 * will cause log4j to attempt to configure itself, with bad results if the 
 * aforementioned bioclipse.* properties have not yet been set.)
 * 
 * Also contains the code that tells log4j which configuration file to use, and
 * the property doAppLogging which can be used at development time to disable
 * all logging output.
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.logger";

    // The shared instance
    private static Activator plugin;
    
    // whether to do any logging at all. 
    public static final boolean doAppLogging = true;
    
    // the LogListener that repeats platform log messages to log4j log
    private static final PlatformLogRepeater repeater = new PlatformLogRepeater();
    
    // whether to print debug messages from this plugin to System.out before 
    // Logger is available
    private static final boolean doLogConfigLogging = true;

    private static final String MISCONFIG_WARNING = 
        "Logging may not be configured properly.";
    
    private static final String NO_LOG_CONFIG_FILE_MSG = 
        "Could not pass logger configuration file location to logger.";
    
    private static final String BAD_PATH_MSG = 
        "Bioclipse attempted a to set a pathname property ending with "
        + File.separator;
    
    // bioclipse-defined paths we want to make available to log4j config file
    // as system properties
    
    public enum BcPath {
        
        USERHOME 
            ("bioclipse.userhome",
             pathnameFromProperty("user.home")),
                
        WORKSPACE
            ("bioclipse.workspace",
             pathnameFromProperty("osgi.instance.area")),
        
        INSTALL_AREA 
            ("bioclipse.installArea",
             pathnameFromProperty("osgi.install.area")),
            
        DEFAULT_LOG_DIR 
            ("bioclipse.defaultLogDir",
             "macosx".equals(System.getProperty("osgi.os"))
                ? pathnameFromProperty("user.home") + "/Library/Logs/Bioclipse"
                : pathnameFromProperty("user.home")); 

        public final String key;
        public final String path;
        private BcPath(String key, String path) { this.key = key; this.path = path; }
    };

    // location of the config file we want to pass to log4j
    
    // (retain configFileUrl as amethod, not a field, so that if there's an  
    // error it happens in the try-catch block in the static initializer below)
    
    private static URL configFileUrl() {
        final String CONFIG_FILE_NAME = "log4j.properties";
        final String BUNDLE_TO_SEARCH = PLUGIN_ID;   // i.e., this plugin
        
        return FileLocator.find(Platform.getBundle(BUNDLE_TO_SEARCH),
                new Path(CONFIG_FILE_NAME), null);
    }

    // log4j configures itself as soon as it getLogger() is called the
    // first time, so BEFORE the first call to getLogger() we need to set  
    // the properties that log4j's configurator will read
    
    private static final Logger logger;    // initialize below
    static {
        // set the bioclipse.* properties we want log4j to see
        for (BcPath p : BcPath.values())
            setPathProperty(p.key, p.path);
        
        // tell log4j the location of the configuration file we want it to use
        try {
            System.setProperty("log4j.configuration", 
                    normalizedUrlStringFrom(configFileUrl()));
        } catch (Exception e) {
            warn(MISCONFIG_WARNING + "\n" + NO_LOG_CONFIG_FILE_MSG);
            warn("Cause: Caught exception: " + e);
            debug(traceStringOf(e));
        }
 
        debug("log4j.configuration = " + System.getProperty("log4j.configuration"));
        debug(BcPath.DEFAULT_LOG_DIR.key + " = " 
                + System.getProperty(BcPath.DEFAULT_LOG_DIR.key));
       
        // NOW it's safe to get start up log4j (by calling getLogger())
        logger = Logger.getLogger(Activator.class);
    }

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        if (doAppLogging)
            showLogFileNameOrWarn();
        else
            disableLogging();
        
        // listen for platform log events and copy to log4j log.
        Platform.addLogListener(repeater);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        
        Platform.removeLogListener(repeater);
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
    
    private static void showLogFileNameOrWarn() {
        String logFile = getActualLogFileName();
        if (logFile == null)
            warn(MISCONFIG_WARNING);
        else
            logger.info("Probable log file location: " + logFile);
    }
    
    private static void disableLogging() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();      // prevents no-appender warning
        Logger.getRootLogger().setLevel(Level.OFF);
    }


    /* getActualLogFileName():
     * 
     * Queries Log4j for the "file" attribute of the first file appender
     * that it can find attached to the root logger 
     *  
     * Because the value of this attribute is naively passed by FileAppender
     * to java.io.FileOutputStream(String, int), we attempt to determine 
     * the absolute path to which FileOutputStream interprets this attribute.
     * 
     * Returns the (anticipated) absolute path of the File attribute of the 
     * file appender, or Null if there is no file appender or if its
     * File attribute is not set.
     */
    
    private static String getActualLogFileName() {
    
        // find first file appender
        @SuppressWarnings("unchecked")
        Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
        
        FileAppender fa = null;
        while (appenders.hasMoreElements()) {
            Appender a = appenders.nextElement();
            if (a instanceof FileAppender) {
                fa = (FileAppender) a;
                break;
            }
        }

        if (fa == null)
            return null;
    
        String requestedLogFileName = fa.getFile();
        if (requestedLogFileName == null)
            return null;
        
        // rather than mimic exactly what FileAppender does with its File
        // attribute (passing it to FileOutputStream, therefore actually
        // opening a file), create a File object which ought to interpret
        // its constructor's argument in the same way that FileOutputStream does.
        
        return (new File(requestedLogFileName)).getAbsolutePath();
    }

    /* pathnameFromProperty(propName):
     * 
     * Attempts to interpret value of system property 'propName' as a URI
     * pointing to a file in the default filesystem. If this fails, attempts
     * to interpret the value as a pathname referring to a file in the 
     * filesystem.
     * 
     * Returns the pathname if one of those interpretations works, or else
     * null.
     */
    private static String pathnameFromProperty(String propName) {
        
        File pathAsFileObj = null;
        
        String pathPropertyVal = System.getProperty(propName);
        if (pathPropertyVal == null)
            return null;
        
       
        
        try {
            // try to interpret as URI (e.g., file: URI)
            
            URI pathAsURIObj = new URI(pathPropertyVal.replaceAll("\\s", "%20"));
            pathAsFileObj = new File(pathAsURIObj);
        } catch (Exception e) {
            // ok, try to interpret as a plain pathnames        	
            pathAsFileObj = new File(pathPropertyVal);
        }
        
        assert pathAsFileObj != null : "pathAsFileObj should not be null here.";
        
        try {
            String pathname = pathAsFileObj.getCanonicalPath();
            return pathname;
        } catch (IOException e) {
            // who knows what it was? our contract is to return null if it's nonsense.
            return null;
        }
    }
    
    /* normalizedUrlStringFor(URL):
     * 
     * Framework functions may return URLs that use funky schemes
     * like "bundleentry:" that non-OSGI code won't understand.
     * Use utility function to convert to a standard file: url referring to
     * an actual file in the filesystem, and return in string form */
    
    private static String normalizedUrlStringFrom(URL url) throws IOException {
        URL fileUrl = FileLocator.toFileURL(url);
        return fileUrl.toExternalForm();
    }

    private static void setPathProperty(String key, String path) {
                
        if (path != null && path.endsWith(File.separator)) {            
            // this is not supposed to happen.
            String msg = BAD_PATH_MSG + "\n'" + key + "' set to '" + path + "'.";
            assert false: msg;
            warn(MISCONFIG_WARNING + "\n" + msg);
        }

        if (key==null || path==null){
            System.out.println("Omitting property: " + key + " with value: " + path);
            return;
        }
        
   
        System.setProperty(key, path);
    }
    

   // remember we can't load net.bioclipse.core classes 

    /** Returns a printable stack trace from a Throwable. Intended to be used
     * in net.bioclipse.logger, which can't import net.bioclipse.core.util
     * 
     * @param t
     *            the Throwable
     * @return String with stack trace information from t
     */
    protected static String traceStringOf(Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter traceWriter = new PrintWriter(out);
        t.printStackTrace(traceWriter);
        traceWriter.flush();
        return out.toString();
    }

    // provided in order to print debug messages before logger classes
    // can be used.   
    private static void debug(String message) {
        if (doAppLogging && doLogConfigLogging) {
            System.out.println(message);
        }
    }
    
    // provided in order to print warning messages before logger classes
    // can be used.   
    private static void warn(String message) {
        if (doAppLogging) {
            System.err.println("WARNING: " + message);
        }
    }
}
