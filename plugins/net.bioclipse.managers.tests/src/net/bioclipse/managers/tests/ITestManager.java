package net.bioclipse.managers.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.managers.business.ManagerImplementation;
import net.bioclipse.ui.jobs.BioclipseJob;
import net.bioclipse.ui.jobs.BioclipseJobUpdateHook;

@ManagerImplementation(TestManager.class)
public interface ITestManager extends IBioclipseManager {

    public List<IBioObject> getBioObjects(String path);
    public List<IBioObject> getBioObjects(IFile ffile);
    public BioclipseJob<IBioObject> getBioObjects( IFile file, 
                                                   BioclipseJobUpdateHook h );
}
