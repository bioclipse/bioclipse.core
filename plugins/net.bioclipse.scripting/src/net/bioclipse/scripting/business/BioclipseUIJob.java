package net.bioclipse.scripting.business;


/**
 * Class to be extended when updating gui after a long running manager call. 
 * Use getReturnValue() to get the return value from the manager call.
 * 
 * @author jonalv
 *
 * @param <T> type of return value
 */
public abstract class BioclipseUIJob<T> {

    private T returnValue;
    
    void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }
    
    T getReturnValue() {
        return returnValue;
    }
    
    public abstract void runInUI();
}
