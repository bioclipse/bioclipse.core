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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;

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

//    /**
//     * If a {@link IBioclipseManager} method <code>foo(IFile)</code>
//     * annotated with {link Job} exists, then there must also be a method in the
//     * matching {@link BioclipseManager} which takes an IFile, an IProgressMonitor
//     * and an IRunnable parameter, <code>foo(IFile, IProgressMonitor, IRunnable)</code>.
//     */
//    @Test public void testForFooJobImplementations() {
//        IBioclipseManager manager = getManager();
//        Class managerInterface = getManagerInterface(manager);
//        Method[] methods = managerInterface.getMethods();
//        for (Method method : methods) {
//            Class[] parameters = method.getParameterTypes();
//            if (isJob(method) && hasParameter(parameters, IFile.class)) {
//                // OK, found a foo(X, IFile, Y)
//                Class[] expectedParameters = expandParameter(
//                    parameters, IFile.class, new Class[]{
//                        IFile.class, IProgressMonitor.class, Runnable.class
//                    },
//                    -1
//                );
//                Method matchingMethod = findMethod(
//                    manager.getClass(), method.getName(), expectedParameters
//                );
//                Assert.assertNotNull(
//                    managerInterface.getName() + " method " + method.getName() +
//                    "(IFile) annotation with @Job does not have the required matching " +
//                    manager.getClass().getName() + " method " +
//                    method.getName() +"(IFile, IProgressMonitor, IRunnable).",
//                    matchingMethod
//                );
//            }
//        }
//    }

    /**
     * Replaces the parameters type <code>toExpand</code> into the parameter
     * array given by expandInto, but only at the given index. If that index
     * is -1, then all instances of <code>toExpand</code> will be replaced.
     */
    private Class[] expandParameter(Class[] currentParameters, Class toExpand,
                                    Class[] expandInto, int index) {
        Class[] expectedParameters =
            new Class[currentParameters.length + expandInto.length];
        int offset = 0;
        for (int i=0; i<currentParameters.length; i++) {
            expectedParameters[i+offset] = currentParameters[i];
            if (index == -1 || index == i) {
                if (currentParameters[i].getName().equals(toExpand.getName())) {
                    // replace IFile, by IFile, IProgressMonitor, Runnable
                    for (int j=0; j<expandInto.length; j++) {
                        expectedParameters[i+j+1] = expandInto[i];
                    }
                    offset += expandInto.length;
                }
            }
        }
        return expectedParameters;
    }

//    /**
//     * If a {@link IBioclipseManager} method <code>foo(IFile)</code>
//     * annotated with {link Job} exists, then the matching method in the
//     * matching {@link BioclipseManager} must return void.
//     */
//    @Test public void testForFooIFileReturnsVoid() {
//        IBioclipseManager manager = getManager();
//        Class managerInterface = getManagerInterface(manager);
//        Method[] methods = managerInterface.getMethods();
//        for (Method method : methods) {
//            Class[] parameters = method.getParameterTypes();
//            if (parameters.length == 1 && isJob(method) &&
//                (parameters[0].getName().equals(IFile.class.getName()))) {
//                // OK, found a foo(IFile)
//                Assert.assertTrue(
//                    managerInterface.getName() + " method " + method.getName() +
//                    "(IFile) annotation with @Job must return void",
//                    method.getReturnType() == null
//                );
//            }
//        }
//    }

//    /**
//     * If a {@link IBioclipseManager} method is annotated with {link Job}
//     * exists which takes a <code>String<code> as the last parameter, then
//     * there must exist a matching method in the {@link BioclipseManager} that
//     * takes the same parameters, but with the last parameter String
//     * parameter replaced by IFile, IProgressMonitor, Runnable.
//     *
//     * @see #testForFooJobImplementations
//     */
//    @Test public void testFooStringJobImplementation() {
//        IBioclipseManager manager = getManager();
//        Class managerInterface = getManagerInterface(manager);
//        Method[] methods = managerInterface.getMethods();
//        for (Method method : methods) {
//            if (isJob(method)) {
//                Class[] parameters = method.getParameterTypes();
//                int paramCount = parameters.length;
//                if (paramCount > 0 &&
//                    parameters[paramCount-1].getName().equals(String.class.getName())) {
//                    // then
//                    Class[] expectedParameters = expandParameter(
//                        parameters, String.class, new Class[]{
//                            IFile.class, IProgressMonitor.class, Runnable.class
//                        },
//                        paramCount-1
//                    );
//                    Method matchingMethod = findMethod(
//                        manager.getClass(), method.getName(), expectedParameters
//                    );
//                    Assert.assertNotNull(
//                        managerInterface.getName() + " method " + method.getName() +
//                        "(String) annotation with @Job does not have the required matching " +
//                        manager.getClass().getName() + " method " +
//                        method.getName() +"(IFile, IProgressMonitor, IRunnable).",
//                        matchingMethod
//                    );
//                }
//            }
//        }
//    }

    /**
     * If a published method has parameters, e.g. <code>foo(IFile)</code>, then
     * the PublishedMethod annotation should have a filled out params field.
     */
    @Test public void testParameterHelp() {
        IBioclipseManager manager = getManager();
        Class managerInterface = getManagerInterface(manager);
        Method[] methods = managerInterface.getMethods();
        for (Method method : methods) {
            if (isPublished(method) && method.getParameterTypes().length > 0) {
                String parameterHelp = method.getAnnotation(PublishedMethod.class).params();
                Assert.assertNotNull(parameterHelp);
                Assert.assertNotNull(
                    managerInterface.getName() + " method " + method.getName() +
                    " has parameters, but does not provide help with the" +
                    " params field of PublishedMethod.",
                    parameterHelp
                );
                Assert.assertTrue(
                    managerInterface.getName() + " method " + method.getName() +
                    " has parameters, but does the provided help is empty.",
                    parameterHelp.length() > 0
                );
                String[] parameters = parameterHelp.split(",");
                Assert.assertEquals(
                    "The parameter help does not match the number of " +
                    "parameters of " + managerInterface.getName() + " method " +
                    method.getName(),
                    method.getParameterTypes().length, parameters.length
                );
                Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)");
                for (int i=0; i<parameters.length; i++) {
                    // assume each parameter has the syntax "Type name"
                    String param = parameters[i];
                    Matcher matcher = pattern.matcher(param);
                    if (matcher.find()) {
                        String type = matcher.group(1);
                        String name = matcher.group(2);
                        Assert.assertTrue(
                            "The help for parameter " + i + " of the " +
                            method.getName() + " method does not" +
                            "adhere to the expected syntax \"Type " +
                            "name\", but is: " + param,
                            method.getParameterTypes()[i]
                                .getName().endsWith(type)
                        );
                    }
                }
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
