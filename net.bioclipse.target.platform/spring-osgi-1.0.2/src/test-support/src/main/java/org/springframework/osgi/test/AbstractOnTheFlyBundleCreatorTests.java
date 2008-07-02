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

package org.springframework.osgi.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.objectweb.asm.ClassReader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.internal.storage.MemoryStorage;
import org.springframework.osgi.test.internal.util.DependencyVisitor;
import org.springframework.osgi.test.internal.util.JarCreator;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Enhanced subclass of {@link AbstractDependencyManagerTests} that facilitates
 * OSGi testing by creating at runtime, on the fly, a jar using the indicated
 * manifest and resource patterns (by default all files found under the root
 * path).
 * 
 * <p/>The test class can automatically determine the imports required by the
 * test, create the OSGi bundle manifest and pack the test and its resources in
 * a jar that can be installed inside an OSGi platform.
 * 
 * <p/> Note that in more complex scenarios, dedicated packaging tools (such as
 * ant scripts or maven2) should be used.
 * 
 * @author Costin Leau
 * 
 */
public abstract class AbstractOnTheFlyBundleCreatorTests extends AbstractDependencyManagerTests {

	private static final String META_INF_JAR_LOCATION = "/META-INF/MANIFEST.MF";

	JarCreator jarCreator;

	/** field used for caching jar content */
	private Map jarEntries;
	/** discovered manifest */
	private Manifest manifest;


	public AbstractOnTheFlyBundleCreatorTests() {
		initializeJarCreator();
	}

	public AbstractOnTheFlyBundleCreatorTests(String testName) {
		super(testName);
		initializeJarCreator();
	}

	private void initializeJarCreator() {
		jarCreator = new JarCreator();
		jarCreator.setStorage(new MemoryStorage());
	}

	/**
	 * Returns the root path used for locating the resources that will be packed
	 * in the test bundle (the root path does not become part of the jar).
	 * <p/>By default, the Maven2 test layout is used:
	 * <code>"file:./target/test-classes"</code>
	 * 
	 * @return root path given as a String
	 */
	protected String getRootPath() {
		return "file:./target/test-classes/";
	}

	/**
	 * Returns the patterns used for identifying the resources added to the jar.
	 * The patterns are added to the root path when performing the search. By
	 * default, the pattern is <code>*&#42;/*</code>.
	 * 
	 * <p/>In large test environments, performance can be improved by limiting
	 * the resource added to the bundle by selecting only certain packages or
	 * classes. This results in a small test bundle which is faster to create,
	 * deploy and install.
	 * 
	 * @return the patterns identifying the resources added to the jar
	 */
	protected String[] getBundleContentPattern() {
		return new String[] { JarCreator.EVERYTHING_PATTERN };
	}

	/**
	 * Returns the location (in Spring resource style) of the manifest location
	 * to be used. By default <code>null</code> is returned, indicating that
	 * the manifest should be picked up from the bundle content (if it's
	 * available) or be automatically created based on the test class imports.
	 * 
	 * @return the manifest location
	 * @see #getManifest()
	 * @see #createDefaultManifest()
	 */
	protected String getManifestLocation() {
		return null;
	}

	/**
	 * Returns the current test bundle manifest. The method tries to read the
	 * manifest from the given location; in case the location is
	 * <code>null</code> (default), it will search for
	 * <code>META-INF/MANIFEST.MF</code> file in jar content (as specified
	 * through the patterns) and, if it cannot find the file,
	 * <em>automatically</em> create a <code>Manifest</code> object
	 * containing default entries.
	 * 
	 * <p/> Subclasses can override this method to enhance the returned
	 * Manifest.
	 * 
	 * @return Manifest used for this test suite.
	 * 
	 * @see #createDefaultManifest()
	 */
	protected Manifest getManifest() {
		// return cached manifest
		if (manifest != null)
			return manifest;

		String manifestLocation = getManifestLocation();
		if (StringUtils.hasText(manifestLocation)) {
			logger.info("Using Manifest from specified location=[" + getManifestLocation() + "]");
			DefaultResourceLoader loader = new DefaultResourceLoader();
			manifest = createManifestFrom(loader.getResource(manifestLocation));
		}

		else {
			// set root path
			jarCreator.setRootPath(getRootPath());
			// add the content pattern
			jarCreator.setContentPattern(getBundleContentPattern());

			// see if the manifest already exists in the classpath
			// to resolve the patterns
			jarEntries = jarCreator.resolveContent();

			for (Iterator iterator = jarEntries.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				if (META_INF_JAR_LOCATION.equals(entry.getKey())) {
					logger.info("Using Manifest from the test bundle content=[/META-INF/MANIFEST.MF]");
					manifest = createManifestFrom((Resource) entry.getValue());
				}
			}
			// fallback to default manifest creation

			if (manifest == null) {
				logger.info("Automatically creating Manifest for the test bundle");
				manifest = createDefaultManifest();
			}
		}

		return manifest;
	}

