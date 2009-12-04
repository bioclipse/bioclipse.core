package net.bioclipse.webservices.wizards.newwizards;

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.core.Activator;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.webservices.business.IWebservicesManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard to download and split PDB files from WSDbfetch
 * @author ola
 *
 */
public class DownloadPDBWizard extends Wizard implements INewWizard {

    private static final Logger logger = 
        Logger.getLogger(DownloadPDBWizard.class);

    private IContainer container;
    private String pdbids;
    
    public String getPdbids() {
        return pdbids;
    }
    public void setPdbids( String pdbids ) {
        this.pdbids = pdbids;
    }


    @Override
    public void addPages() {
        addPage( new DownloadPDBPage("Download PDB") );
    }
    
    @Override
    public boolean canFinish() {
        if (pdbids==null || pdbids.length()<=0) return false;
        return true;
    }

    
    @Override
    public boolean performFinish() {
        
        if (container==null)
            container=Activator.getVirtualProject();

        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) {
                   monitor.beginTask("Downloading PDBs... ", 
                                     IProgressMonitor.UNKNOWN);

                   IWebservicesManager ws = net.bioclipse.webservices.Activator
                                      .getDefault().getJavaWebservicesManager();

                   try {
                       ws.queryPDB( pdbids, container );
                } catch ( BioclipseException e ) {
                    logger.error("Error downloading PDBs: " + e.getMessage());
                }

                   monitor.done();
                }
             });
        } catch ( InvocationTargetException e ) {
            logger.error("Error downloading PDBs: " + e.getMessage());
            return false;
        } catch ( InterruptedException e ) {
            logger.error("Cancelled downloading of PDBs.");
            return false;
        }
        

        return true;
    }

    /**
     * Store selections so we save to this IContainer
     */
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        
        setNeedsProgressMonitor( true );
        
        //If a resource is selected, create results here
        Object obj = selection.getFirstElement();
        if ( obj instanceof IFile ) {
            IFile file = (IFile) obj;
            container=file.getParent();
        }
        if ( obj instanceof IContainer ) {
            container=(IContainer) obj;
        }
    }
    
    
    
}
