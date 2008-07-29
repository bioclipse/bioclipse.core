/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

/**
 * Implements a general scripting environment. An "environment" is something
 * that holds settings and variables during a scripting session.
 * 
 * @author masak
 *
 */
public interface ScriptingEnvironment {

    /**
     * Resets the environment. Clears all variables and settings, in
     * effect starting at a clean slate.
     */
    public void reset();
    
    /**
     * Evaluates an expression and returns the result. May have side
     * effects in the form of variables being set, etc.
     *  
     * @param expression The expression to be evaluated.
     * @return The result of the evaluation. Never <code>null</code>.
     */
    public String eval(String expression);
    
}