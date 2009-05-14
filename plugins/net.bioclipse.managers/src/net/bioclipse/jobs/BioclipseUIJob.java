package net.bioclipse.jobs;


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
    
    public T getReturnValue() {
        return returnValue;
    }
    
    /**
     * Method that will be run using the UI thread. 
     * The return value van be reached by calling getReturnValue.
     */
    public abstract void runInUI();
    
    /**
     * Whether the manager job is run in background job. The default is not as
     * as background job but as user job. Implementors should override this 
     * method if not happy with default value.
     * 
     * @return whether the manager job is run as a background job
     */
    public boolean runInBackground() {
        return false;
    }
}
