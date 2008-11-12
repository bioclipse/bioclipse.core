/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.core.tests.coverage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.tests.AbstractManagerTest;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 * 
 * @author egonw
 */
public abstract class AbstractCoverageTest {
    
    abstract public IBioclipseManager getManager();
    
    /**
     * Tests if {@link PublishedMethod}'s are tested and annotated
     * with {@link TestMethod}.
     */
    @Test public void testCoverage() throws Exception {
        TestClasses testClassAnnotation = getClassAnnotation();
        Assert.assertNotNull(
            "Class does not have TestClasses annotation: " + getManager().getClass().getName(),
            testClassAnnotation
        );
        String testClassNames = testClassAnnotation.value();
        List<Class> testClasses = new ArrayList<Class>();
        for (String testClassName : testClassNames.split(",")) {
            Class testClass = this.getClass().getClassLoader().loadClass(testClassName);
            Assert.assertNotNull("Could not load the test class: " + testClassName, testClassName);
            testClasses.add(testClass);
        }
        checkPublishedMethods(testClasses);
    }
    
    /**
     * Tests if the {@link IBioclipseManager} extends {@link AbstractManagerTest}
     * as it should.
     */
    @Test public void testManagerExtendsAbstractManagerTest() throws Exception {
        TestClasses testClassAnnotation = getClassAnnotation();
        boolean extendsAbstractManagerTest = false;
        // at least one test class should extend it
        String testClassNames = testClassAnnotation.value();
        List<Class> testClasses = new ArrayList<Class>();
        for (String testClassName : testClassNames.split(",")) {
            Class testClass = this.getClass().getClassLoader().loadClass(testClassName);
            // should do this recursively, but for now let's just assume
            // the first superclass is the AbstractManagerTest
            Class superClass = testClass.getSuperclass();
            if (superClass.getName().equals(AbstractManagerTest.class.getName()))
                extendsAbstractManagerTest = true;
        }
        Assert.assertTrue(
            "At least one ManagerTest must extend AbstractManagerTest",
            extendsAbstractManagerTest
        );
    }

    private void checkPublishedMethods(List<Class> testClasses) {
        int missingTestMethodAnnotations = 0;
        String methodsMissingAnnotation = "";
        int missingTestMethods = 0;
        String testMethodsMissing = "";
        for (Class<?> iface : getManager().getClass().getInterfaces()) {
            for (Method method : iface.getMethods()) {
                if (method.getAnnotation(PublishedMethod.class) != null) {
                    // every published method should have one or more tests
                    if (method.getAnnotation(TestMethods.class) == null) {
                        missingTestMethodAnnotations++;
                        methodsMissingAnnotation += method.getName() + " ";
                    } else {
                        TestMethods testMethodAnnotation = method.getAnnotation(TestMethods.class);
                        for (String testMethod : testMethodAnnotation.value().split(",")) {
                            boolean foundTestMethod = checkIfATestClassContainsTheMethod(testClasses, testMethod);
                            if (!foundTestMethod) {
                                missingTestMethods++;
                                testMethodsMissing += testMethod + " ";
                            }
                        }
                    }
                }
            }
        }
        String message = "";
        if (missingTestMethodAnnotations > 0) {
            message += "Missing method annotations (" + missingTestMethodAnnotations +
                       "): " + methodsMissingAnnotation + "; ";
        }
        if (missingTestMethods > 0) {
            message += "Missing test methods (" + testMethodsMissing +
                       "): " + missingTestMethods + "; ";
        }
        Assert.assertFalse(message, message.length() > 0);
    }
    
    private boolean checkIfATestClassContainsTheMethod(List<Class> testClasses, String testMethod) {
        for (Class testClass : testClasses) {
            // now test if the listed test methods really exist
            Method[] testClassMethods = testClass.getMethods();
            for (Method testClassMethod : testClassMethods) {
                return true;
            }
        }
        return false;
    }

    private TestClasses getClassAnnotation() {
        for (Class<?> iface : getManager().getClass().getInterfaces()) {
            TestClasses testClass = iface.getAnnotation(TestClasses.class);
            if (testClass != null) return testClass; 
        }
        return null;
    }

}
