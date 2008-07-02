package org.springframework.osgi.compendium.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.OsgiServiceUtils;
import org.springframework.osgi.util.internal.MapBasedDictionary;
import org.springframework.util.Assert;

/**
 * Configuration admin trigger.
 * 
 * @author Hal Hildebrand
 */
public class OsgiConfig implements InitializingBean, BeanFactoryAware, BundleContextAware, DisposableBean {

	public static class ConfigListener {
		private String reference;

		private String updateMethod;

		private String deletedMethod;

		private Object bean;

		public void setReference(String reference) {
			this.reference = reference;
		}

		public void setUpdateMethod(String updateMethod) {
			this.updateMethod = updateMethod;
		}

		public void setDeletedMethod(String deletedMethod) {
			this.deletedMethod = deletedMethod;
		}

		void resolve(BeanFactory beanFactory, boolean isFactory) {
			bean = beanFactory.getBean(reference);
			if (isFactory) {
				try {
					bean.getClass().getMethod(updateMethod, new Class[] { String.class, Map.class });
				}
				catch (NoSuchMethodException e) {
					try {
						bean.getClass().getMethod(updateMethod, new Class[] { String.class, Dictionary.class });
					}
					catch (NoSuchMethodException e1) {
						IllegalArgumentException illArgEx = new IllegalArgumentException(
								"Invalid or missing update method for bean " + reference + "; requires signature "
										+ updateMethod + "(java.util.Dictionary) or " + updateMethod
										+ "(java.util.Map)");
						illArgEx.initCause(e);
						throw illArgEx;
					}
				}
			}
			else {
				try {
					bean.getClass().getMethod(updateMethod, new Class[] { Map.class });
				}
				catch (NoSuchMethodException e) {
					try {
						bean.getClass().getMethod(updateMethod, new Class[] { Dictionary.class });
					}
					catch (NoSuchMethodException e1) {
						IllegalArgumentException illArgEx = new IllegalArgumentException(
								"Invalid or missing update method for bean " + reference + "; requires signature "
										+ updateMethod + "(java.util.Dictionary) or " + updateMethod
										+ "(java.util.Map)");
						illArgEx.initCause(e);
						throw illArgEx;
					}
				}
			}
			if (deletedMethod != null) {
				try {
					bean.getClass().getMethod(deletedMethod, new Class[] { String.class });
				}
				catch (NoSuchMethodException e) {
					IllegalArgumentException illArgEx = new IllegalArgumentException(
							"Invalid or missing deleted method for bean " + reference + "; requires signature "
									+ deletedMethod + "(java.lang.String)");
					illArgEx.initCause(e);
					throw illArgEx;
				}
			}
		}

		void updated(String instancePid, Dictionary properties) throws ConfigurationException {
			Method update;
			MapBasedDictionary props = new MapBasedDictionary(properties);
			try {
				update = bean.getClass().getMethod(updateMethod, new Class[] { String.class, Map.class });
			}
			catch (NoSuchMethodException e) {
				try {
					update = bean.getClass().getMethod(updateMethod, new Class[] { String.class, Dictionary.class });
				}
				catch (NoSuchMethodException e1) {
					throw new ConfigurationException(instancePid, "Invalid or missing update method for bean "
							+ reference + "; requires signature " + updateMethod
							+ "(java.util.String, java.util.Dictionary) or " + updateMethod + "(java.util.Map)", e);
				}
			}

			try {
				update.invoke(bean, new Object[] { instancePid, props });
			}
			catch (IllegalAccessException e) {
				throw new ConfigurationException(instancePid, "Insufficient permission to invoke update method", e);
			}
			catch (InvocationTargetException e) {
				throw new ConfigurationException(instancePid, "Error updating", e.getTargetException());
			}
		}

