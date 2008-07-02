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
package org.springframework.osgi.service.importer.internal.aop;

import org.osgi.framework.ServiceReference;

/**
 * Simple interface passed around to decouple proxy creation (which is highly
 * contextual and configuration dependent) from the overall OSGi infrastructure
 * which is concerned with synchronization and events.
 * 
 * @author Costin Leau
 */
public interface ServiceProxyCreator {

	/**
	 * Create a service proxy for the given service reference. The proxy purpose
	 * is to transparently decouple the client from holding a strong reference
	 * to the service (which might go away) and provide various decorations.
	 * 
	 * @param reference service reference
	 * @return proxy on top of the given service reference
	 */
	Object createServiceProxy(ServiceReference reference);
}
