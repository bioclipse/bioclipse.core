package net.bioclipse.core.domain;

/**
 * Provides means to send scripts, where target is identified by a namspace
 * Example of namespace is "jmol"
 * 
 * @author ola
 *
 */
public class ScriptSelection implements IChemicalSelection{

    private String script;
    private String namespace;
    
    public ScriptSelection(String namespace, String script) {
        super();
        this.script = script;
    }

    public Object getSelection() {
        return script;
    }

    
    public String getScript() {
    
        return script;
    }

    
    public void setScript( String script ) {
    
        this.script = script;
    }

    
    public String getNamespace() {
    
        return namespace;
    }

    
    public void setNamespace( String namespace ) {
    
        this.namespace = namespace;
    }
    
    

}
