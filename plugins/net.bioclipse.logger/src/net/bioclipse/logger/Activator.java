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
 * the property useLogging which can be used at development time to disable
 * all logging output.
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.logger";

    // The shared instance
    private static Activator plugin;

    // whether to print debug messages from this plugin to System.out before 
    // Logger is available
    private static final boolean useLoggerLogging = true;
    
    // whether to do any logging at all. 
    private static final boolean useLogging = true;

    private static final String NO_LOG_FILE_WARNING_MSG = 
        "Warning: Bioclipse log file may not be configured.";

    private static final String NO_LOG_CONFIG_FILE_MSG = 
        "Warning: Could not pass logger configuration file location to logger. "
        + "Logging may not be configured properly.";

    
    // here define any bioclipse system properties we want log4j config file to see
    private enum BCPathProp {
        
        USERHOME
            { { _key = "bioclipse.userhome";
                _path = getPathnameOrNullFromProperty("user.home"); } },
        
        WORKSPACE
            { { _key = "bioclipse.workspace";
                _path = getPathnameOrNullFromProperty("osgi.instance.area"); } },
            
        INSTALL_AREA
            { { _key = "bioclipse.installArea";
                _path = getPathnameOrNullFromProperty("osgi.install.area"); } },
       
        DEFAULT_LOG_DIR
            { { _key = "bioclipse.defaultLogDir";
                _path = "macosx".equals(System.getProperty("osgi.os")) 
                        ?
                        getPathnameOrNullFromProperty("user.home") + "/Library/Logs/Bioclipse"
                        :
                        getPathnameOrNullFromProperty("osgi.instance.area"); } };

        protected String _path;
        protected String _key;
        public final String key;
        public final String path;
        private BCPathProp() { this.key = _key; this.path = _path; }
        
    };

    // here define the location of the config file you want to pass to log4j
    // (define configFileUrl as method, not field, so that if there's an error 
    // it happens in the try-catch block in the static initializer below)
    private static URL configFileUrl() {
        final String CONFIG_FILE_NAME = "log4j.properties";
        final String BUNDLE_TO_SEARCH = PLUGIN_ID; // i.e., this plugin
        
        return  FileLocator.find(Platform.getBundle(BUNDLE_TO_SEARCH),
                new Path(CONFIG_FILE_NAME), null);
    }

    // log4j configures itself as soon as it getLogger() is called the
    // first time, so we need to make sure to to set some properties it will
    // need BEFORE we call getLogger() for the first time.
    
    private static final Logger logger;    // initialize this below
    static {
        // set the bioclipse.* properties we want log4j to see
        for (BCPathProp p : BCPathProp.values())
            setPathProperty(p.key, p.path);
        
        // tell log4j the location of the configuration file we want it to use
        try {
            System.setProperty("log4j.configuration", 
                    normalizedUrlStringFrom(configFileUrl()));
        } catch (Exception e) {
            warn(NO_LOG_CONFIG_FILE_MSG);
            warn("Cause: Caught exception: " + e);
            debug(traceStringFrom(e));
        }
 
        debug("log4j.configuration = " + System.getProperty("log4j.configuration"));
        debug(BCPathProp.DEFAULT_LOG_DIR.key + " = " 
                + System.getProperty(BCPathProp.DEFAULT_LOG_DIR.key));
       
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
        
        if (useLogging)
            showLogFileNameOrWarn();
        else
            disableLogging();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
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

    private void disableLogging() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();      // prevents no-appender warning
        Logger.getRootLogger().setLevel(Level.OFF);
    }

    private void showLogFileNameOrWarn() {
        String logFile = getActualLogFileName();
        if (logFile == null)
            warn(NO_LOG_FILE_WARNING_MSG);     
        else
            logger.info("Probable log file location: " + logFile);
    }

    /* Queries Log4j for the "file" attribute of the first file appender
     * attached to the root logger that it can find.
     *  
     * Because the value of this attribute is naively passed by FileAppender
     * to java.io.FileOutputStream(String, int), we attempt to determine 
     * the absolute path to which FileOutputStream interprets this attribute.
     * 
     * Returns the (anticipated) absolute path of the File attribute of the 
     * file appender, or Null if there is no file appender or if its
     * File attribute is not set.
     */
    
    private String getActualLogFileName() {
    
        // first find file appender
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

    private static String getPathnameOrNullFromProperty(String propName) {
        
        File logDirAsFile = null;
        
        String logDirName = System.getProperty(propName);
        if (logDirName == null)
            return null;

        try {
            URI logDirAsURI = new URI(logDirName);
            logDirAsFile = new File(logDirAsURI);
        } catch (Exception e) {
            // might be a pathname; File constructor will take anything.
            logDirAsFile = new File(logDirName);
        }
        
        assert logDirAsFile != null : "logDirAsFile should not be null here.";
        
        try {
            String pathname = logDirAsFile.getCanonicalPath();
            return pathname;
        } catch (IOException e) {
            // who knows what it was? our contract is to return null if it's nonsense.
            return null;
        }
    }
    
    // normalizedUrlStringFor(URL):
    // Framework functions may return URLs that use funky schemes
    // like "bundleentry:" that non-OSGI code won't understand.
    // Use utilituy function to convert to a standard file: url referring to
    // an actual file in the filesystem, and return in string form
    
    private static String normalizedUrlStringFrom(URL url) throws IOException {
        URL fileUrl = FileLocator.toFileURL(url);
        return fileUrl.toExternalForm();
    }

    private static void setPathProperty(String propName, String pathName) {
        
        if (pathName != null &&
                pathName.endsWith(File.separator)) {
            String suffix = "attempted a to set a pathname property ending with " + 
                File.separator + "; pathname = " + pathName;
            assert false: "Should not have " + suffix;  // for dev time only, remember
            warn("Warning: logging may be misconfigured.");
            // try to warn using logger also, but remember it may be misconfigured
            if (logger != null) logger.warn(suffix);
        }
    
        System.setProperty(propName, pathName);
    }

    private static String traceStringFrom(Throwable t) {
        PrintWriter trace = new PrintWriter(new ByteArrayOutputStream());
        t.printStackTrace(trace);
        return trace.toString();
    }

    // provided in order to print debug messages before logger classes
    // can be used.   
    private static void debug(String message) {
        if (useLogging & useLoggerLogging) {
            System.out.println(message);
        }
    }
    
    // provided in order to print warning messages before logger classes
    // can be used.   
    private static void warn(String message) {
        if (useLogging) {
            System.err.println(message);
        }
    }
}
