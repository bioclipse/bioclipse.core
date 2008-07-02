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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.osgi.service.dependency.DependentServiceExporter;

/**
 * BeanPostProcessor registered for detecting the dependency between service
 * importer and service exporters.
 * 
 * @author Costin Leau
 * 
 */
public class MandatoryDependencyBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	private static final Log log = LogFactory.getLog(MandatoryDependencyBeanPostProcessor.class);

	private MandatoryServiceDependencyManager manager;

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof DependentServiceExporter) {
			manager.addServiceExporter(beanName);
		}
		return bean;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		DefaultMandatoryDependencyManager manager = new DefaultMandatoryDependencyManager();
		manager.setBeanFactory(beanFactory);
		this.manager = manager;
	}

}
