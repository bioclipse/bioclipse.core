package org.springframework.osgi.compendium.config;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.osgi.context.support.BundleContextAwareProcessor;
import org.springframework.osgi.mock.MockBundleContext;

/**
 * @author Hal Hildebrand
 *         Date: Nov 2, 2006
 *         Time: 10:26:53 AM
 */
public abstract class OsgiConfigNamespaceHandlerTest extends TestCase {
    private static final String MANAGED_SERVICE_PID = "man.from.mars";
    private static final String MANAGED_FACTORY_SERVICE_PID = "man.from.mars.eating.guitars";
    private static final String MANAGED_SERVICE = "managed.service";
    private static final String MANAGED_SERVICE_2 = "managed.service.2";
    private static final String MANAGED_SERVICE_FACTORY = "managed.service.factory";
    private static final String MANAGED_SERVICE_FACTORY_2 = "managed.service.factory.2";
    private static final String OSGI_CONFIG_TESTS_XML = "osgiConfigTests.xml";


    public void testManagedServices() throws Exception {
        MyMockBundleContext bundleContext = new MyMockBundleContext();
        bundleContext.getBundle();
        GenericApplicationContext appContext = new GenericApplicationContext();
        appContext.getBeanFactory().addBeanPostProcessor(new BundleContextAwareProcessor(bundleContext));

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(appContext);
        reader.loadBeanDefinitions(new ClassPathResource(OSGI_CONFIG_TESTS_XML, getClass()));
        appContext.refresh();

        ArrayList registeredServices = bundleContext.registeredServices;
        assertEquals(2, registeredServices.size());
        Object[] service1 = (Object[]) registeredServices.get(0);
        assertEquals(ManagedService.class.getName(), service1[0]);
        assertTrue(service1[1] instanceof ManagedService);
        assertEquals(MANAGED_SERVICE_PID, ((Dictionary) service1[2]).get(Constants.SERVICE_PID));

        Object[] service2 = (Object[]) registeredServices.get(1);
        assertEquals(ManagedServiceFactory.class.getName(), service2[0]);
        assertTrue(service2[1] instanceof ManagedServiceFactory);
        assertEquals(MANAGED_FACTORY_SERVICE_PID, ((Dictionary) service2[2]).get(Constants.SERVICE_PID));

        ManagedService updater1 = (ManagedService) service1[1];
        ManagedServiceFactory updater2 = (ManagedServiceFactory) service2[1];

        Hashtable props = new Hashtable();
        props.put("Now he only eats guitars", "eating cars and eating bars");

        String instancePid = "marvin";

        updater1.updated(props);
        updater2.updated(instancePid, props);
        updater2.deleted(instancePid);

        ManagedServiceListener serviceListener1 = (ManagedServiceListener) appContext.getBean(MANAGED_SERVICE);
        ManagedServiceListener serviceListener2 = (ManagedServiceListener) appContext.getBean(MANAGED_SERVICE_2);


        ManagedServiceFactoryListener serviceFactoryListener1 =
                (ManagedServiceFactoryListener) appContext.getBean(MANAGED_SERVICE_FACTORY);
        ManagedServiceFactoryListener serviceFactoryListener2 =
                (ManagedServiceFactoryListener) appContext.getBean(MANAGED_SERVICE_FACTORY_2);

        assertEquals(1, serviceListener1.notifications.size());
        assertEquals(props, serviceListener1.notifications.get(0));

        assertEquals(1, serviceListener2.notifications.size());
        assertEquals(props, serviceListener1.notifications.get(0));

        assertEquals(2, serviceFactoryListener1.notifications.size());
        Object[] notification = (Object[]) serviceFactoryListener1.notifications.get(0);
        assertEquals(instancePid, notification[0]);
        assertEquals(props, notification[1]);
        assertEquals(instancePid, serviceFactoryListener1.notifications.get(1));

        assertEquals(2, serviceFactoryListener2.notifications.size());
        notification = (Object[]) serviceFactoryListener2.notifications.get(0);
        assertEquals(instancePid, notification[0]);
        assertEquals(props, notification[1]);
        assertEquals(instancePid, serviceFactoryListener2.notifications.get(1));
    }


    private static class MyMockBundleContext extends MockBundleContext {
        public ArrayList registeredServices = new ArrayList();


        public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
            registeredServices.add(new Object[]{clazz, service, properties});
            return super.registerService(clazz, service, properties);
        }
    }
}
