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
package org.springframework.osgi.service.importer.support;

import java.util.Iterator;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.osgi.service.dependency.DependentServiceExporter;
import org.springframework.osgi.service.dependency.MandatoryDependencyEvent;
import org.springframework.osgi.service.dependency.MandatoryDependencyListener;
import org.springframework.osgi.service.dependency.internal.DefaultMandatoryDependencyManager;
import org.springframework.osgi.service.importer.support.AbstractDependableServiceImporter;

/**
 * @author Costin Leau
 * 
 */
public class OneExporterWithTwoMandatoryImportersTest extends TestCase {

	private ConfigurableListableBeanFactory bf;

	private DefaultMandatoryDependencyManager manager;

	private String importerAName = "importerA";

	private String importerBName = "importerB";

	private String exporterName = "exporter";

	private MockControl mc;

	private ServiceImporterMock importerA, importerB;

	private DependentServiceExporter exporter;

	private static boolean[] mockImporterSatisfied = new boolean[] { true };

	private static class ServiceImporterMock extends AbstractDependableServiceImporter {
		private final MandatoryDependencyEvent event = new MandatoryDependencyEvent(this);

		public void signalDependencyDown() {
			for (Iterator iter = getDepedencyListeners().iterator(); iter.hasNext();) {
				MandatoryDependencyListener listener = (MandatoryDependencyListener) iter.next();
				listener.mandatoryDependencyUnsatisfied(event);
			}
		}

		public void signalDependencyUp() {
			for (Iterator iter = getDepedencyListeners().iterator(); iter.hasNext();) {
				MandatoryDependencyListener listener = (MandatoryDependencyListener) iter.next();
				listener.mandatoryDependencySatisfied(event);
			}
		}

		public boolean isSatisfied() {
			return mockImporterSatisfied[0];
		}
	}

	protected void setUp() throws Exception {
		bf = new DefaultListableBeanFactory();
		manager = new DefaultMandatoryDependencyManager();
		importerA = new ServiceImporterMock();
		importerB = new ServiceImporterMock();

		mc = MockControl.createStrictControl(DependentServiceExporter.class);
		exporter = (DependentServiceExporter) mc.getMock();

		// register dependencies into the BF
		bf.registerSingleton(importerAName, importerA);
		bf.registerSingleton(importerBName, importerB);
		bf.registerSingleton(exporterName, exporter);

		bf.registerDependentBean(importerAName, exporterName);
		bf.registerDependentBean(importerBName, exporterName);

		manager.setBeanFactory(bf);
		manager.addServiceExporter(exporterName);

		// NOTE: reset this since the manager already does some call on the mock
		mc.reset();
	}

	protected void tearDown() throws Exception {
		bf.destroySingletons();
		bf = null;
		manager.destroy();
		manager = null;

		mc.verify();
		mc = null;
	}

	public void testPublishAtStartup() throws Exception {
		// reset manager
		manager.destroy();

		exporter.setPublishAtStartup(false);
		exporter.start();
		mc.replay();
		
		// check that the manager properly sets the exporter
		manager.addServiceExporter(exporterName);
	}

	public void testImporterComesUpAndDown() throws Exception {
		// should go down once
		exporter.stop();
		exporter.start();
		mc.replay();

		// importer goes down
		importerA.signalDependencyDown();
		// importer comes up
		importerA.signalDependencyUp();
	}

	public void testImporterSignalsTwice() throws Exception {

		// should go down once
		exporter.stop();
		exporter.stop();
		exporter.start();
		exporter.start();
		mc.replay();

		// signal down twice (from the same source)
		importerA.signalDependencyDown();
		importerA.signalDependencyDown();

		// signal up twice (from the same source)
		importerA.signalDependencyUp();
		importerA.signalDependencyUp();
	}

	public void testTwoImportersGoDownOnlyOneComesUp() throws Exception {
		exporter.stop();
		exporter.stop();
		mc.replay();

		// both importers go down
		importerA.signalDependencyDown();
		importerB.signalDependencyDown();

		// only one importer goes up
		importerA.signalDependencyUp();
	}

	public void testTwoImportersGoDownBothComeUp() throws Exception {
		exporter.stop();
		exporter.stop();

		exporter.start();
		mc.replay();

		// both importers go down
		importerA.signalDependencyDown();
		importerB.signalDependencyDown();

		// both come up
		importerA.signalDependencyUp();
		importerB.signalDependencyUp();
	}

	public void testTwoImportersGoDownAndUpRepeteadlyButBothComeUpEventually() throws Exception {
		exporter.stop();
		exporter.stop();
		exporter.start();
		mc.replay();

		// one goes down, the other goes up (was already up)
		importerA.signalDependencyDown();
		importerB.signalDependencyUp();

		// B goes down, A comes up
		importerB.signalDependencyDown();
		importerA.signalDependencyUp();

		// A signals up again (should not be possible)
		importerA.signalDependencyUp();

		// finally B is up
		importerB.signalDependencyUp();
	}
}
