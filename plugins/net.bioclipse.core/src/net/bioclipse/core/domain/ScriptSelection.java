/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core.domain;
import java.util.HashMap;
import java.util.Map;
/**
 * Provides means to send scripts, where target is identified by a namspace
 * Example of namespace is "jmol"
 * 
 * @author ola
 *
 */
public class ScriptSelection extends AbstractChemicalSelection{
    //Each command is stored in a map, with Script -> Namespace
    private Map<String, String> scripts;
    /**
     * Create an empty ScriptSelection
     */
    public ScriptSelection() {
        scripts=new HashMap<String, String>();
    }
    /**
     * Create a new ScriptSelection and add a first script
     * @param script the script to provide
     * @param namespace a namespace for the intended target
     */
    public ScriptSelection(String script, String namespace) {
        this();
        addScript( script, namespace );
    }
    /**
     * Create a new ScriptSelection and add a first script
     * @param script the script to provide
     * @param namespace a namespace for the intended target
     * @param chemicalModel the model object = molecule
     */
    public ScriptSelection(String script, String namespace, Object chemicalModel) {
        this();
        addScript( script, namespace );
        setChemicalModel(chemicalModel);
    }
    public Map<String, String> getSelection() {
        return scripts;
    }
    public void addScript(String script, String namespace){
        scripts.put( script, namespace);
    }
}
