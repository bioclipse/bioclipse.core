package net.bioclipse.managers.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.core.api.jobs.GuiAction;
import net.bioclipse.core.api.jobs.IBioclipseJob;
import net.bioclipse.core.api.jobs.IExtendedBioclipseJob;
import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseUIJob;

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
    
    public void getBioObject( IFile file,
                              BioclipseJobUpdateHook h  );

    public void createBioObject( IFile file,
                                 BioclipseUIJob<IBioObject> uiJob );
    
    public BioclipseJob<IBioObject> 
           createBioObject( IFile file,
                            BioclipseJobUpdateHook<IBioObject> h );
    
    @GuiAction
    public void guiAction();
    
    public IBioclipseJob<Void> voidJobMethod(String path);
    
    public IBioclipseJob<Void> voidJobMethod(IFile file);
    
    /*
     * ExtendedBioclipseJob
     */
    
    public IExtendedBioclipseJob<Void> extendedVoidJobMethod(String path);
    
    public IExtendedBioclipseJob<Void> extendedVoidJobMethod(IFile file);

    public IExtendedBioclipseJob<IBioObject> 
               extendedGetBioObjects( IFile file, 
                                      BioclipseJobUpdateHook h );

}
