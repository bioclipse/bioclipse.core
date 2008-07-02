package ${package}.impl;

import ${package}.Bean;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Local integration test (outside of OSGi).
 * @see BeanOsgiIntegrationTest for integration test inside OSGi.
 */
public class BeanIntegrationTest extends AbstractDependencyInjectionSpringContextTests {

	private Bean myBean;
	
	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/bundle-context.xml"};
	}
	
	public void setBean(Bean bean) {
	  this.myBean = bean;
	}
	
	public void testBeanIsABean() {
	  assertTrue(this.myBean.isABean());
	}

}
