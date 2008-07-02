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

package org.springframework.osgi.io;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.osgi.io.internal.OsgiResourceUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * OSGi-aware {@link ResourcePatternResolver}.
 * 
 * Can find resources in the <em>bundle jar</em> and <em>bundle space</em>.
 * See {@link OsgiBundleResource} for more information.
 * 
 * <p/> <strong>Note:</strong> <code>classpath:</code> and
 * <code>classpath*:</code> prefixes are not (yet) supported as there are no
 * methods for doing classpath discovery. A future version might add such
 * functionality.
 * 
 * @see Bundle
 * @see OsgiBundleResource
 * 
 * @author Costin Leau
 * 
 */
public class OsgiBundleResourcePatternResolver extends PathMatchingResourcePatternResolver {

	/**
	 * Our own logger to protect against incompatible class changes.
	 */
	private static final Log logger = LogFactory.getLog(OsgiBundleResourcePatternResolver.class);

	/**
	 * The bundle on which this resolver works on.
	 */
	private Bundle bundle;

	private static final String FOLDER_SEPARATOR = "/";

	private static final String FOLDER_WILDCARD = "**";


	public OsgiBundleResourcePatternResolver(Bundle bundle) {
		this(new OsgiBundleResourceLoader(bundle));
	}

	public OsgiBundleResourcePatternResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
		if (resourceLoader instanceof OsgiBundleResourceLoader) {
			this.bundle = ((OsgiBundleResourceLoader) resourceLoader).getBundle();

		}
	}

	public Resource[] getResources(String locationPattern) throws IOException {
		Assert.notNull(locationPattern, "Location pattern must not be null");
		int type = OsgiResourceUtils.getSearchType(locationPattern);

		// look for patterns
		if (getPathMatcher().isPattern(locationPattern)) {
			if (type == OsgiResourceUtils.PREFIX_TYPE_CLASS_SPACE)
				throw new IllegalArgumentException("pattern matching is unsupported for class space lookups");
			return findPathMatchingResources(locationPattern, type);
		}
		// even though we have no pattern
		// the OSGi space can return multiple entries for the same resource name
		// - treat this case below
		else {
			Resource[] result = null;
			// consider bundle-space which can return multiple URLs
			if (type == OsgiResourceUtils.PREFIX_TYPE_NOT_SPECIFIED
					|| type == OsgiResourceUtils.PREFIX_TYPE_BUNDLE_SPACE) {
				OsgiBundleResource resource = new OsgiBundleResource(bundle, locationPattern);
				URL[] urls = resource.getAllUrlsFromBundleSpace(locationPattern);
				result = OsgiResourceUtils.convertURLArraytoResourceArray(urls);
			}

			else if (type == OsgiResourceUtils.PREFIX_TYPE_CLASS_SPACE) {
				// remove prefix
				String location = OsgiResourceUtils.stripPrefix(locationPattern);
				result = OsgiResourceUtils.convertURLEnumerationToResourceArray(bundle.getResources(location));
			}

			// check whether we found something or we should fallback to a
			// non-existing resource
			if (ObjectUtils.isEmpty(result)) {
				result = new Resource[] { getResourceLoader().getResource(locationPattern) };
			}

			return result;
		}
	}

	/**
	 * Override it to pass in the searchType parameter.
	 */
	private Resource[] findPathMatchingResources(String locationPattern, int searchType) throws IOException {
		String rootDirPath = determineRootDir(locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		Resource[] rootDirResources = getResources(rootDirPath);

		Set result = new LinkedHashSet(16);
		for (int i = 0; i < rootDirResources.length; i++) {
			Resource rootDirResource = rootDirResources[i];
			if (isJarResource(rootDirResource)) {
				result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
			}
			else {
				result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern, searchType));
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Resolved location pattern [" + locationPattern + "] to resources " + result);
		}
		return (Resource[]) result.toArray(new Resource[result.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p/> Overrides the default check up since computing the URL can be fairly
	 * expensive operation as there is no caching (due to the framework dynamic
	 * nature).
	 */
	protected boolean isJarResource(Resource resource) throws IOException {
		if (resource instanceof OsgiBundleResource) {
			// check the resource type
			OsgiBundleResource bundleResource = (OsgiBundleResource) resource;
			// if it's known, then it's not a jar
			if (bundleResource.getSearchType() != OsgiResourceUtils.PREFIX_TYPE_UNKNOWN) {
				return false;
			}
			// otherwise the normal parsing occur
		}
		return super.isJarResource(resource);
	}
	
	/**
	 * Based on the search type, use the appropriate method
	 * 
	 * @see OsgiBundleResource#BUNDLE_URL_PREFIX
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources(java.lang.String)
	 */

	private Set doFindPathMatchingFileResources(Resource rootDirResource, String subPattern, int searchType)
			throws IOException {

		String rootPath = null;

		if (rootDirResource instanceof OsgiBundleResource) {
			OsgiBundleResource bundleResource = (OsgiBundleResource) rootDirResource;
			rootPath = bundleResource.getPath();
			searchType = bundleResource.getSearchType();
		}
		else if (rootDirResource instanceof UrlResource) {
			rootPath = rootDirResource.getURL().getPath();
		}

		if (rootPath != null) {
			String cleanPath = OsgiResourceUtils.stripPrefix(rootPath);
			String fullPattern = cleanPath + subPattern;
			Set result = new LinkedHashSet(16);
			doRetrieveMatchingBundleEntries(bundle, fullPattern, cleanPath, result, searchType);
			return result;
		}
		else {
			return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
		}
	}

	/**
	 * Seach each level inside the bundle for entries based on the search
	 * strategy chosen.
	 * 
	 * @param bundle the bundle to do the lookup
	 * @param fullPattern matching pattern
	 * @param dir directory inside the bundle
	 * @param result set of results (used to concatenate matching sub dirs)
	 * @param searchType the search strategy to use
	 * @throws IOException
	 */
	private void doRetrieveMatchingBundleEntries(Bundle bundle, String fullPattern, String dir, Set result,
			int searchType) throws IOException {

		Enumeration candidates;

		switch (searchType) {
			case OsgiResourceUtils.PREFIX_TYPE_NOT_SPECIFIED:
			case OsgiResourceUtils.PREFIX_TYPE_BUNDLE_SPACE:
				// returns an enumeration of URLs
				candidates = bundle.findEntries(dir, null, false);
				break;
			case OsgiResourceUtils.PREFIX_TYPE_BUNDLE_JAR:
				// returns an enumeration of Strings
				candidates = bundle.getEntryPaths(dir);
				break;
			case OsgiResourceUtils.PREFIX_TYPE_CLASS_SPACE:
				// returns an enumeration of URLs
				throw new IllegalArgumentException("class space does not support pattern matching");
			default:
				throw new IllegalArgumentException("unknown searchType " + searchType);
		}

		// entries are relative to the root path - miss the leading /
		if (candidates != null) {
			boolean dirDepthNotFixed = (fullPattern.indexOf(FOLDER_WILDCARD) != -1);
			while (candidates.hasMoreElements()) {

				Object path = candidates.nextElement();
				String currPath;

				if (path instanceof String)
					currPath = handleString((String) path);
				else
					currPath = handleURL((URL) path);

				if (!currPath.startsWith(dir)) {
					// Returned resource path does not start with relative
					// directory:
					// assuming absolute path returned -> strip absolute path.
					int dirIndex = currPath.indexOf(dir);
					if (dirIndex != -1) {
						currPath = currPath.substring(dirIndex);
					}
				}
				if (currPath.endsWith(FOLDER_SEPARATOR)
						&& (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, FOLDER_SEPARATOR) < StringUtils.countOccurrencesOf(
							fullPattern, FOLDER_SEPARATOR))) {
					// Search subdirectories recursively: we manually get the
					// folders on only one level

					doRetrieveMatchingBundleEntries(bundle, fullPattern, currPath, result, searchType);
				}
				if (getPathMatcher().match(fullPattern, currPath)) {
					if (path instanceof URL)
						result.add(new UrlResource((URL) path));
					else
						result.add(new OsgiBundleResource(bundle, currPath));

				}
			}
		}
	}

	/**
	 * Handle candidates returned as URLs.
	 * 
	 * @param path
	 * @return
	 */
	private String handleURL(URL path) {
		return path.getPath();
	}

	/**
	 * Handle candidates returned as Strings.
	 * 
	 * @param path
	 * @return
	 */
	private String handleString(String path) {
		return FOLDER_SEPARATOR.concat(path);
	}
}
