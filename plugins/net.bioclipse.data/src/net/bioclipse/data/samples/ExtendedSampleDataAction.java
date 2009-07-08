package net.bioclipse.data.samples;

import java.lang.reflect.InvocationTargetException;

import java.util.Properties;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration;
import org.eclipse.update.standalone.InstallCommand;
import org.osgi.framework.Version;

public class ExtendedSampleDataAction extends Action implements IIntroAction {
	
  private static final Logger logger = Logger.getLogger(ExtendedSampleDataAction.class);

	private static final String SAMPLE_FEATURE_ID = "net.bioclipse.sampledata_feature";
	private static final String SAMPLE_FEATURE_VERSION = "2.0.0.v20090707";
	private static final String UPDATE_SITE = "http://update2.bioclipse.net/";
//	private static final String UPDATE_SITE = "file:///Users/ola/Workspaces/bioclipse2_1/bioclipse-updatesite/";

	/**
	 *  Default constructor
	 */
	public ExtendedSampleDataAction() {
	}

	/**
	 * Run action.
	 * Try to download and install sample feature if not present.
	 */
	public void run(IIntroSite site, Properties params) {
		
		final IIntroSite psite=site;
		final Properties pparams=params;
		
		Runnable r = new Runnable() {
			public void run() {
				try {
					if (ensureSampleFeaturePresent()){
						logger.debug("Sample feature is or was already installed");
//						showMessage("Installation of sample data was successful");
						
						IIntroAction sampleDataAction=new SampleDataAction();
						sampleDataAction.run(psite, pparams);

					}else{
						//TODO: should not report this on cancel"
						logger.error("There was an error downloading sample data from update site.");
						showMessage("There was an error downloading sample data from update site.");
					}
				} catch (InvocationTargetException e) {
					logger.error("Sample data installation failed: " + e.getMessage());
					showError("Installation of sample data failed: " + e.getMessage());
				} catch (InterruptedException e) {
					logger.error("Sample data installation was interrupted.");
//					showMessage("Sample data installation was interrupted.");
				}

			}
		};

		Shell currentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		currentShell.getDisplay().asyncExec(r);
	}

	/**
	 * If sample not present, try to download it
	 * @return true if sample is present after possible downloading
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	private boolean ensureSampleFeaturePresent() throws InvocationTargetException, InterruptedException {
		if (checkFeature())
			return true;
		// the feature is not present - ask to download
		if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Download Sample Data?", 
				"Extended Sample Data is not available. " +
				"Would you like to download samples from bioclipse.net?")) {
			return downloadFeature();
		}
		logger.debug("Download extended samples cancelled.");
		throw new InterruptedException("Download extended samples cancelled.");
	}

	/**
	 * Check if already installed
	 * @return true if installed
	 */
	private boolean checkFeature() {
		IPlatformConfiguration config = ConfiguratorUtils.getCurrentPlatformConfiguration();
		IPlatformConfiguration.IFeatureEntry[] features = config.getConfiguredFeatureEntries();
//		Version sampleVersion = new Version(SAMPLE_FEATURE_VERSION);
		for (int i = 0; i < features.length; i++) {
			String id = features[i].getFeatureIdentifier();
			if (SAMPLE_FEATURE_ID.equals(id)) {
				String version = features[i].getFeatureVersion();
				Version fversion = Version.parseVersion(version);
				System.out.println("Existing version of sampledata installed: " + fversion);
				return true;
			}
		}
		return false;
	}

	/**
	 * Download feature from update site
	 * @return false if error, true if success or interrupted
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	private boolean downloadFeature() throws InvocationTargetException, InterruptedException {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					InstallCommand command = new InstallCommand(SAMPLE_FEATURE_ID, SAMPLE_FEATURE_VERSION, UPDATE_SITE, null, "false"); //$NON-NLS-1$
					command.run(monitor);
					command.applyChangesNow();
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		};
//		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
//		} catch (InvocationTargetException e) {
//			logger.debug(e);
//			return false;
//		} catch (InterruptedException e) {
//			logger.debug(e);
//		}
		return true;
	}
	
    private void showMessage(String message) {
        MessageDialog.openInformation(
        		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
        		 getShell(),
                "Sample Data Installation",
                	message);
    }

    private void showError(String message) {
        MessageDialog.openError(
        		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
        		 getShell(),
                "Sample Data Installation",
                	message);
    }

}
