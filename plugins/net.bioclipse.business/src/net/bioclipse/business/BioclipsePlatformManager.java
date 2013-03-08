/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

public class BioclipsePlatformManager implements IBioclipseManager {

    public String getManagerName() {
        return "bioclipse";
    }

    public void planet() throws BioclipseException {
    	try {
			openURL(new URL("http://planet.bioclipse.net"));
		} catch (MalformedURLException e) {
			throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
		}
    }

    public void wiki() throws BioclipseException {
    	try {
			openURL(new URL("http://wiki.bioclipse.net"));
		} catch (MalformedURLException e) {
			throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
		}
    }

    public String logfileLocation() {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        for (Logger logger : context.getLoggerList()) {
                for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                    Appender<ILoggingEvent> appender = index.next();
                    if(appender instanceof FileAppender) {
                        return stripExtraSlashes( ((FileAppender<?>)appender).getFile());
                    }
                }
            }
        return "";
    }

    /* Due to the way the logfile location is assembled an extra slash may is
     * should not have any effect on the code. This method strips the extra slashes
     * for a better visual presentation see bug 3479.
     */
    static String stripExtraSlashes(String input) {
        return input.replaceAll( "/(/)+", "/" );
    }

    public void bugTracker() throws BioclipseException {
    	try {
			openURL(new URL("http://bugs.bioclipse.net"));
		} catch (MalformedURLException e) {
			throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
		}
    }

    public String download(String url, IProgressMonitor monitor)
    throws BioclipseException {
        return download(url, null, monitor);
    }

    public String download(String url, String mimeType,
            IProgressMonitor monitor) throws BioclipseException {
        StringBuffer content = new StringBuffer();
        URLConnection rawConn;
        try {
            rawConn = createURL(url).openConnection();
            if (mimeType != null)
                rawConn.addRequestProperty("Accept", mimeType);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(rawConn.getInputStream())
            );
            String line = reader.readLine();
            while (line != null) {
                content.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException exception) {
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        }
        return content.toString();
    }

    public IFile downloadAsFile(String url, IFile target,
            IProgressMonitor monitor) throws BioclipseException {
        return downloadAsFile(url, null, target, monitor);
    }

    public IFile downloadAsFile(String url, String mimeType, IFile target,
        IProgressMonitor monitor)
    throws BioclipseException {
    	return downloadAsFile(url, mimeType, target, null, monitor);
    }

    public IFile downloadAsFile(String url, String mimeType, IFile target,
        Map<String,String> extraHeaders, IProgressMonitor monitor)
    throws BioclipseException {
        URLConnection rawConn;
        try {
            rawConn = createURL(url).openConnection();
            if (extraHeaders != null) {
            	for (String header : extraHeaders.keySet()) {
            		rawConn.addRequestProperty(header, extraHeaders.get(header));
            	}
            }
            if (mimeType != null)
                rawConn.addRequestProperty("Accept", mimeType);
            if (target.exists()) {
                target.setContents(rawConn.getInputStream(), true, false, null);
            } else {
                target.create(rawConn.getInputStream(), false, null);
            };
        } catch (IOException exception) {
        	if (exception.getMessage().contains("403"))
        		throw new BioclipseException(
                    "No access.", exception
                );
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        } catch (CoreException exception) {
            throw new BioclipseException(
                "Error while downloading from URL.", exception
            );
        }
        return target;
    }


    public void openURL(URL url) throws BioclipseException {
    	IWorkbenchBrowserSupport browserSupport =
    		PlatformUI.getWorkbench().getBrowserSupport();
    	IWebBrowser browser;
    	try {
    		browser = browserSupport.createBrowser(
    				IWorkbenchBrowserSupport.LOCATION_BAR |
    				IWorkbenchBrowserSupport.NAVIGATION_BAR,
    				null, null, null
    		);
    		browser.openURL(url);
    	} catch (PartInitException e) {
    		throw new BioclipseException("Error while opening browser: " +
				e.getMessage(), e);
    	}
    }

    private URL createURL(String url) throws BioclipseException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BioclipseException("Error while opening browser: " +
                e.getMessage(), e);
        }
    }

    public boolean isOnline() {
    	// if both fail, we do not have internet
    	String[] sites = new String[]{
    		"http://google.com/",
    		"http://slashdot.org/"
    	};
    	for (String site : sites) {
    		try {
    		    URL url = new URL(site);
    		    URLConnection conn = url.openConnection();
    		    conn.connect();
    		    return true;
    		} catch (Exception exception) {}
    	}
    	return false;
    }

    public String version() {
        return System.getProperty( "eclipse.buildId" );
    }

    public static class VersionNumberComparator implements Comparator<String> {

        private static Pattern p
            = Pattern.compile( "(\\d+)\\.(\\d+)(?:\\.(\\d+)(?:\\.(\\S+))?)?" );

        private static final int QUALIFER_POSITION = 4;

        private VersionNumberComparator() {
        }

        public static final VersionNumberComparator INSTANCE
            = new VersionNumberComparator();

        @Override
        public int compare( String o1, String o2 ) {
            Matcher m1 = p.matcher( o1 );
            Matcher m2 = p.matcher( o2 );
            if ( !m1.matches() || !m2.matches() ) {
                // Build error message
                String s = null;
                if ( !m1.matches() ) {
                    s = o1;
                }
                else if ( !m2.matches() ) {
                    s = o2;
                }
                throw new IllegalArgumentException(
                    "Could not identify the String: \"" + s + "\" as a " +
                    "version number. Version numbers looks like these: " +
                    "\"2.2\", \"2.2.0\", or \"2.2.0.RC1");
            }
            else {
                int groups = Math.max( m1.groupCount(), m2.groupCount() );
                for ( int i = 0 ; i < groups ; i++ ) {

                    if ( i+1 == QUALIFER_POSITION ) {
                        String g1 = m1.group(i+1) != null ? m1.group(i+1)
                                                          : "";
                        String g2 = m2.group(i+1) != null ? m2.group(i+1)
                                                        : "";
                        return g1.compareTo( g2 );
                    }
                    String g1 = m1.group(i+1) != null ? m1.group(i+1)
                                                      : "0";
                    String g2 = m2.group(i+1) != null ? m2.group(i+1)
                                                      : "0";
                    Integer i1 = Integer.parseInt( g1 );
                    Integer i2 = Integer.parseInt( g2 );
                    if ( i1 < i2 ) {
                        return -1;
                    }
                    if ( i1 > i2 ) {
                        return +1;
                    }
                }
                return 0;
            }
        }
    }

    public void requireVersion( String version ) throws BioclipseException {
        try {
            if (!(VersionNumberComparator.INSTANCE
                                         .compare( version, version() ) <= 0)) {
                throw new BioclipseException(
                              "You are running Bioclipse version: " + version()
                              + "but require: " + version );
            }
        } catch(Exception e) {
            throw new BioclipseException(e.getMessage(), e);
        }
    }

    public void assumeOnline() throws BioclipseException {
    	if (!isOnline())
    		throw new BioclipseException(
    			"Bioclipse does not have internet access."
    		);
    }

    public void requireVersion( String lowerVersionBound,
                                String upperVersionBound )
                   throws BioclipseException {
        try {
            String version = version();
            List<String> versions = new ArrayList<String>();
            versions.add( lowerVersionBound );
            versions.add( version );
            versions.add( upperVersionBound );
            Collections.sort( versions, VersionNumberComparator.INSTANCE );
            if (!( version == versions.get( 1 ) &&
                   VersionNumberComparator.INSTANCE
                                          .compare(version,
                                                   versions.get( 2 ) ) == -1)) {
                throw new BioclipseException(
                     "You are running Bioclipse version: " + version() +
                     " but require ["+lowerVersionBound +  ", "
                     + upperVersionBound + ")");
            }
        }
        catch (Exception e) {
            throw new BioclipseException(e.getMessage(), e);
        }
    }
}
