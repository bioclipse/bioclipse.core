/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core.jobs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public abstract class ActionJobAdapter implements IActionJob {

    /* (non-Javadoc)
     * @see de.spiritlink.ecore.jobinterface.jobs.IEcoreActionJob#getTotalTime()
     */
    public int getTotalTime() {
        return IProgressMonitor.UNKNOWN;
    }

    /* (non-Javadoc)
     * @see de.spiritlink.ecore.jobinterface.jobs.IEcoreActionJob#getJobName()
     */
    public String getJobName() {
        return ""; //$NON-NLS-1$
    }
    /* (non-Javadoc)
     * @see de.spiritlink.ecore.jobinterface.jobs.IEcoreActionJob#getJobDescription()
     */
    public String getJobDescription() {
        return getJobName();
    }

    /* (non-Javadoc)
     * @see de.spiritlink.ecore.jobinterface.jobs.IEcoreActionJob#getRule()
     */
    public ISchedulingRule getRule() {
         return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        // implemented by client.
    }
}
