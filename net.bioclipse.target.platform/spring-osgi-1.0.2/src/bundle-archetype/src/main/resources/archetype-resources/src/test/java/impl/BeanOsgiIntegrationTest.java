package ${package}.impl;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

/**
 * OSGi integration test (inside OSGi).
 * @see AbstractConfigurableBundleCreatorTests
 */
public class BeanOsgiIntegrationTest extends AbstractConfigurableBundleCreatorTests {

	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/*.xml"};
	}
	
	public void testOsgiBundleContext() {
	  assertNotNull(bundleContext);
	}

}