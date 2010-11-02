/*
 * Copyright (c) 2010 Jonathan Alvarsson <jonalv@users.sourceforge.net> All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.bioclipse.core.api.managers;

/**
 * @author jonalv
 *
 * @param <T>
 */
public interface IBioclipseUIJob<T> {

    public abstract void setReturnValue( T returnValue );

    public abstract T getReturnValue();

    /**
     * Method that will be run using the UI thread. 
     * The return value van be reached by calling getReturnValue.
     */
    public abstract void runInUI();

    /**
     * Whether the manager job is run in background job. The default is not as
     * as background job but as user job. Implementors should override this 
     * method if not happy with default value.
     * 
     * @return whether the manager job is run as a background job
     */
    public abstract boolean runInBackground();

}