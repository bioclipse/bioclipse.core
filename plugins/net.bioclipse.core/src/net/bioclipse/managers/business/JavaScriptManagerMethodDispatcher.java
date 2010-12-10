package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.IResourcePathTransformer;
import net.bioclipse.core.api.ResourcePathTransformer;
import net.bioclipse.core.api.domain.BioObject;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.domain.RecordableList;
import net.bioclipse.core.api.jobs.IReturner;
import net.bioclipse.core.api.jobs.MonitorContainer;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.util.IJavaScriptConsolePrinterChannel;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.jobs.BioclipseUIJob;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;

/**
 * @author jonalv
 *
 */
public class JavaScriptManagerMethodDispatcher 
       extends AbstractManagerMethodDispatcher {

    private static Logger logger 
        = Logger.getLogger( JavaScriptManagerMethodDispatcher.class );
    
    @Override
    protected Object doInvokeInGuiThread( final IBioclipseManager manager, 
                                          final Method method,
                                          final Object[] arguments,
                                          final MethodInvocation invocation ) {

        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                try {
                    doInvoke( manager, method, arguments, invocation, true );
                }
                catch (Throwable t) {
                    LogUtils.debugTrace( logger, t );
                    printError(t);
                }
            }
        } );
        return null;
    }
    
    public static void printError( Throwable t ) {

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint serviceObjectExtensionPoint 
            = registry.getExtensionPoint(
                  "net.bioclipse.scripting.contribution" );

        IExtension[] serviceObjectExtensions
            = serviceObjectExtensionPoint.getExtensions();
        for ( IExtension extension : serviceObjectExtensions) {
            for ( IConfigurationElement element 
                    : extension.getConfigurationElements() ) {
                Object service = null;
                try {
                    service = element.createExecutableExtension("service");
                }
                catch (CoreException e) {
                    throw new RuntimeException(e);
                }
                if ( service instanceof IJavaScriptConsolePrinterChannel) {
                    ( (IJavaScriptConsolePrinterChannel) service )
                        .printError(t);
                }
            }
        }
    }

    @Override
    protected Object doInvokeInSameThread( IBioclipseManager manager, 
                                           Method method,
                                           Object[] arguments,
                                           MethodInvocation invocation ) 
                     throws BioclipseException {
        return doInvoke( manager, method, arguments, invocation, true );
    }
}