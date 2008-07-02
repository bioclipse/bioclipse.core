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

package org.springframework.osgi.test.internal.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.springframework.osgi.test.internal.holder.OsgiTestInfoHolder;
import org.springframework.util.ReflectionUtils;

/**
 * Utility class for running OSGi-JUnit tests.
 * 
 * @author Costin Leau
 * 
 */
public abstract class TestUtils {

	/**
	 * Serialize the test result using the given OutputStream.
	 * 
	 * @deprecated will be removed in 1.1.0 w/o a replacement. the test
	 * framework relies on classloading instead of serialization.
	 * 
	 * @param result
	 * @param stream
	 */
	public static void sendTestResult(TestResult result, ObjectOutputStream stream) {

		List errorList = new ArrayList();
		Enumeration errors = result.errors();
		while (errors.hasMoreElements()) {
			TestFailure failure = (TestFailure) errors.nextElement();
			errorList.add(failure.thrownException());
		}
		List failureList = new ArrayList();
		Enumeration failures = result.failures();
		while (failures.hasMoreElements()) {
			TestFailure failure = (TestFailure) failures.nextElement();
			failureList.add(failure.thrownException());
		}

		try {
			stream.writeObject(errorList);
			stream.writeObject(failureList);
			stream.flush();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Deserialize testResult from the given ObjectInputStream. The loaded
	 * failures/errors are added to the given testResult.
	 * 
	 * @deprecated will be removed in 1.1.0 w/o a replacement. the test
	 * framework relies on classloading instead of serialization.
	 * 
	 * @param testResult the test result to which the properties are added
	 * @param stream the stream used to load the test result
	 * @param test the test used for adding the TestResult errors/failures
	 */
	public static void receiveTestResult(TestResult testResult, Test test, ObjectInputStream stream) {
		// deserialize back the TestResult
		List errors;
		List failures;
		try {
			errors = (List) stream.readObject();
			failures = (List) stream.readObject();
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		// get errors
		for (Iterator iter = errors.iterator(); iter.hasNext();) {
			testResult.addError(test, (Throwable) iter.next());
		} // get failures
		for (Iterator iter = failures.iterator(); iter.hasNext();) {
			testResult.addFailure(test, (AssertionFailedError) iter.next());
		}
	}

	/**
	 * Clones the test result from a TestResult loaded through a different
	 * classloader.
	 * 
	 * @param source test result loaded through a different classloader
	 * @param destination test result reported to the outside framework
	 * @param test initial test used for bootstrapping the integration framework
	 * @return cloned test result
	 */
	public static TestResult cloneTestResults(OsgiTestInfoHolder source, TestResult destination, Test test) {
		// since we cannot cast, we have to use reflection

		//		List errors;
		//		List failures;
		//
		//		{
		//			Field errorsField = ReflectionUtils.findField(source.getClass(), "fErrors", Vector.class);
		//			ReflectionUtils.makeAccessible(errorsField);
		//			Field failuresField = ReflectionUtils.findField(source.getClass(), "fFailures", Vector.class);
		//			ReflectionUtils.makeAccessible(failuresField);
		//			try {
		//				errors = (List) errorsField.get(source);
		//				failures = (List) failuresField.get(source);
		//			}
		//			catch (IllegalAccessException iae) {
		//				throw (RuntimeException) new IllegalStateException("cannot access test results fields").initCause(iae);
		//			}
		//		}

		// get errors
		for (Iterator iter = source.getTestErrors().iterator(); iter.hasNext();) {
			destination.addError(test, (Throwable) iter.next());
		}

		// get failures
		// since failures are a special JUnit error, we have to clone the stack
		for (Iterator iter = source.getTestFailures().iterator(); iter.hasNext();) {
			Throwable originalFailure = (Throwable) iter.next();
			AssertionFailedError clonedFailure = new AssertionFailedError(originalFailure.getMessage());
			clonedFailure.setStackTrace(originalFailure.getStackTrace());
			destination.addFailure(test, clonedFailure);
		}

		return destination;
	}

	/**
	 * Utility method which extracts the information from a TestResult and
	 * stores it as primordial classes. This avoids the use of reflection when
	 * reading the results outside OSGi.
	 * 
	 * @param result
	 * @param holder
	 */
	public static void unpackProblems(TestResult result, OsgiTestInfoHolder holder) {
		Enumeration errors = result.errors();
		while (errors.hasMoreElements()) {
			TestFailure failure = (TestFailure) errors.nextElement();
			holder.addTestError(failure.thrownException());
		}
		Enumeration failures = result.failures();
		while (failures.hasMoreElements()) {
			TestFailure failure = (TestFailure) failures.nextElement();
			holder.addTestFailure(failure.thrownException());
		}
	}
}
