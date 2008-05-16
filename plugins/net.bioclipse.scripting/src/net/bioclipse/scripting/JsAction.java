package net.bioclipse.scripting;

public class JsAction {
    private String command;
    private Hook postCommandHook;
    
    public JsAction(String command, Hook postCommandHook) {
        
        this.command = command;
        this.postCommandHook = postCommandHook;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void runPostCommandHook(String result) {
        postCommandHook.run(result);
    }
}