		void updated(Dictionary properties, String servicePid) throws ConfigurationException {
			Method update;
			MapBasedDictionary props = new MapBasedDictionary(properties);
			try {
				update = bean.getClass().getMethod(updateMethod, new Class[] { Map.class });
			}
			catch (NoSuchMethodException e) {
				try {
					update = bean.getClass().getMethod(updateMethod, new Class[] { Dictionary.class });
				}
				catch (NoSuchMethodException e1) {
					throw new ConfigurationException(servicePid, "Invalid or missing update method for bean "
							+ reference + "; requires signature " + updateMethod + "(java.util.Dictionary) or "
							+ updateMethod + "(java.util.Map)", e);
				}
			}

			try {
				update.invoke(bean, new Object[] { props });
			}
			catch (IllegalAccessException e) {
				throw new ConfigurationException(servicePid, "Insufficient permission to invoke update method", e);
			}
			catch (InvocationTargetException e) {
				throw new ConfigurationException(servicePid, "Error updating", e.getTargetException());
			}
		}

		void deleted(String instancePid) {
			if (deletedMethod == null) {
				return;
			}
			Method deleted;
			try {
				deleted = bean.getClass().getMethod(deletedMethod, new Class[] { String.class });
			}
			catch (NoSuchMethodException e) {
				throw (IllegalStateException) new IllegalStateException("Invalid or missing deleted method for bean "
						+ reference + "; requires signature " + deletedMethod + "(java.lang.String)").initCause(e);
			}

			try {
				deleted.invoke(bean, new Object[] { instancePid });
			}
			catch (IllegalAccessException e) {
				throw (IllegalStateException) new IllegalStateException(
						"Insufficient permission to invoke deleted method").initCause(e);
			}
			catch (InvocationTargetException e) {
				throw (IllegalStateException) new IllegalStateException("Error deleting").initCause(e.getTargetException());
			}
		}
	}

	private class OsgiManagedServiceUpdater implements ManagedService {
		public void updated(Dictionary properties) throws ConfigurationException {
			for (Iterator l = listeners.iterator(); l.hasNext();) {
				ConfigListener listener = (ConfigListener) l.next();
				listener.updated(properties, pid);
			}

		}
	}

	private class OsgiManagedServiceFactoryUpdater implements ManagedServiceFactory {
		public void deleted(String instancePid) {
			for (Iterator l = listeners.iterator(); l.hasNext();) {
				ConfigListener listener = (ConfigListener) l.next();
				listener.deleted(instancePid);
			}
		}

		public String getName() {
			return "Managed service factory updater for: [" + pid + "]";
		}

		public void updated(String instancePid, Dictionary properties) throws ConfigurationException {
			for (Iterator l = listeners.iterator(); l.hasNext();) {
				ConfigListener listener = (ConfigListener) l.next();
				listener.updated(instancePid, properties);
			}
		}
	}

	private String pid;

	private List listeners;

	private BeanFactory beanFactory;

	private boolean factory = false;

	private BundleContext bundleContext;

	private ServiceRegistration registration;

	private static final Log log = LogFactory.getLog(OsgiConfig.class);

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(pid, "persistence id property is required");
		for (Iterator l = listeners.iterator(); l.hasNext();) {
			ConfigListener listener = (ConfigListener) l.next();
			listener.resolve(beanFactory, factory);
		}
		Dictionary props = new MapBasedDictionary();
		props.put(Constants.SERVICE_PID, pid);

		if (factory) {
			registration = bundleContext.registerService(ManagedServiceFactory.class.getName(), new OsgiManagedServiceFactoryUpdater(),
				props);
		}
		else {
			registration = bundleContext.registerService(ManagedService.class.getName(), new OsgiManagedServiceUpdater(), props);
		}
	}

	public void destroy() throws Exception {
		OsgiServiceUtils.unregisterService(registration);
	}

	public void setBundleContext(BundleContext context) {
		bundleContext = context;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setFactory(boolean factory) {
		this.factory = factory;
	}

}
