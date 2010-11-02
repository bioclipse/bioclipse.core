/*
 * Copyright (c) 2010 Jonathan Alvarsson <jonalv@users.sourceforge.net> All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.bioclipse.core.api.jobs;

import java.lang.reflect.Method;

import net.bioclipse.core.api.managers.IBioclipseManager;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author jonalv
 *
 * @param <T>
 */
public interface IBioclipseJob<T> {

    public static final Object NULLVALUE = new Object();

    @SuppressWarnings("unchecked")
    public abstract T getReturnValue();

    public abstract void setMethod( Method method );

    public abstract Method getMethod();

    public abstract void setArguments( Object[] arguemtns );

    public abstract MethodInvocation getInvocation();

    public abstract void setBioclipseManager( IBioclipseManager manager );

    public abstract void setMethodCalled( Method methodCalled );

    public abstract Method getMethodCalled();

}