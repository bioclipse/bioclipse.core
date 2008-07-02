package org.springframework.osgi.extender.internal.dependencies.shutdown;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.exporter.OsgiServicePropertiesResolver;
import org.springframework.osgi.util.OsgiServiceReferenceUtils;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.util.ObjectUtils;

/**
 * Null safe service-based dependency sorter for bundles. Sorts bundles based on
 * their services using the following algorithm:
 * 
 * <ol>
 * <li> if two bundles are connected (transitively) then the bundle that exports
 * the service with lowest ranking id, will be considered lower. </li>
 * <li> if the ranks are equal, then the service id (which is guaranteed to be
 * unique) will be considered. </li>
 * <li> if the bundles are not related then they will be sorted based on their
 * symbolic name. </li>
 * </ol>
 * 
 * @author Hal Hildebrand
 * @author Andy Piper
 * @author Costin Leau
 */
public class BundleDependencyComparator implements Comparator, Serializable {

	private static final long serialVersionUID = -108354908478230663L;

	private static final Log log = LogFactory.getLog(BundleDependencyComparator.class);

	public int compare(Object a, Object b) {
		boolean trace = log.isTraceEnabled();

		Bundle bundle1 = (Bundle) a;
		Bundle bundle2 = (Bundle) b;

		if (trace)
			log.trace("comparing bundle1 [" + OsgiStringUtils.nullSafeNameAndSymName(bundle1) + "] w/ bundle2 ["
					+ OsgiStringUtils.nullSafeNameAndSymName(bundle2) + "]..");

		if (bundle1 == null) {
			if (bundle2 == null) {
				return 0;
			}
			else {
				// Sort nulls first
				return 1;
			}
		}
		else if (bundle2 == null) {
			// Sort nulls first
			return -1;
		}

		// At this point, we know that bundle1 and bundle2 are not null
		if (bundle1.equals(bundle2)) {
			return 0;
		}

		// At this point, bundle1 and bundle2 are not null and not equal, here
		// we
		// compare them to see which is "higher" in the dependency graph
		boolean b1Lower = references(bundle2, bundle1);
		boolean b2Lower = references(bundle1, bundle2);

		if (b1Lower && !b2Lower) {
			// b2->b1
			return 1;
		}
		else if (b2Lower && !b1Lower) {
			// b1->b2
			return -1;
		}
		// Deal with circular references and unrelated bundles.
		// both bundles refer to themselves
		// b2 -> b1
		// b1 -> b2
		int compare = compareUsingServiceRankingAndId(bundle1, bundle2);

		if (trace)
			log.trace("comparison based on service ranking/id was won by bundle " + (compare > 0 ? "1" : "2"));
		return compare;
	}

	/**
	 * Answer whether Bundle b is referenced by Bundle a or bundle A references
	 * Bundle B. This is the same as b -> a (i.e. a service owned by B is used
	 * by A).
	 */
	protected boolean references(Bundle a, Bundle b) {
		return references(a, b, new HashSet());
	}

