/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/

package net.bioclipse.data;

import org.eclipse.core.runtime.IProgressMonitor;

public class DummyProgressMonitor implements IProgressMonitor{

    public void beginTask(String name, int totalWork) {
        // TODO Auto-generated method stub
        
    }

    public void done() {
        // TODO Auto-generated method stub
        
    }

    public void internalWorked(double work) {
        // TODO Auto-generated method stub
        
    }

    public boolean isCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setCanceled(boolean value) {
        // TODO Auto-generated method stub
        
    }

    public void setTaskName(String name) {
        // TODO Auto-generated method stub
        
    }

    public void subTask(String name) {
        // TODO Auto-generated method stub
        
    }

    public void worked(int work) {
        // TODO Auto-generated method stub
        
    }

}

