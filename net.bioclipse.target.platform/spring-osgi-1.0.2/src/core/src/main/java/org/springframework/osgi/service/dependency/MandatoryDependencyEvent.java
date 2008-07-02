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

import java.util.EventObject;

/**
 * Mandatory dependency event.
 * 
 * @author Costin Leau
 * 
 */
public class MandatoryDependencyEvent extends EventObject {

	private static final long serialVersionUID = -5653734359406600399L;

	/**
	 * Constructs a new <code>MandatoryDependencyEvent</code> instance.
	 *
	 * @param source event source
	 */
	public MandatoryDependencyEvent(ServiceDependency source) {
		super(source);
	}

	/**
	 * Returns the source of the event.
	 * 
	 * @return the source of the event
	 */
	public ServiceDependency getServiceImporter() {
		return (ServiceDependency) super.getSource();
	}

}
