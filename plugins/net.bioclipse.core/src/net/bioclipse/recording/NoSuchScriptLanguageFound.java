/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.recording;

public class NoSuchScriptLanguageFound extends Exception {

    private static final long serialVersionUID = -3593316094806126773L;

    public NoSuchScriptLanguageFound(String scriptLanguage) {
        super(scriptLanguage);
    }
}
