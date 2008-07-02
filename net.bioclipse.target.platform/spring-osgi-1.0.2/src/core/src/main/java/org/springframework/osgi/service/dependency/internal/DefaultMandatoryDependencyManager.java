/*
 * Copyright 2006-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.osgi.service.dependency.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ConcurrentMap;
import org.springframework.osgi.service.dependency.DependableServiceImporter;
import org.springframework.osgi.service.dependency.DependentServiceExporter;
import org.springframework.osgi.service.dependency.MandatoryDependencyEvent;
import org.springframework.osgi.service.dependency.MandatoryDependencyListener;
import org.springframework.osgi.service.dependency.ServiceDependency;
import org.springframework.osgi.util.internal.BeanFactoryUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Default implementation of {@link MandatoryServiceDependencyManager} which
 * determines the relationship between importers and exporters and unpublishes
 * exported service if they dependent, transitively, on imported OSGi services
 * that are mandatory and cannot be satisfied.
 * 
 * <strong>Note:</strong> aimed for singleton beans only
 * 
 * @author Costin Leau
 * 
 */
public class DefaultMandatoryDependencyManager implements MandatoryServiceDependencyManager, BeanFactoryAware,
		DisposableBean {

	private class ImportersListeners implements MandatoryDependencyListener {

		public void mandatoryDependencySatisfied(MandatoryDependencyEvent event) {
			boolean trace = log.isTraceEnabled();

			ServiceDependency importer = event.getServiceImporter();
			for (Iterator iter = ((List) importerToExportersDeps.get(importer)).iterator(); iter.hasNext();) {
				DependentServiceExporter exporter = (DependentServiceExporter) iter.next();

				// check the exporter exporterToImporterDeps
				// exporter.registerService();
				synchronized (exporter) {
					Map importers = (Map) exporterToImporterDeps.get(exporter);
					importers.put(event.getServiceImporter(), Boolean.TRUE);
					if (!importers.containsValue(Boolean.FALSE)) {
						exporter.start();

						if (trace)
							log.trace("exporter " + exporter
									+ " can be registered; all its exporterToImporterDeps are satisfied");
					}
				}
			}
		}

		public void mandatoryDependencyUnsatisfied(MandatoryDependencyEvent event) {
			boolean trace = log.isTraceEnabled();
			ServiceDependency importer = event.getServiceImporter();

			for (Iterator iter = ((List) importerToExportersDeps.get(importer)).iterator(); iter.hasNext();) {
				DependentServiceExporter exporter = (DependentServiceExporter) iter.next();
				if (trace)
					log.trace("exporter " + exporter + " is stopped; one of its exporterToImporterDeps disappeared");

				exporter.stop();

				synchronized (exporter) {
					Map importers = (Map) exporterToImporterDeps.get(exporter);
					importers.put(importer, Boolean.FALSE);
				}
			}
		}

	};

	private static final Log log = LogFactory.getLog(DefaultMandatoryDependencyManager.class);

	/** association of importers instances: name -> ServiceImporter instance */
	private final Map importers = CollectionFactory.createConcurrentMap(4);

	/** association of exporter instances: name -> ServiceExporter instance */
	private final Map exporters = CollectionFactory.createConcurrentMap(4);

	/** cache map - useful for avoiding double registration */
	private final ConcurrentMap importersSeen = CollectionFactory.createConcurrentMap(4);

	/** cache map - useful for avoiding double registration */
	private final ConcurrentMap exportersSeen = CollectionFactory.createConcurrentMap(4);

	private static final Object VALUE = new Object();

	/**
	 * map of exporterToImporterDeps between each importer and associated
	 * service
	 */
	/**
	 * the map contains as key the importers instances and as values, a list of
	 * exporter instances
	 */
	private final Map importerToExportersDeps = CollectionFactory.createConcurrentMap(8);

	/**
	 * Importers on which an exporter depends. The exporter instance is used as
	 * a key, while the value is represented by a list of importers name and
	 * their status (up or down).
	 */
	private final Map exporterToImporterDeps = CollectionFactory.createConcurrentMap(8);

	/** owning bean factory */
	private ConfigurableListableBeanFactory beanFactory;

	private final MandatoryDependencyListener importerListener = new ImportersListeners();

	public void addServiceExporter(String exporterBeanName) {
		Assert.hasText(exporterBeanName);

		if (exportersSeen.putIfAbsent(exporterBeanName, VALUE) == null) {

			String beanName = exporterBeanName;

			if (beanFactory.isFactoryBean(exporterBeanName))
				beanName = BeanFactory.FACTORY_BEAN_PREFIX + exporterBeanName;

			// check if it's factory bean (no need to check for abstract
			// definition since we're called by a BPP)
			if (!beanFactory.isSingleton(beanName))
				log.trace("exporter [" + beanName + "] is not singleton and will not be tracked");
			else {
				DependentServiceExporter exporter = (DependentServiceExporter) beanFactory.getBean(beanName);

				// disable publication at startup
				exporter.setPublishAtStartup(false);

				// populate the dependency maps
				discoverDependentImporterFor(exporterBeanName, exporter);
			}
		}
	}

	/**
	 * Discover all the importers for the given exporter. Since the importers
	 * are already created before the exporter instance is created, this method
	 * only does filtering based on the mandatory imports.
	 */
	protected void discoverDependentImporterFor(String exporterBeanName, DependentServiceExporter exporter) {

		boolean trace = log.isTraceEnabled();

		// add exporter if necessary
		exporters.put(exporterBeanName, exporter);

		// determine exporters
		String[] importerNames = BeanFactoryUtils.getTransitiveDependenciesForBean(beanFactory, exporterBeanName, true,
			DependableServiceImporter.class);

		// create associated exporters for this importer
		Map importers = new LinkedHashMap(importerNames.length);

		if (trace)
			log.trace("exporter [" + exporterBeanName + "] depends (transitively) on the following importers:"
					+ ObjectUtils.nullSafeToString(importerNames));

		// exclude non-singletons and non-mandatory importers
		for (int i = 0; i < importerNames.length; i++) {
			if (beanFactory.isSingleton(importerNames[i])) {
				DependableServiceImporter importer = (DependableServiceImporter) beanFactory.getBean(importerNames[i]);

				if (importer.isMandatory()) {
					importers.put(importer, importerNames[i]);

					// lock the entry
					synchronized (importerListener) {
						if (importersSeen.putIfAbsent(importer, importerNames[i]) == null) {
							importer.registerListener(importerListener);
						}
					}
				}

				else if (trace)
					log.trace("importer [" + importerNames[i] + "] is optional; skipping it");
			}
			else if (trace)
				log.trace("importer [" + importerNames[i] + "] is a non-singleton; ignoring it");
		}

		if (trace)
			log.trace("after filtering, exporter [" + exporterBeanName + "] depends on importers:" + importers.values());

		Collection imps = importers.keySet();

		// dependency between importers and exporter
		for (Iterator iter = imps.iterator(); iter.hasNext();) {
			DependableServiceImporter importer = (DependableServiceImporter) iter.next();
			// do entry locking since the map itself is already concurrent aware
			synchronized (importer) {
				List exporters = (List) importerToExportersDeps.get(importer);
				if (exporters == null)
					// start small
					exporters = new ArrayList(2);
				exporters.add(exporter);
				importerToExportersDeps.put(importer, exporters);
			}
		}

		// add the importers and their status to the collection
		synchronized (exporter) {
			Map importerStatuses = new LinkedHashMap(imps.size());

			for (Iterator iter = imps.iterator(); iter.hasNext();) {
				DependableServiceImporter imp = (DependableServiceImporter) iter.next();
				importerStatuses.put(imp, Boolean.valueOf(imp.isSatisfied()));
			}

			exporterToImporterDeps.put(exporter, importerStatuses);

			// if all dependencies are up, start the exporter
			if (!importerStatuses.containsValue(Boolean.FALSE))
				exporter.start();
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	public void destroy() {
		importers.clear();
		exporters.clear();
		importersSeen.clear();
		exportersSeen.clear();
	}
}
