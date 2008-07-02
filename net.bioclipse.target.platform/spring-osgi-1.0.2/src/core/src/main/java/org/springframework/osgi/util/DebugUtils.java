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

package org.springframework.osgi.util;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.logging.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Utility class used for debugging exceptions in OSGi environment, such as
 * class loading errors.
 * 
 * The main entry point is
 * {@link #debugClassLoadingThrowable(Throwable, Bundle, Class[])} which will
 * try to determine the cause by trying to load the given interfaces using the
 * given bundle.
 * 
 * <p/> The debugging process can be potentially expensive.
 * 
 * @author Costin Leau
 * @author Andy Piper
 */
public abstract class DebugUtils {

	/** use degradable logger */
	private static final Log log = LogUtils.createLogger(DebugUtils.class);


	/**
	 * Tries to debug the cause of the {@link Throwable}s that can appear when
	 * loading classes in OSGi environments (for example when creating proxies).
	 * 
	 * <p/> This method will try to determine the class that caused the problem
	 * and to search for it in the given bundle or through the classloaders of
	 * the given classes.
	 * 
	 * It will look at the classes are visible by the given bundle on debug
	 * level and do a bundle discovery process on trace level.
	 * 
	 * The method accepts also an array of classes which will be used for
	 * loading the 'problematic' class that caused the exception on debug level.
	 * 
	 * @param loadingThrowable class loading {@link Throwable} (such as
	 * {@link NoClassDefFoundError} or {@link ClassNotFoundException})
	 * @param bundle bundle used for loading the classes
	 * @param classes (optional) array of classes that will be used for loading
	 * the problematic class
	 */
	public static void debugClassLoadingThrowable(Throwable loadingThrowable, Bundle bundle, Class[] classes) {

		String className = null;
		// NoClassDefFoundError
		if (loadingThrowable instanceof NoClassDefFoundError) {
			className = loadingThrowable.getMessage().replace('/', '.');
		}
		// ClassNotFound
		else if (loadingThrowable instanceof ClassNotFoundException) {
			className = loadingThrowable.getMessage().replace('/', '.');
		}

		if (className != null) {

			debugClassLoading(bundle, className, null);

			if (!ObjectUtils.isEmpty(classes) && log.isDebugEnabled()) {
				StringBuffer message = new StringBuffer();

				// Check out all the classes.
				for (int i = 0; i < classes.length; i++) {
					ClassLoader cl = classes[i].getClassLoader();
					String cansee = "cannot";
					if (ClassUtils.isPresent(className, cl))
						cansee = "can";
					message.append(classes[i] + " is loaded by " + cl + " which " + cansee + " see " + className);
				}
				log.debug(message);
			}
		}
	}

	/**
	 * Tries (through a best-guess attempt) to figure out why a given class
	 * could not be found. This method will search the given bundle and its
	 * classpath to determine the reason for which the class cannot be loaded.
	 * 
	 * <p/> This method tries to be effective especially when the dealing with
	 * {@link NoClassDefFoundError} caused by failure of loading transitive
	 * classes (such as getting a NCDFE when loading <code>foo.A</code>
	 * because <code>bar.B</code> cannot be found).
	 * 
	 * @param bundle the bundle to search for (and which should do the loading)
	 * @param className the name of the class that failed to be loaded in dot
	 * format (i.e. java.lang.Thread)
	 * @param rootClassName the name of the class that triggered the loading
	 * (i.e. java.lang.Runnable)
	 */
	public static void debugClassLoading(Bundle bundle, String className, String rootClassName) {
		boolean trace = log.isTraceEnabled();
		if (!trace)
			return;

		Dictionary dict = bundle.getHeaders();
		String bname = dict.get(Constants.BUNDLE_NAME) + "(" + dict.get(Constants.BUNDLE_SYMBOLICNAME) + ")";
		if (trace)
			log.trace("Could not find class [" + className + "] required by [" + bname + "] scanning available bundles");

		BundleContext context = OsgiBundleUtils.getBundleContext(bundle);
		String packageName = className.substring(0, className.lastIndexOf('.'));
		// Reject global packages
		if (className.indexOf('.') < 0) {
			if (trace)
				log.trace("Class is not in a package, its unlikely that this will work");
			return;
		}
		Version iversion = hasImport(bundle, packageName);
		if (iversion != null && context != null) {
			if (trace)
				log.trace("Class is correctly imported as version [" + iversion + "], checking providing bundles");
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getBundleId() != bundle.getBundleId()) {
					Version exported = checkBundleForClass(bundles[i], className, iversion);
					// Everything looks ok, but is the root bundle importing the
					// dependent class also?
					if (exported != null && exported.equals(iversion) && rootClassName != null) {
						for (int j = 0; j < bundles.length; j++) {
							Version rootexport = hasExport(bundles[j], rootClassName.substring(0,
								rootClassName.lastIndexOf('.')));
							if (rootexport != null) {
								// TODO -- this is very rough, check the bundle
								// classpath also.
								Version rootimport = hasImport(bundles[j], packageName);
								if (rootimport == null || !rootimport.equals(iversion)) {
									if (trace)
										log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundles[j])
												+ "] exports [" + rootClassName + "] as version [" + rootexport
												+ "] but does not import dependent package [" + packageName
												+ "] at version [" + iversion + "]");
								}
							}
						}
					}
				}
			}
		}
		if (hasExport(bundle, packageName) != null) {
			if (trace)
				log.trace("Class is exported, checking this bundle");
			checkBundleForClass(bundle, className, iversion);
		}
	}

	private static Version checkBundleForClass(Bundle bundle, String name, Version iversion) {
		String packageName = name.substring(0, name.lastIndexOf('.'));
		Version hasExport = hasExport(bundle, packageName);

		// log.info("Examining Bundle [" + bundle.getBundleId() + ": " + bname +
		// "]");
		// Check for version matching
		if (hasExport != null && !hasExport.equals(iversion)) {
			log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] exports [" + packageName
					+ "] as version [" + hasExport + "] but version [" + iversion + "] was required");
			return hasExport;
		}
		// Do more detailed checks
		String cname = name.substring(packageName.length() + 1) + ".class";
		Enumeration e = bundle.findEntries("/" + packageName.replace('.', '/'), cname, false);
		if (e == null) {
			if (hasExport != null) {
				URL url = checkBundleJarsForClass(bundle, name);
				if (url != null) {
					log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains [" + cname
							+ "] in embedded jar [" + url.toString() + "] but exports the package");
				}
				else {
					log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] does not contain ["
							+ cname + "] but exports the package");
				}
			}

			String root = "/";
			String fileName = packageName;
			if (packageName.lastIndexOf(".") >= 0) {
				root = root + packageName.substring(0, packageName.lastIndexOf(".")).replace('.', '/');
				fileName = packageName.substring(packageName.lastIndexOf(".") + 1).replace('.', '/');
			}
			Enumeration pe = bundle.findEntries(root, fileName, false);
			if (pe != null) {
				if (hasExport != null) {
					log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains package ["
							+ packageName + "] and exports it");
				}
				else {
					log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains package ["
							+ packageName + "] but does not export it");
				}

			}
		}
		// Found the resource, check that it is exported.
		else {
			if (hasExport != null) {
				log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains resource [" + cname
						+ "] and it is correctly exported as version [" + hasExport + "]");
				Class c = null;
				try {
					c = bundle.loadClass(name);
				}
				catch (ClassNotFoundException e1) {
					// Ignored
				}
				log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] loadClass [" + cname
						+ "] returns [" + c + "]");
			}
			else {
				log.trace("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains resource [" + cname
						+ "] but its package is not exported");
			}
		}
		return hasExport;
	}

	private static URL checkBundleJarsForClass(Bundle bundle, String name) {
		String cname = name.replace('.', '/') + ".class";
		for (Enumeration e = bundle.findEntries("/", "*.jar", true); e != null && e.hasMoreElements();) {
			URL url = (URL) e.nextElement();
			JarInputStream jin = null;
			try {
				jin = new JarInputStream(url.openStream());
				// Copy entries from the real jar to our virtual jar
				for (JarEntry ze = jin.getNextJarEntry(); ze != null; ze = jin.getNextJarEntry()) {
					if (ze.getName().equals(cname)) {
						jin.close();
						return url;
					}
				}
			}
			catch (IOException e1) {
				log.trace("Skipped " + url.toString() + ": " + e1.getMessage());
			}

			finally {
				if (jin != null) {
					try {
						jin.close();
					}
					catch (Exception ex) {
						// ignore it
					}
				}
			}

		}
		return null;
	}

	/**
	 * Get the version of a package import from a bundle.
	 * 
	 * @param bundle
	 * @param packageName
	 * @return
	 */
	private static Version hasImport(Bundle bundle, String packageName) {
		Dictionary dict = bundle.getHeaders();
		// Check imports
		String imports = (String) dict.get(Constants.IMPORT_PACKAGE);
		Version v = getVersion(imports, packageName);
		if (v != null) {
			return v;
		}
		// Check for dynamic imports
		String dynimports = (String) dict.get(Constants.DYNAMICIMPORT_PACKAGE);
		if (dynimports != null) {
			for (StringTokenizer strok = new StringTokenizer(dynimports, ","); strok.hasMoreTokens();) {
				StringTokenizer parts = new StringTokenizer(strok.nextToken(), ";");
				String pkg = parts.nextToken().trim();
				if (pkg.endsWith(".*") && packageName.startsWith(pkg.substring(0, pkg.length() - 2)) || pkg.equals("*")) {
					Version version = Version.emptyVersion;
					for (; parts.hasMoreTokens();) {
						String modifier = parts.nextToken().trim();
						if (modifier.startsWith("version")) {
							version = Version.parseVersion(modifier.substring(modifier.indexOf("=") + 1).trim());
						}
					}
					return version;
				}
			}
		}
		return null;
	}

	private static Version hasExport(Bundle bundle, String packageName) {
		Dictionary dict = bundle.getHeaders();
		return getVersion((String) dict.get(Constants.EXPORT_PACKAGE), packageName);
	}

	/**
	 * Get the version of a package name.
	 * 
	 * @param stmt
	 * @param packageName
	 * @return
	 */
	private static Version getVersion(String stmt, String packageName) {
		if (stmt != null) {
			for (StringTokenizer strok = new StringTokenizer(stmt, ","); strok.hasMoreTokens();) {
				StringTokenizer parts = new StringTokenizer(strok.nextToken(), ";");
				String pkg = parts.nextToken().trim();
				if (pkg.equals(packageName)) {
					Version version = Version.emptyVersion;
					for (; parts.hasMoreTokens();) {
						String modifier = parts.nextToken().trim();
						if (modifier.startsWith("version")) {
							String vstr = modifier.substring(modifier.indexOf("=") + 1).trim();
							if (vstr.startsWith("\""))
								vstr = vstr.substring(1);
							if (vstr.endsWith("\""))
								vstr = vstr.substring(0, vstr.length() - 1);
							version = Version.parseVersion(vstr);
						}
					}
					return version;
				}
			}
		}
		return null;
	}
}
