package net.bioclipse.core.api.managers;

import java.lang.reflect.Method;

import javax.naming.OperationNotSupportedException;

import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.util.IJavaScriptConsolePrinterChannel;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

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

        throw new UnsupportedOperationException("FIXME");
        //TODO: FIXME perhaps do something here when not running bsl-runner?
//        Display.getDefault().asyncExec( new Runnable() {
//            public void run() {
//                try {
//                    doInvoke( manager, method, arguments, invocation, true );
//                }
//                catch (Throwable t) {
//                    logger.log( Level.DEBUG, t );
//                    printError(t);
//                }
//            }
//        } );
//        return null;
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