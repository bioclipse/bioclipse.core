package net.bioclipse.core.api;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private static final Logger logger = Logger.getLogger(Activator.class);
	
	// The shared instance
    private static Activator plugin;
	
	// Virtual project
    public static final String VIRTUAL_PROJECT_NAME = "Virtual";
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

    /**
     * @return
     */
    public static IProject getVirtualProject() {

        IWorkspaceRoot root=ResourcesPlugin.getWorkspace().getRoot();
        IProject project=root.getProject(VIRTUAL_PROJECT_NAME);
        try {
            if(!project.exists()){
                logger.debug("Inserting "+VIRTUAL_PROJECT_NAME+" into workspace");
                createVirtualProject(project);
            }
            if(!project.isOpen()) {
                try {
                    project.open( null );
                } catch ( CoreException e ) {
                    logger.debug( "Faild to open Virtual" );
                }
            }
        }catch( URISyntaxException e) {
            logger.debug( "Failed to create "+ VIRTUAL_PROJECT_NAME,e );
        } catch ( CoreException e ) {
            logger.warn( "Failed to create "+ VIRTUAL_PROJECT_NAME ,e);
        }
        return project;    }

    /**
     * @param project
     * @throws URISyntaxException 
     * @throws CoreException 
     */
    public static void createVirtualProject( IProject project ) throws URISyntaxException, CoreException {

        IProjectDescription description = 
            ResourcesPlugin.getWorkspace()
            .newProjectDescription(net.bioclipse.core.api.Activator.VIRTUAL_PROJECT_NAME);
        description.setLocationURI(new URI("memory:/Virtual"));
        project.create(description,null);
        project.refreshLocal( IResource.DEPTH_ZERO, null );
        project.open(null);
        
    }
    
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

}
