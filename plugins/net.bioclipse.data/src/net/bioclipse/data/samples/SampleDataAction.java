package net.bioclipse.data.samples;

import java.lang.reflect.InvocationTargetException;

import java.util.Properties;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
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

public class SampleDataAction extends Action implements IIntroAction {
	
  private static final Logger logger = Logger.getLogger(SampleDataAction.class);

	private static final String SAMPLE_FEATURE_ID = "net.bioclipse.sampledata_feature";
	private static final String SAMPLE_FEATURE_VERSION = "2.0.0.B20090124";
//	private static final String UPDATE_SITE = "http://update2.bioclipse.net/";
	private static final String UPDATE_SITE = "file:///Users/ola/Workspaces/bioclipse2_1/bioclipse-updatesite/";
	private String sampleId;

	/**
	 *  Default constructor
	 */
	public SampleDataAction() {
	}

	/**
	 * Run action.
	 * Try to download and install sample feature if not present.
	 */
	public void run(IIntroSite site, Properties params) {
		
//		sampleId = params.getProperty("id"); //$NON-NLS-1$
//		if (sampleId == null)
//			return;
//
		Runnable r = new Runnable() {
			public void run() {
				
				//Install sample data project from data plugin
				
				
				if (!ensureSampleFeaturePresent()){
					logger.debug("Sample feature is installed");
				}else{
					logger.debug("Sample feature is NOT installed");
				}
				
				//TODO: do something about this

			}
		};

		Shell currentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		currentShell.getDisplay().asyncExec(r);
	}

	/**
	 * If sample not present, try to download it
	 * @return true if sample is present after possible downloading
	 */
	private boolean ensureSampleFeaturePresent() {
		if (checkFeature())
			return true;
		// the feature is not present - ask to download
		if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Download Sample Data?", "Extended Sample Data is not available. Would you like to download samples from bioclipse.net?")) {
			return downloadFeature();
		}
		return false;
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
	 */
	private boolean downloadFeature() {
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
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(op);
		} catch (InvocationTargetException e) {
			logger.debug(e);
			return false;
		} catch (InterruptedException e) {
			logger.debug(e);
		}
		return true;
	}
}
