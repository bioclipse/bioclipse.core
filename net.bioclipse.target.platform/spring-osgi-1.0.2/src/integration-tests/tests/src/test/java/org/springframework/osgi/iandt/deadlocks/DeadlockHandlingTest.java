package org.springframework.osgi.iandt.deadlocks;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.osgi.context.ConfigurableOsgiBundleApplicationContext;
import org.springframework.osgi.iandt.BaseIntegrationTest;

/**
 * @author Hal Hildebrand Date: Jun 5, 2007 Time: 9:10:11 PM
 */
public class DeadlockHandlingTest extends BaseIntegrationTest {

	// Specifically do not wait
	protected boolean shouldWaitForSpringBundlesContextCreation() {
		return false;
	}

	/**
	 * While it may appear that this test is doing nothing, what it is doing is
	 * testing what happens when the OSGi framework is shutdown while the
	 * Spring/OSGi extender is deadlocked. If all goes well, the test will
	 * gracefully end. If not, it will hang for quite a while.
	 */
	public void testErrorHandling() throws Exception {
		Resource errorResource = getLocator().locateArtifact("org.springframework.osgi",
			"org.springframework.osgi.iandt.deadlock", getSpringDMVersion());
		assertNotNull("bundle resource exists", errorResource);
		Bundle errorBundle = bundleContext.installBundle(errorResource.getURL().toExternalForm());
		assertNotNull("bundle exists", errorBundle);
		errorBundle.start();
		StringBuffer filter = new StringBuffer();

		filter.append("(&");
		filter.append("(").append(Constants.OBJECTCLASS).append("=").append(
			AbstractRefreshableApplicationContext.class.getName()).append(")");
		filter.append("(").append(ConfigurableOsgiBundleApplicationContext.APPLICATION_CONTEXT_SERVICE_PROPERTY_NAME);
		filter.append("=").append("org.springframework.osgi.iandt.deadlock").append(")");
		filter.append(")");
		ServiceTracker tracker = new ServiceTracker(bundleContext, bundleContext.createFilter(filter.toString()), null);

		try {
			tracker.open();

			AbstractRefreshableApplicationContext appContext = (AbstractRefreshableApplicationContext) tracker.waitForService(3000);
			assertNull("Deadlock context should not be published", appContext);
		}
		finally {
			tracker.close();
		}
	}
}