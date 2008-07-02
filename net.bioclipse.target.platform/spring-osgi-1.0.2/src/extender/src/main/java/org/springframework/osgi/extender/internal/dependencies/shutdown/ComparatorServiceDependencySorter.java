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
package org.springframework.osgi.extender.internal.dependencies.shutdown;

import java.util.Arrays;
import java.util.Comparator;

import org.osgi.framework.Bundle;

/**
 * Comparator based dependency sorter.
 * 
 * @author Costin Leau
 * 
 */
public class ComparatorServiceDependencySorter implements ServiceDependencySorter {

	private Comparator comparator = new BundleDependencyComparator();

	public Bundle[] computeServiceDependencyGraph(Bundle[] bundles) {
		Bundle[] bndls = new Bundle[bundles.length];
		System.arraycopy(bundles, 0, bndls, 0, bundles.length);
		Arrays.sort(bndls, comparator);
		return bndls;
	}
}
