/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.recording;

/**
 * A specially tailored class for scriptrecords. It is used for those 
 * special cases when manual recording is needed. 
 * 
 * @author jonalv
 */
public class ScriptRecord implements IRecord {

    public enum Language {
        JS;
    }

    private String js;
    
    public void setScript(Language l, String script) {
        this.js = script;
    }
    
    public String getScript(Language l) {
        switch (l) {
        case JS:
            return js;
        default:
            throw new IllegalArgumentException(l + "not supported");
        }
    }
}
