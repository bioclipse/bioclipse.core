package net.bioclipse.core.domain;

import java.util.HashMap;
import java.util.List;
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
    
    public ScriptSelection() {
        scripts=new HashMap<String, String>();
    }

    public Object getSelection() {
        return scripts;
    }
    
    public void addScript(String script, String namespace){
        scripts.put( script, namespace);
    }

}
