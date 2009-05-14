package net.bioclipse.managers.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.managers.business.IBioclipseManager;

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
        assertNotNull( monitor );
        job.partialReturn( new BioObject(){} );
        job.partialReturn( new BioObject(){} );
    }
    
    public String getGreeting(String name) {
        return "OH HAI " + name;
    }
    
    public void runAsJob(IFile file, IProgressMonitor monitor) {
        monitor.beginTask( "bla", 2 );
        monitor.worked( 1 );
        monitor.worked( 1 );
        monitor.done();
    }
}
