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
public class ScriptSelection implements IChemicalSelection{

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

    public Object getSelection() {
        return scripts;
    }
    
    public void addScript(String script, String namespace){
        scripts.put( script, namespace);
    }

}
