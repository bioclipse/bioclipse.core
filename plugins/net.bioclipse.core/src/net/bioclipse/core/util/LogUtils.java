/**
 * 
 */
package net.bioclipse.core.util;

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
    public static String getTraceStringFrom(Throwable t) {
        PrintWriter trace = new PrintWriter(new ByteArrayOutputStream());
        t.printStackTrace(trace);
        return trace.toString();
    }
}
