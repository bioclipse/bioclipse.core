package net.bioclipse.core.domain;


public class ScriptSelection implements IChemicalSelection{

    private String script;
    
    public ScriptSelection(String script) {
        super();
        this.script = script;
    }

    public Object getSelection() {
        return script;
    }
    
    

}
