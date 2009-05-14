package net.bioclipse.managers.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.ui.jobs.BioclipseJob;

import static org.junit.Assert.*;

/**
 * @author jonalv
 */
public class TestManager implements IBioclipseManager {

    public String getNamespace() {
        return "test";
    }
    
    public void getBioObjects( IFile file, 
                               BioclipseJob<IBioObject> job, 
                               IProgressMonitor monitor ) {

        assertNotNull( file );
        assertNotNull( job );
        assertNotNull( monitor );
    }
}
