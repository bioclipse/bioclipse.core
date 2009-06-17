/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/

package net.bioclipse.managers;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * A singleton keeping track of all monitors used by all threads executing 
 * JavaScript scripts by connecting a monitor with a thread.
 * 
 * @author jonalv
 *
 */
public class MonitorContainer {

    private static MonitorContainer _instance = new MonitorContainer();
    private static Logger logger = Logger.getLogger( MonitorContainer.class );
    
    private volatile Map<Thread, IProgressMonitor> monitors;
    private volatile Map<Thread, Long>             lastWarningTimes;
    
    private MonitorContainer() {
        monitors         = new WeakHashMap<Thread, IProgressMonitor>();
        lastWarningTimes = new WeakHashMap<Thread, Long>();
    }

    public static MonitorContainer getInstance() {
        return _instance;
    }
    
    /**
     * Associates the given monitor with the current thread.
     * 
     * @param monitor to be associated with the current thread
     */
    public synchronized void addMonitor( IProgressMonitor monitor ) {
        monitors.put( Thread.currentThread(), monitor );
    }
    
    /**
     * @return monitor associated with the current thread
     */
    public synchronized IProgressMonitor getMonitor() {
        Thread t = Thread.currentThread();
        IProgressMonitor m =  monitors.get( t );
        if ( m == null ) {
            
            int timeout = 120;
            if ( !lastWarningTimes.containsKey( t ) || 
                    System.currentTimeMillis()
                        - lastWarningTimes.get( t ) > 1000 * timeout ) {
                   
                lastWarningTimes.put( t, System.currentTimeMillis() );
                logger.warn( "The MonitorContainer could not find a monitor " +
                             "connected to current thread so returning a " +
                             "NullProgressMonitor. This warning will not be " +
                             "repeated withing the comming " + timeout + 
                             "seconds." );
            }
            m = new NullProgressMonitor();
        }
        return m;
    }
}
