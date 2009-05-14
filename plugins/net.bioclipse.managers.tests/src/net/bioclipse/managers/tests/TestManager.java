package net.bioclipse.managers.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.IPartialReturner;
import net.bioclipse.managers.business.IBioclipseManager;

import static org.junit.Assert.*;

/**
 * @author jonalv
 */
public class TestManager implements IBioclipseManager {

    public static volatile Boolean methodRun = false;
    public static Object lock = new Object(); 
    
    public String getNamespace() {
        return "test";
    }
    
    public void getBioObjects( IFile file, 
                               IPartialReturner returner, 
                               IProgressMonitor monitor ) {

        assertNotNull( file );
        assertNotNull( monitor );
        returner.partialReturn( new BioObject(){} );
        returner.partialReturn( new BioObject(){} );
        done();
    }
    
    public String getGreeting(String name) {
        done();
        return "OH HAI " + name;
    }
    
    public void runAsJob(IFile file, IProgressMonitor monitor) {
        monitor.beginTask( "bla", 2 );
        monitor.worked( 1 );
        monitor.worked( 1 );
        monitor.done();
        done();
    }
    
    public void dontRunAsJob(IFile file) {
        done();
    }
    
    public String getPath(IFile file) {
        done();
        return file.getFullPath().toPortableString();
    }
    
    public void done() {
        methodRun = true;
        synchronized ( lock ) {
            lock.notifyAll();
        }
    }
    
    public void guiAction() {
        assertNotNull( Display.getCurrent() );
        done();
    }
}
