package net.bioclipse.core.business;

import java.util.List;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.ui.jobs.BioclipseJob;
import net.bioclipse.ui.jobs.BioclipseJobUpdateHook;


public interface ITestManager extends IBioclipseManager {

    public List<IBioObject> getBioObjects(String p);
    public List<IBioObject> getBioObjects(IFile f);
    public BioclipseJob<IBioObject> getBioObjects( IFile f, 
                                                   BioclipseJobUpdateHook h );
}
