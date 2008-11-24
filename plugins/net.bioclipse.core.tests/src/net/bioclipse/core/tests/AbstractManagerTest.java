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
import net.bioclipse.core.jobs.Job;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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
                (parameters[0].getName().equals(IFile.class.getName()))) {
                // OK, found a foo(IFile)
                if (isRecorded(method)) {
                    // OK, IF applies, now test the THEN
                    boolean foundMatchingStringMethod = false;
                    for (Method otherMethod : methods) {
                        if (otherMethod.getName().equals(method.getName())) {
                            Class[] otherParameters = method.getParameterTypes();
                            if (otherParameters.length == 1 &&
                                (otherParameters[0].getName().equals(String.class.getName()))) {
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
     * If a {@link IBioclipseManager} method <code>foo(IFile)</code> exists,
     * then there must also be a method in the matching
     * {@link BioclipseManager} which takes an IFile parameter, <code>foo(IFile)</code>,
     * or a method which takes an IFile, an IProgressMonitor and an IRunnable
     * parameter, <code>foo(IFile, IProgressMonitor, IRunnable)</code>.
     */
    @Test public void testForFooIFileImplementation() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            if (parameters.length == 1 &&
                (parameters[0].getName().equals(IFile.class.getName()))) {
                // OK, found a foo(IFile)
                boolean foundMatchingImplementation = false;
                Method[] otherMethods = manager.getClass().getMethods();
                for (Method otherMethod : methods) {
                    Class[] otherParameters = otherMethod.getParameterTypes();
                    if (otherParameters.length == 1 &&
                        (otherParameters[0].getName().equals(IFile.class.getName()))) {
                        foundMatchingImplementation = true;
                    } else if (otherParameters.length == 3 &&
                        (otherParameters[0].getName().equals(IFile.class.getName())) &&
                        (otherParameters[1].getName().equals(IProgressMonitor.class.getName())) &&
                        (otherParameters[2].getName().equals(Runnable.class.getName()))) {
                        foundMatchingImplementation = true;
                    }
                }
                Assert.assertTrue(
                    managerInterface.getName() + " method " + method.getName() +
                    "(IFile) does not have the required matching " +
                    manager.getClass().getName() + " method " +
                    method.getName() +"(IFile) or " +
                    method.getName() +"(IFile, IProgressMonitor, IRunnable).",
                    foundMatchingImplementation
                );
            }
        }
    }

    /**
     * If a {@link IBioclipseManager} method <code>foo(IFile)</code>
     * annotated with {link Job} exists, then there must also be a method in the
     * matching {@link BioclipseManager} which takes an IFile, an IProgressMonitor
     * and an IRunnable parameter, <code>foo(IFile, IProgressMonitor, IRunnable)</code>.
     */
    @Test public void testForFooJobImplementations() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            if (isJob(method) && hasParameter(parameters, IFile.class)) {
                // OK, found a foo(X, IFile, Y)
                Class[] expectedParameters = new Class[parameters.length+2];
                int offset = 0;
                for (int i=0; i<parameters.length; i++) {
                    expectedParameters[i+offset] = parameters[i];
                    if (parameters[i].getName().equals(IFile.class.getName())) {
                        // replace IFile, by IFile, IProgressMonitor, Runnable
                        expectedParameters[i+offset+1] = IProgressMonitor.class;
                        expectedParameters[i+offset+2] = Runnable.class;
                        offset += 2;
                    }
                }
                Method matchingMethod = findMethod(
                    manager.getClass(), method.getName(), expectedParameters
                );
                Assert.assertNotNull(
                    managerInterface.getName() + " method " + method.getName() +
                    "(IFile) annotation with @Job does not have the required matching " +
                    manager.getClass().getName() + " method " +
                    method.getName() +"(IFile, IProgressMonitor, IRunnable).",
                    matchingMethod
                );
            }
        }
    }

    /**
     * If a {@link IBioclipseManager} method <code>foo(IFile)</code>
     * annotated with {link Job} exists, then there must also be a method in the
     * matching {@link BioclipseManager} which takes an IFile, an IProgressMonitor
     * and an IRunnable parameter, <code>foo(IFile, IProgressMonitor, IRunnable)</code>.
     */
    @Test public void testForFooIFileReturnsVoid() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            Class[] parameters = method.getParameterTypes();
            if (parameters.length == 1 && isJob(method) &&
                (parameters[0].getName().equals(IFile.class.getName()))) {
                // OK, found a foo(IFile)
                Assert.assertTrue(
                    managerInterface.getName() + " method " + method.getName() +
                    "(IFile) annotation with @Job must return void",
                    method.getReturnType() == null
                );
            }
        }
    }

    /**
     * If a published method has parameters, e.g. <code>foo(IFile)</code>, then
     * the PublishedMethod annotation should have a filled out params field.
     */
    @Test public void testParameterHelp() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            if (isPublished(method)) {
                String parameterHelp = method.getAnnotation(PublishedMethod.class).params();
                Assert.assertNotNull(parameterHelp);
                Assert.assertNotNull(
                    managerInterface.getName() + " method " + method.getName() +
                    " has parameters, but does not provide help with the" +
                    " params field of PublishedMethod.",
                    parameterHelp.length() != 0
                );
            }
        }
    }

    /**
     * If a published method has parameters, e.g. <code>foo(IFile)</code>, then
     * the PublishedMethod annotation should have a filled out params field.
     */
    @Test public void testMethodDescription() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            if (isPublished(method)) {
                String sum = method.getAnnotation(PublishedMethod.class).methodSummary();
                Assert.assertNotNull(sum);
                Assert.assertFalse(
                    managerInterface.getName() + " method " + method.getName() +
                    " has no method summary defined by PublishedMethod's " +
                    "methodSummary field",
                    sum.length() == 0
                );
            }
        }
    }

    private Class getManagerInterface(IBioclipseManager manager) {
        Class[] interfaces = manager.getClass().getInterfaces();
        String managerName = manager.getClass().getName();
        String managerPkg = managerName.substring(0, managerName.lastIndexOf('.'));
        String managerClass = managerName.substring(managerPkg.length()+1);
        String expectedName = managerPkg + ".I" + managerClass;
        for (Class interfaz : interfaces) {
            if (interfaz.getName().equals(expectedName))
                return interfaz;
        }
        return null;
    }

    /**
     * Tests if the Method has {@link Job} annotation.
     */
    private boolean isJob(Method method) {
        Annotation[] otherAnnots = method.getAnnotations();
        for (Annotation annot : otherAnnots) {
            if (annot instanceof Job) {
                return true;
            }
        }
        return false;
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
    
    private boolean hasParameter(Class[] parameters, Class class1) {
        for (Class clazz : parameters) {
            if (clazz.getName().equals(class1.getName())) return true;
        }
        return false;
    }

    private Method findMethod(Class clazz, String methodName,
                              Class[] parameterTypes) {
        Method[] otherMethods = clazz.getMethods();
        for (Method otherMethod : otherMethods) {
            Class[] otherParameters = otherMethod.getParameterTypes();
            System.out.println("method: " + otherMethod.getName());
            if (otherParameters.length == parameterTypes.length) {
                // OK, at least of equal length
                if (hasIdenticalTypes(otherParameters, parameterTypes)) {
                    return otherMethod;
                }
            }
        }
        return null;
    }

    private boolean hasIdenticalTypes(Class[] otherParameters,
                                      Class[] parameterTypes) {
        for (int i=0; i<parameterTypes.length; i++) {
            System.out.println("otherParam: " + otherParameters[i].getName());
            System.out.println("param: " + parameterTypes[i].getName());
            if (!otherParameters[i].getName().equals(
                parameterTypes[i].getName())) return false;
        }
        return true;
    }

}
