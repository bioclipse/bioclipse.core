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

package net.bioclipse.jsexecution.tools;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A singleton keeping track of all monitors used by all threads executing 
 * JavaScript scripts by connecting a monitor with a thread.
 * 
 * @author jonalv
 *
 */
public class MonitorContainer {

    private static MonitorContainer _instance = new MonitorContainer();
    
    private Map<Thread, IProgressMonitor> monitors;
    
    private MonitorContainer() {
        monitors = new WeakHashMap<Thread, IProgressMonitor>();
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
        return monitors.get( Thread.currentThread() );
    }
}
