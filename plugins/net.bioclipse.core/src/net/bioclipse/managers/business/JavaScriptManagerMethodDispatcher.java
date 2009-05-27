package net.bioclipse.managers.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.core.IResourcePathTransformer;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.util.IJavaScriptConsolePrinterChannel;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IPartialReturner;
import net.bioclipse.managers.MonitorContainer;

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

    @Override
    protected Object doInvokeInGuiThread( final IBioclipseManager manager, 
                                          final Method method,
                                          final Object[] arguments ) {

        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                try {
                    doInvoke( manager, method, arguments );
                }
                catch (Throwable t) {
                    printError(t);
                }
            }
        } );
        return null;
    }
    
    private void printError( Throwable t ) {

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
                ( (IJavaScriptConsolePrinterChannel) service ).printError(t);
            }
        }
    }

    @Override
    protected Object doInvokeInSameThread( IBioclipseManager manager, 
                                           Method method,
                                           Object[] arguments ) 
                     throws BioclipseException {
        return doInvoke( manager, method, arguments );
    }
}