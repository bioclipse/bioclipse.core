package net.bioclipse.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.IBioclipseManager;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.WorkbenchJob;


/**
 * @author jonalv
 *
 */
public class BioclipseJob<T> extends Job {

    private Method method;
    private MethodInvocation invocation;
    private boolean newWay;

    private static Logger logger = Logger.getLogger( BioclipseJob.class );
    
    public BioclipseJob( String name, 
                         Method methodToBeInvocated, 
                         MethodInvocation originalInvocation ) {
        super( name );
        this.setMethod( methodToBeInvocated );
        this.setInvocation( originalInvocation );
        newWay = false;
    }
    
    public BioclipseJob(String name) {
        super( name );
        newWay = true;
    }

    public static final Object NULLVALUE = new Object();
    
    private Object returnValue = NULLVALUE;
    private IBioclipseManager bioclipseManager;
    
    protected IStatus run( IProgressMonitor monitor ) {

        if ( newWay ) {
            return runNewWay(monitor);
        }
        
        Object[] args;
        try {
            if ( getMethod() != getInvocation().getMethod() ) {
                /*
                 * Setup args array
                 */
                boolean hasUIJob = false;
                for ( Object o : getInvocation().getArguments() ) {
                    if ( o instanceof BioclipseUIJob ) {
                        hasUIJob = true;
                        break;
                    }
                }
                if (hasUIJob) {
                    args = new Object[getInvocation().getArguments().length];
                }
                else { 
                    args = new Object[getInvocation().getArguments().length + 1];
                }
                args[args.length-1] = monitor;
                System.arraycopy( getInvocation().getArguments(), 
                                  0, 
                                  args, 
                                  0, 
                                  args.length - 1 );
                /*
                 * Then substitute from String to IFile where suitable
                 */
                for ( int i = 0; i < args.length; i++ ) {
                    Object arg = args[i];
                    if ( arg instanceof String &&
                        getMethod().getParameterTypes()[i] == IFile.class ) {
                         
                        args[i] = ResourcePathTransformer
                                  .getInstance()
                                  .transform( (String) arg );
                    }
                }
            }
            else {
                args = getInvocation().getArguments();
                monitor.beginTask( "", IProgressMonitor.UNKNOWN );
            }
        
            returnValue = getMethod().invoke( getInvocation().getThis(), args );
            
            int i = Arrays.asList( getInvocation().getMethod().getParameterTypes() )
                          .indexOf( BioclipseUIJob.class );

            final BioclipseUIJob uiJob ;
            
            if ( i != -1 )
                uiJob = ( (BioclipseUIJob)getInvocation().getArguments()[i] );
            else {
                uiJob = null;
            }
            
            if ( uiJob != null ) {
                uiJob.setReturnValue( returnValue );
                new WorkbenchJob("Refresh") {
        
                    @Override
                    public IStatus runInUIThread( 
                            IProgressMonitor monitor ) {
                        uiJob.runInUI();
                        return Status.OK_STATUS;
                    }
                    
                }.schedule();
            }
        } 
        catch ( Exception e ) {
            returnValue = e;
            if (e instanceof InvocationTargetException) {
                returnValue = e.getCause();
            }
            
            throw new RuntimeException( "Exception occured: "
                                        + e.getClass().getSimpleName() 
                                        + " - "
                                        + e.getMessage(),
                                        e );
        }
        finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }

    private IStatus runNewWay( IProgressMonitor monitor ) {

        Object[] arguments;
        try {
            List<Object> newArguments = new ArrayList<Object>();
            newArguments.addAll( Arrays.asList( invocation.getArguments() ) );
            
            boolean doingPartialReturns = false;
            ReturnCollector returnCollector = new ReturnCollector();
            //add partial returner
            for ( Class<?> param : method.getParameterTypes() ) {
                if ( param == IPartialReturner.class ) {
                    doingPartialReturns = true;
                    boolean alreadyHasPartialReturner = false;
                    for ( Object o : newArguments ) {
                        if ( o instanceof IPartialReturner ) {
                            alreadyHasPartialReturner = true;
                        }
                    }
                    if ( !alreadyHasPartialReturner ) {
                        newArguments.add( returnCollector );
                    }
                }
            }
            
            int i = Arrays.asList( getInvocation().getMethod().getParameterTypes() )
                          .indexOf( BioclipseUIJob.class );
    
            final BioclipseUIJob uiJob ;
            
            if ( i != -1 )
                uiJob = ( (BioclipseUIJob)getInvocation().getArguments()[i] );
            else {
                uiJob = null;
            }

            newArguments.add( monitor );
            monitor.beginTask( "", IProgressMonitor.UNKNOWN );
            
            arguments = newArguments.toArray();
            //translate String -> IFile
            for ( int j = 0; j < arguments.length; j++ ) {
                if ( arguments[j] instanceof String &&
                     method.getParameterTypes()[j] == IFile.class ) {
                    arguments[j] = ResourcePathTransformer.getInstance()
                                       .transform( (String)arguments[j] );
                }
            }

            returnValue = getMethod().invoke( bioclipseManager, 
                                              arguments );
            
            if ( doingPartialReturns ) {
                returnValue = returnCollector.getReturnValues();
            }
            
            if ( uiJob != null ) {
                uiJob.setReturnValue( returnValue );
                new WorkbenchJob("Refresh") {
        
                    @Override
                    public IStatus runInUIThread( 
                            IProgressMonitor monitor ) {
                        uiJob.runInUI();
                        return Status.OK_STATUS;
                    }
                    
                }.schedule();
            }
        } 
        catch ( Exception e ) {
            returnValue = e;
            if (e instanceof InvocationTargetException) {
                returnValue = e.getCause();
            }
            LogUtils.debugTrace( logger, e );
            throw new RuntimeException( "Exception occured: "
                                        + e.getClass().getSimpleName() 
                                        + " - "
                                        + e.getMessage(),
                                        e );
        }
        finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }

    @SuppressWarnings("unchecked")
    public synchronized T getReturnValue() {
        if ( returnValue == NULLVALUE ) {
            throw new IllegalStateException( "There is no return value" );
        }
        return (T)returnValue;
    }

    public void partialReturn( BioObject bioObject ) {
        // TODO Auto-generated method stub
    }

    public void setMethod( Method method ) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setInvocation( MethodInvocation invocation ) {
        this.invocation = invocation;
    }

    public MethodInvocation getInvocation() {
        return invocation;
    }

    public void setBioclipseManager( IBioclipseManager manager ) {
        this.bioclipseManager = manager;
    }
    
    private static class ReturnCollector implements IPartialReturner {

        private List<IBioObject> returnValues = new BioList<IBioObject>();
        
        public void partialReturn( BioObject bioObject ) {
            
            synchronized ( returnValues ) {
                returnValues.add( bioObject );
            }
        }
        
        public List<IBioObject> getReturnValues() {
            synchronized ( returnValues ) {
                return returnValues;
            }
        }
    }
}
