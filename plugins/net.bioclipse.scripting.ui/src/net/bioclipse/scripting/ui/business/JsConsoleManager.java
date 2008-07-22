/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Carl Masak
 *     
 ******************************************************************************/
package net.bioclipse.scripting.ui.business;

import net.bioclipse.ui.Activator;

/**
 * Contains general methods for interacting with the Javascript console.
 * 
 * @author masak
 *
 */
public class JsConsoleManager implements IJsConsoleManager {

    public void clear() {
        Activator.getDefault().CONSOLE.echo("clear() called"); 
    }

    public String getNamespace() {
        return "js";
    }
}
