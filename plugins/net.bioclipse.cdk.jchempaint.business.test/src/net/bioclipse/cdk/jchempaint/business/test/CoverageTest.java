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
package net.bioclipse.cdk.jchempaint.business.test;

import java.lang.reflect.Method;

import net.bioclipse.cdk.jchempaint.business.JChemPaintManager;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.TestClass;
import net.bioclipse.core.TestMethods;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 * 
 * @author egonw
 */
public class CoverageTest {
    
    private JChemPaintManager manager = new JChemPaintManager();
    
    /**
     * Tests if {@link PublishedMethod}'s are tested and annotated
     * with {@link TestMethod}.
     */
    @Test public void testCoverage() throws Exception {
        TestClass testClassAnnotation = getClassAnnotation();
        Assert.assertNotNull(
            "Class does not have TestClass annotation: " + manager.getClass().getName(),
            testClassAnnotation
        );
        String testClassName = testClassAnnotation.value();
        Class testClass = this.getClass().getClassLoader().loadClass(testClassName);
        Assert.assertNotNull("Could not load the test class: " + testClassName);
        checkPublishedMethods(testClass);
    }
    
    private void checkPublishedMethods(Class testClass) {
        int missingTestMethods = 0;
        for (Class<?> iface : manager.getClass().getInterfaces()) {
            for (Method method : iface.getMethods()) {
                if (method.getAnnotation(PublishedMethod.class) != null) {
                    // every published method should have one or more tests
                    if (method.getAnnotation(TestMethods.class) == null) {
                        System.out.println("Missing test annotation for: " + method.getName());
                        missingTestMethods++;
                    } else {
                        // now test if the listed test methods really exist
                        Method[] testClassMethods = testClass.getMethods();
                        TestMethods testMethodAnnotation = method.getAnnotation(TestMethods.class);
                        for (String testMethod : testMethodAnnotation.value().split(",")) {
                            boolean foundTestMethod = false;
                            for (Method testClassMethod : testClassMethods) {
                                if (testClassMethod.getName().equals(testMethod)) foundTestMethod = true;
                            }
                            Assert.assertTrue(
                                "Test method does not exist in test class: " + testMethod,
                                foundTestMethod
                            );
                        }
                    }
                }
            }
        }
        Assert.assertEquals("Missing test method: " + missingTestMethods, 0, missingTestMethods);
    }
    
    private TestClass getClassAnnotation() {
        for (Class<?> iface : manager.getClass().getInterfaces()) {
            TestClass testClass = iface.getAnnotation(TestClass.class);
            if (testClass != null) return testClass; 
        }
        return null;
    }

}