	/**
	 * Answer whether Bundle b is transitively referenced by Bundle a
	 */
	protected boolean references(Bundle a, Bundle b, Set seen) {
		if (seen.contains(b)) {
			return false;
		}
		seen.add(b);
		ServiceReference[] services = b.getRegisteredServices();
		if (services == null) {
			return false;
		}
		for (int i = 0; i < services.length; i++) {
			// filter on spring managed services
			if (isSpringManagedService(services[i])) {
				Bundle[] referingBundles = services[i].getUsingBundles();
				if (referingBundles != null) {
					for (int j = 0; j < referingBundles.length; j++) {
						if (a.equals(referingBundles[j])) {
							return true;
						}
						else if (references(a, referingBundles[j], seen)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Simple method checking whether the given service reference points to a
	 * spring managed service or not. Checks for
	 * 
	 * @param reference reference to the OSGi service
	 * @return true if the service is spring managed, false otherwise
	 */
	private boolean isSpringManagedService(ServiceReference reference) {
		if (reference == null)
			return false;
		return (reference.getProperty(OsgiServicePropertiesResolver.BEAN_NAME_PROPERTY_KEY) != null);
	}

	private ServiceReference[] excludeNonSpringManagedServices(ServiceReference[] references) {
		if (ObjectUtils.isEmpty(references))
			return references;

		int count = 0;
		for (int i = 0; i < references.length; i++) {
			if (!isSpringManagedService(references[i]))
				references[i] = null;
			else
				count++;
		}

		if (count == references.length)
			return references;

		ServiceReference[] refs = new ServiceReference[count];
		int j = 0;
		for (int i = 0; i < references.length; i++) {
			if (references[i] != null) {
				refs[j] = references[i];
				j++;
			}
		}

		return refs;
	}

	/**
	 * Answer whether Bundle a is higher or lower depending on the ranking and
	 * id of its exported services. This is used as a tie-breaker for circular
	 * references.
	 */
	protected int compareUsingServiceRankingAndId(Bundle a, Bundle b) {
		ServiceReference[] aservices = excludeNonSpringManagedServices(a.getRegisteredServices());
		ServiceReference[] bservices = excludeNonSpringManagedServices(b.getRegisteredServices());

		boolean trace = log.isTraceEnabled();

		// this case should not occur
		if (ObjectUtils.isEmpty(aservices) && ObjectUtils.isEmpty(bservices)) {
			if (trace)
				log.trace("both services have 0 services; sorting based on id");
			return (int) (a.getBundleId() - b.getBundleId());
		}
		else if (aservices == null) {
			return -1;
		}
		else if (bservices == null) {
			return 1;
		}

		// Look for the *lowest* highest ranked service in each bundle
		// i.e. take a look at each bundle, find the highest ranking service
		// and compare it to the other bundle
		// this means that the service with the highest ranking service will
		// be shutdown last

		int aRank = findHighestServiceRanking(aservices);
		int bRank = findHighestServiceRanking(bservices);

		// since we are looking for the minimum, invert the substraction
		// (the higher bundle is the one with the lowest rank)
		if (aRank != bRank) {
			int compare = -(bRank - aRank);
			if (trace) {
				int min = (compare > 0 ? (int) bRank : (int) aRank);
				log.trace("sorting based on lowest-service-ranking won by bundle" + (compare > 0 ? "1" : "2")
						+ " w/ service id " + min);
			}

			return -(bRank - aRank);
		}

		// Look for the highest id in each bundle (i.e. started last).
		long aMaxId = findHighestServiceId(aservices);
		long bMaxId = findHighestServiceId(bservices);

		if (aMaxId != bMaxId) {
			int compare = (int) (bMaxId - aMaxId);
			if (trace) {
				int max = (compare > 0 ? (int) bMaxId : (int) aMaxId);
				log.trace("sorting based on highest-service-id won by bundle " + (compare > 0 ? "1" : "2")
						+ " w/ service id " + max);
			}

			return compare;
		}

		return (int) (a.getBundleId() - b.getBundleId());
	}

	/**
	 * Find the highest service ranking. This might come as unexpected however,
	 * since a bundle can export multiple services, we have to find the minimum
	 * between the maximum services in each bundle - i.e. the bundle with the
	 * highest service ranking will win.
	 * 
	 * @param refs
	 * @return
	 */
	private int findHighestServiceRanking(ServiceReference[] refs) {
		int maxRank = Integer.MIN_VALUE;
		for (int i = 0; i < refs.length; i++) {
			int currentRank = OsgiServiceReferenceUtils.getServiceRanking(refs[i]);
			if (currentRank > maxRank)
				maxRank = currentRank;
		}

		return maxRank;
	}

	private long findHighestServiceId(ServiceReference[] refs) {
		long maxId = Long.MIN_VALUE;
		for (int i = 0; i < refs.length; i++) {
			long currentId = OsgiServiceReferenceUtils.getServiceId(refs[i]);
			if (currentId > maxId)
				maxId = currentId;
		}

		return maxId;
	}
}