	private Manifest createManifestFrom(Resource resource) {
		Assert.notNull(resource);
		try {
			return new Manifest(resource.getInputStream());
		}
		catch (IOException ex) {
			throw (RuntimeException) new IllegalArgumentException("cannot create manifest from " + resource).initCause(ex);
		}
	}

	/**
	 * Creates the default manifest in case none if found on the disk. By
	 * default, the imports are synthetised based on the test class bytecode.
	 * 
	 * @return default manifest for the jar created on the fly
	 */
	protected Manifest createDefaultManifest() {
		Manifest manifest = new Manifest();
		Attributes attrs = manifest.getMainAttributes();

		// manifest versions
		attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		attrs.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");

		String description = getName() + "-" + getClass().getName();
		// name/description
		attrs.putValue(Constants.BUNDLE_NAME, "TestBundle-" + description);
		attrs.putValue(Constants.BUNDLE_SYMBOLICNAME, "TestBundle-" + description);
		attrs.putValue(Constants.BUNDLE_DESCRIPTION, "on-the-fly test bundle");

		// activator
		attrs.putValue(Constants.BUNDLE_ACTIVATOR, JUnitTestActivator.class.getName());

		// add Import-Package entry
		addImportPackage(manifest);

		if (logger.isDebugEnabled())
			logger.debug("Created manifest:" + manifest.getMainAttributes().entrySet());
		return manifest;
	}

	private void addImportPackage(Manifest manifest) {
		String[] rawImports = determineImports(getClass());

		boolean trace = logger.isTraceEnabled();

		if (trace)
			logger.trace("Discovered raw imports " + ObjectUtils.nullSafeToString(rawImports));

		Collection specialImportsOut = eliminateSpecialPackages(rawImports);
		Collection imports = eliminatePackagesAvailableInTheJar(specialImportsOut);

		if (trace)
			logger.trace("Filtered imports are " + imports);

		manifest.getMainAttributes().putValue(Constants.IMPORT_PACKAGE,
			StringUtils.collectionToCommaDelimitedString(imports));
	}

	/**
	 * Eliminate 'special' packages (java.*, test framework internal and the
	 * class declaring package)
	 * 
	 * @param rawImports
	 * @return
	 */
	private Collection eliminateSpecialPackages(String[] rawImports) {
		String currentPckg = ClassUtils.classPackageAsResourcePath(getClass()).replace('/', '.');

		Set filteredImports = new LinkedHashSet(rawImports.length);
		Set eliminatedImports = new LinkedHashSet(4);

		for (int i = 0; i < rawImports.length; i++) {
			String pckg = rawImports[i];

			if (!(pckg.startsWith("java.") || pckg.startsWith("org.springframework.osgi.test.internal") || pckg.equals(currentPckg)))
				filteredImports.add(pckg);
			else
				eliminatedImports.add(pckg);
		}

		if (!eliminatedImports.isEmpty() && logger.isTraceEnabled())
			logger.trace("Eliminated special packages " + eliminatedImports);

		return filteredImports;
	}

	/**
	 * Eliminates imports for packages already included in the bundle. Works
	 * only if the jar content is known (variable 'jarEntries' set).
	 * 
	 * @param imports
	 * @return
	 */
	private Collection eliminatePackagesAvailableInTheJar(Collection imports) {
		// no jar entry present, bail out.
		if (jarEntries == null || jarEntries.isEmpty())
			return imports;

		Set filteredImports = new LinkedHashSet(imports.size());
		Collection eliminatedImports = new LinkedHashSet(2);

		Collection jarPackages = jarCreator.getContainedPackages();
		for (Iterator iterator = imports.iterator(); iterator.hasNext();) {
			String pckg = (String) iterator.next();
			if (jarPackages.contains(pckg))
				eliminatedImports.add(pckg);
			else
				filteredImports.add(pckg);
		}
		if (!eliminatedImports.isEmpty() && logger.isTraceEnabled())
			logger.trace("Eliminated packages already present in the bundle " + eliminatedImports);

		return filteredImports;
	}

