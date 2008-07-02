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
package org.springframework.osgi.service.dependency;

/**
 * Interface describing the OSGi service exporters dependency contract.
 * 
 * @author Costin Leau
 */
public interface DependentServiceExporter {

	/**
	 * Instructs the exporter whether to publish the service at startup or not. This method
	 * is used internally inside the framework to prevent the exporter for
	 * publishing a service if it depends on importers with mandatory service
	 * import.
	 * 
	 * @param publish true if the exporter will publish things at startup, false
	 * otherwise
	 */
	void setPublishAtStartup(boolean publish);

	/**
	 * Starts the OSGi lifecycle.
	 * 
	 * Should not throw an exception if the component is already running.
	 */
	void start();

	/**
	 * Stops the OSGi lifecycle.
	 * 
	 * Should not throw an exception if the component is already stopped.
	 */
	void stop();

	/**
	 * Indicates whether this OSGi component is currently running.
	 * 
	 * @return true if the component is running, false otherwise
	 */
	boolean isRunning();
}
