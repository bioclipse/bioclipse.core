package net.bioclipse.core;

import java.net.URI;
import java.net.URISyntaxException;

import net.bioclipse.core.business.IMoleculeManager;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.recording.IHistory;
import net.bioclipse.recording.IRecordingAdvice;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.core";

    private static final long SERVICE_TIMEOUT_MILLIS = 10*1000;
    
    // The shared instance
    private static Activator plugin;
    
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    private ServiceTracker historyTracker;
    private ServiceTracker recordingAdviceTracker;
    private ServiceTracker moleculeManagerTracker;

    
    public Activator() {
    }

    
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        createVirtualProject();
        historyTracker 
            = new ServiceTracker( context, 
                                  IHistory.class.getName(), 
                                  null );
        historyTracker.open();
        recordingAdviceTracker 
            = new ServiceTracker( context, 
                                  IRecordingAdvice.class.getName(), 
                                  null );
        recordingAdviceTracker.open();
        moleculeManagerTracker 
            = new ServiceTracker( context,
                                  IMoleculeManager.class.getName(),
                                  null );
        moleculeManagerTracker.open();
        
    }
    
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        deleteVirtualProject();
    }

    
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    
    public IHistory getHistory() {
        IHistory history = null;
        try {
            history = (IHistory) historyTracker.waitForService(SERVICE_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Error getting History service: " + e);
            LogUtils.debugTrace(logger, e);
        }
        if(history == null) {
            throw new IllegalStateException("Could not get the history");
        }
        return history;
    }
    
    
    public IRecordingAdvice getRecordingAdvice() {
        IRecordingAdvice recordingAdvice = null;
        try {
            recordingAdvice = (IRecordingAdvice) 
                recordingAdviceTracker.waitForService(SERVICE_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Error getting RecordingAdvice service: " + e);
            LogUtils.debugTrace(logger, e);
        }
        if(recordingAdvice == null) {
            throw new IllegalStateException("Could not get the recordingAdvice");
        }
        return recordingAdvice;
    }


    public IMoleculeManager getMoleculeManager() {
        IMoleculeManager moleculeManager = null;
        try {
            Object service = moleculeManagerTracker
                             .waitForService( SERVICE_TIMEOUT_MILLIS );
            if (service instanceof IMoleculeManager) {
                moleculeManager = (IMoleculeManager)service;
            } else {
                logger.error( "Unexpected service type " +
                		      "(expected IMoleculeManager): " + 
                		      service.getClass().getName() );
            }
        }
        catch ( InterruptedException e ) {
            logger.error("Error getting MoleculeManager: " + e.getMessage(), e);
            LogUtils.debugTrace( logger, e );
        }
        if(moleculeManager == null) {
            throw new IllegalStateException("could not get moleculeManager");
        }
        return moleculeManager;
    }
    
    protected void createVirtualProject(){
        
        String projectName = "Virtual";
        IProject project = ResourcesPlugin.getWorkspace().
                getRoot().getProject(projectName);
        IProjectDescription description = 
               ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
        try{
            description.setLocationURI(new URI("memory:/Virtual"));
            project.create(description,null);
            project.open(null);
        }catch(URISyntaxException use){
            logger.debug(use.getMessage(),use);
        }catch(CoreException x){
            logger.warn("Failed to create virtual project: "+x.getMessage());
        }
    }
    protected void deleteVirtualProject(){
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject("Virtual");
        try {
            project.delete(true, null);
        } catch (CoreException e) {            
            e.printStackTrace();
        }
    }
}