	/**
	 * Determine imports by walking a class hierarchy until the current package
	 * is found. Currently, the parsers checks only the test class hierarchy
	 * available in the bundle. note that split packages are not supported.
	 * 
	 * 
	 * @return
	 */
	private String[] determineImports(Class clazz) {
		Assert.notNull(clazz, "a not-null class is required");

		boolean trace = logger.isTraceEnabled();

		String endPackage = ClassUtils.classPackageAsResourcePath(AbstractOnTheFlyBundleCreatorTests.class).replace(
			'/', '.');

		Set cumulatedPackages = new LinkedHashSet();

		// get contained packages to do matching on the test hierarchy
		Collection containedPackages = jarCreator.getContainedPackages();

		// make sure the collection package is valid
		boolean validPackageCollection = !containedPackages.isEmpty();

		String clazzPackage;

		// start parsing the test class hierarchy
		do {
			clazzPackage = ClassUtils.classPackageAsResourcePath(clazz).replace('/', '.');
			// check if the class package is inside this jar or not
			// parse the class only for available packages otherwise

			// if we don't have the package, add it
			if (validPackageCollection && !containedPackages.contains(clazzPackage)) {
				logger.trace("Package [" + clazzPackage + "] is NOT part of the test archive; adding an import for it");
				cumulatedPackages.add(clazzPackage);
			}

			// otherwise parse the class byte-code
			else {
				if (trace)
					logger.trace("Package [" + clazzPackage + "] is part of the test archive; parsing " + clazz
							+ " bytecode to determine imports...");
				cumulatedPackages.addAll(determineImportsForClass(clazz));
			}
			clazz = clazz.getSuperclass();
			// work until the testing framework packages are reached 
		} while (!endPackage.equals(clazzPackage));

		String[] packages = (String[]) cumulatedPackages.toArray(new String[cumulatedPackages.size()]);
		// sort the array
		Arrays.sort(packages);

		for (int i = 0; i < packages.length; i++) {
			packages[i] = packages[i].replace('/', '.');
		}

		return packages;
	}

	private Set determineImportsForClass(Class clazz) {
		Assert.notNull(clazz, "a not-null class is required");
		DependencyVisitor visitor = new DependencyVisitor();

		// find inner classes
		Set allClasses = new LinkedHashSet(4);

		allClasses.add(clazz);
		CollectionUtils.mergeArrayIntoCollection(clazz.getDeclaredClasses(), allClasses);

		for (Iterator iterator = allClasses.iterator(); iterator.hasNext();) {
			Class innerClazz = (Class) iterator.next();
			CollectionUtils.mergeArrayIntoCollection(innerClazz.getDeclaredClasses(), allClasses);
		}

		boolean trace = logger.isTraceEnabled();

		if (trace)
			logger.trace("Discovered classes to analyze " + allClasses);

		ClassReader reader;

		for (Iterator iterator = allClasses.iterator(); iterator.hasNext();) {
			Class classToVisit = (Class) iterator.next();
			try {
				if (trace)
					logger.trace("Visiting class " + classToVisit);
				reader = new ClassReader(clazz.getResourceAsStream(ClassUtils.getClassFileName(classToVisit)));
			}
			catch (Exception ex) {
				throw (RuntimeException) new IllegalArgumentException("cannot read class " + clazz).initCause(ex);
			}
			reader.accept(visitor, false);
		}

		return visitor.getPackages();
	}

	protected void postProcessBundleContext(BundleContext context) throws Exception {
		logger.debug("Post processing: creating test bundle");

		Resource jar;

		Manifest mf = getManifest();

		// if the jar content hasn't been discovered yet (while creating the manifest)
		// do so now
		if (jarEntries == null) {
			// set root path
			jarCreator.setRootPath(getRootPath());
			// add the content pattern
			jarCreator.setContentPattern(getBundleContentPattern());

			// use jar creator for pattern discovery
			jar = jarCreator.createJar(mf);
		}

		// otherwise use the cached resources
		else {
			jar = jarCreator.createJar(mf, jarEntries);
		}

		try {
			installAndStartBundle(context, jar);
		}
		catch (Exception e) {
			IllegalStateException ise = new IllegalStateException(
				"Unable to dynamically start generated unit test bundle");
			ise.initCause(e);
			throw ise;
		}

		// now do the delegation
		super.postProcessBundleContext(context);
	}

	private void installAndStartBundle(BundleContext context, Resource resource) throws Exception {
		// install & start
		Bundle bundle = context.installBundle("[onTheFly-test-bundle]" + ClassUtils.getShortName(getClass()) + "["
				+ hashCode() + "]", resource.getInputStream());

		String bundleString = OsgiStringUtils.nullSafeNameAndSymName(bundle);
		boolean debug = logger.isDebugEnabled();

		if (debug)
			logger.debug("Test bundle [" + bundleString + "] succesfully installed");
		bundle.start();
		if (debug)
			logger.debug("Test bundle [" + bundleString + "] succesfully started");
	}

}
