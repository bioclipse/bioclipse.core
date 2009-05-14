package net.bioclipse.core.business;

import java.util.List;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.ui.jobs.BioclipseJob;
import net.bioclipse.ui.jobs.BioclipseJobUpdateHook;


public interface ITestManager extends IBioclipseManager {

    public List<IBioObject> getBioObjects(String path);
    public List<IBioObject> getBioObjects(IFile ffile);
    public BioclipseJob<IBioObject> getBioObjects( IFile file, 
                                                   BioclipseJobUpdateHook h );
}
