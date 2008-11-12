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
package net.bioclipse.core.tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests basic API patterns for stable {@link IBioclipseManager}. This class must
 * be extended by all manager test suites.
 * 
 * @author egonw
 */
public abstract class AbstractManagerTest {

    public abstract IBioclipseManager getManager();

    /**
     * If a {@link IBioclipseManager} method <code>foo(IFile)</code> is annotated as 
     * <code>@Recorded</code>, then there must also be a method in that
     * {@link IBioclipseManager} which takes an String parameter, <code>foo(String)</code>,
     * that is annotated with <code>@Published</code>
     * and <code>@Recorder</code>.
     */
    @Test public void testForFooString() {
        IBioclipseManager manager = getManager();
        Method[] methods = manager.getClass().getMethods();
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            if (parameters.length == 1 &&
                (parameters[0].getClass().getName().equals(IFile.class.getName()))) {
                // OK, found a foo(IFile)
                if (isRecorded(method)) {
                    // OK, IF applies, now test the THEN
                    boolean foundMatchingStringMethod = false;
                    for (Method otherMethod : methods) {
                        if (otherMethod.getName().equals(method.getName())) {
                            Class[] otherParameters = method.getParameterTypes();
                            if (otherParameters.length == 1 &&
                                (otherParameters[0].getClass().getName().equals(String.class.getName()))) {
                                foundMatchingStringMethod =
                                    isRecorded(otherMethod) && isPublished(otherMethod);
                            }
                        }
                    }
                    Assert.assertTrue(
                        "Recorder method foo(IFile) does not have the required matching" +
                        " recorded and published foo(String)",
                        foundMatchingStringMethod
                    );
                }
            }
        }
    }
    
    /**
     * Tests if the Method has {@link Recorded} annotation.
     */
    private boolean isRecorded(Method method) {
        Annotation[] otherAnnots = method.getAnnotations();
        for (Annotation annot : otherAnnots) {
            if (annot instanceof Recorded) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests if the Method has {@link Published} annotation.
     */
    private boolean isPublished(Method method) {
        Annotation[] otherAnnots = method.getAnnotations();
        for (Annotation annot : otherAnnots) {
            if (annot instanceof PublishedMethod) {
                return true;
            }
        }
        return false;
    }
    
}
