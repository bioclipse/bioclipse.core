package net.bioclipse.ui.install.commands;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;

public interface IRepositoryDiscoveryStrategy {

    public abstract void addLocation( URI location );

    public abstract void removeLocation( URI location );

    public abstract void performDiscovery( IProgressMonitor progressMonitor )
                                                                             throws CoreException;

    public abstract String getProperty( IInstallableUnit candidate, String key );

}