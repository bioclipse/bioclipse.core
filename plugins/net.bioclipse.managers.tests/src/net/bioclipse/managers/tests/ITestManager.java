package net.bioclipse.managers.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.managers.business.ManagerImplementation;

@ManagerImplementation(TestManager.class)
public interface ITestManager extends IBioclipseManager {

    public List<IBioObject> getBioObjects(String path);
    public List<IBioObject> getBioObjects(IFile file);
    public BioclipseJob<IBioObject> getBioObjects( IFile file, 
                                                   BioclipseJobUpdateHook h );
    
    public String getGreeting(String name);
    
    public void runAsJob(IFile file);
    public void runAsJob(String path);
    
    public void dontRunAsJob(IFile file);
    public void dontRunAsJob(String path);
    
    public String getPath(IFile file);
    public String getPath(String path);
}
