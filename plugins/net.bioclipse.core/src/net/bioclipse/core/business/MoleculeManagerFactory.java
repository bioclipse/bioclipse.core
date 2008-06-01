package net.bioclipse.core.business;

import net.bioclipse.core.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class MoleculeManagerFactory implements IExecutableExtension, 
                                               IExecutableExtensionFactory {

    private IMoleculeManager manager;
    
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data ) throws CoreException {

        try {
            manager = Activator.getDefault().getMoleculeManager();
            if (false) {
                throw new CoreException(new Status(
                    IStatus.ERROR, Activator.getDefault().PLUGIN_ID, "Will never occur; just to make Eclipse happy about the otherwise never thrown CoreException."
                ));
            }
        } catch (CoreException exception) {
            System.out.println("Mol manager could not be started: " + exception.getMessage());
            exception.printStackTrace();
            throw exception;
        }
    }

    public Object create() throws CoreException {
        return manager;
    }
}
