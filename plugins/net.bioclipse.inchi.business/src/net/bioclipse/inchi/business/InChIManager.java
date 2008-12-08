/*******************************************************************************
 * Copyright (c) 2007  Jonathan Alvarsson
 *               2008  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.inchi.business;

import net.bioclipse.ui.Activator;

public class InChIManager implements IInChIManager {

    public void example(String ex) {
        Activator.getDefault().CONSOLE.echo(
            "InChIManager.example() called with:" + ex
        ); 
    }

    public String getNamespace() {
        return "inchi";
    }
}
