package net.bioclipse.managers.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.GuiAction;
import net.bioclipse.managers.business.IBioclipseManager;

public interface ITestManager extends IBioclipseManager {

    public String getGreeting(String name);
    
    public void dontRunAsJob(IFile file);
    public void dontRunAsJob(String path);
    
    public void runAsJob(IFile file);
    public void runAsJob(String path);
    
    public IFile returnsAFile(IFile file);
    public String returnsAFile(String string);
    
    public String getPath(IFile file);
    public String getPath(String path);
    
    public List<IBioObject> getBioObjects(String path);
    public List<IBioObject> getBioObjects(IFile file);
    public BioclipseJob<IBioObject> getBioObjects( IFile file, 
                                                   BioclipseJobUpdateHook h );
    public void getBioObjects( IFile file, 
                               BioclipseUIJob<List<IBioObject>> uiJob );
    
    @GuiAction
    public void guiAction();
}
