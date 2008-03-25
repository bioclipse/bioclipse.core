/**
 * 
 */
package net.bioclipse.core.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * LogUtils:
 *   Utility functions for working with loggers.
 *   
 * @author rklancer
 *
 */
public class LogUtils {

    /** Returns a printable stack trace from a Throwable
     * 
     * @param t
     *            the Throwable
     * @return String with stack trace information from t
     */
    public static String traceStringFrom(Throwable t) {
        PrintWriter trace = new PrintWriter(new ByteArrayOutputStream());
        t.printStackTrace(trace);
        return trace.toString();
    }
    
    
    /** Prints stack trace from a Throwable to provided Logger
     * 
     * @param logger
     *           the Logger to which to output the stack trace from t
     * @param t
     *           the Throwable
     */
    public static void debugTrace(Logger logger, Throwable t) {
        // constructing stack trace string is expensive, so check isDebugEnabled
        if (logger.isDebugEnabled()) {
            logger.debug(traceStringFrom(t));
        }
    }
}
