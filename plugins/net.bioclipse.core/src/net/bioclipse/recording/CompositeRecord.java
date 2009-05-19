/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.recording;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jonathan
 *
 */
public class CompositeRecord implements IRecord {

    protected Map<String, String> scripts = new HashMap<String, String>();
    
    public void addScriptRecord( String scriptLanguage, String scriptRecord ) {

        scripts.put(scriptLanguage, scriptRecord);
    }

    public String getScript( String scriptLanguage ) 
                  throws NoSuchScriptLanguageFound {
        
        if( !scripts.containsKey(scriptLanguage) ) {
            throw new NoSuchScriptLanguageFound(scriptLanguage);
        }
        return scripts.get(scriptLanguage);
    }
}
