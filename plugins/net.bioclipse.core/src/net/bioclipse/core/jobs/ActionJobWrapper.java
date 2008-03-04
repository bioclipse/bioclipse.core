package net.bioclipse.core.jobs;

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.core.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


public class ActionJobWrapper extends Job {
    
    private IActionJob actionJob = null;

    /**
     * @param name
     */
    public ActionJobWrapper(IActionJob job) {
        super(job.getJobName());
        this.actionJob = job;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run(IProgressMonitor monitor) {
        IStatus returnValue = Status.OK_STATUS;
        IStatus exceptionStatus = null;
        setPriority(Job.INTERACTIVE);       
        monitor.beginTask(this.actionJob.getJobDescription(), this.actionJob.getTotalTime());           
        try{                        
            this.actionJob.run(monitor);                      
        }
        catch(InvocationTargetException ex)
        {
            exceptionStatus = ErrorHandler.solveException(ex.getCause(),Activator.PLUGIN_ID);
            returnValue = new MultiStatus(Activator.PLUGIN_ID,
                    500,new IStatus[]{exceptionStatus},ex.getMessage(),null);
            
        }
        catch (InterruptedException e) {
            exceptionStatus = ErrorHandler.solveException(e,Activator.PLUGIN_ID);
            returnValue = new MultiStatus(Activator.PLUGIN_ID,
                    500,new IStatus[]{exceptionStatus},e.getMessage(),null);
        }
        finally{
            
            monitor.done();
        }
        
        if (monitor.isCanceled() )
            returnValue = Status.CANCEL_STATUS;
           
            
        return returnValue;
    }
}
