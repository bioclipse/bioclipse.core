package net.bioclipse.ui;

public class EchoEvent {
    private String message;
    
    public EchoEvent(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
