package net.bioclipse.core.business;

import net.bioclipse.core.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;


public class MoleculeManagerFactory implements IExecutableExtension, 
                                               IExecutableExtensionFactory {

    private IMoleculeManager manager;
    
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data ) throws CoreException {

        manager = Activator.getDefault().getMoleculeManager();
    }

    public Object create() throws CoreException {
        return manager;
    }
}
