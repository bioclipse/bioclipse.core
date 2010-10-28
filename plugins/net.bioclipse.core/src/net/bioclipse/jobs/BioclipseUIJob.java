package net.bioclipse.jobs;

import net.bioclipse.core.api.managers.IBioclipseUIJob;


/**
 * Class to be extended when updating gui after a long running manager call. 
 * Use getReturnValue() to get the return value from the manager call.
 * 
 * @author jonalv
 *
 * @param <T> type of return value
 */
public abstract class BioclipseUIJob<T> implements IBioclipseUIJob<T> {

    private T returnValue;
    
    /* (non-Javadoc)
     * @see net.bioclipse.jobs.IBioclipseUIJob#setReturnValue(T)
     */
    @Override
    public void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.jobs.IBioclipseUIJob#getReturnValue()
     */
    @Override
    public T getReturnValue() {
        return returnValue;
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.jobs.IBioclipseUIJob#runInUI()
     */
    @Override
    public abstract void runInUI();
    
    /* (non-Javadoc)
     * @see net.bioclipse.jobs.IBioclipseUIJob#runInBackground()
     */
    @Override
    public boolean runInBackground() {
        return false;
    }
}
