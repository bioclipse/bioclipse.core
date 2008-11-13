/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *     Jonathan Alvarsson
 *
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.business.test;

import java.lang.reflect.Method;

import net.bioclipse.cdk.jchempaint.business.IJChemPaintManager;
import net.bioclipse.cdk.jchempaint.business.JChemPaintManager;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.tests.AbstractManagerTest;

import org.junit.Assert;
import org.junit.Test;

public class JChemPaintManagerTest extends AbstractManagerTest {

    IJChemPaintManager cdk;

    //Do not use SPRING OSGI for this manager
    //since we are only testing the implementations of the manager methods
    public JChemPaintManagerTest() {
        cdk = new JChemPaintManager();
    }

    public IBioclipseManager getManager() {
        return cdk;
    }
    
    @Test public void testImplementsAllControllerHubMethods() throws Exception {
        Class hub = this.getClass().getClassLoader().loadClass("org.openscience.cdk.controller.IChemModelRelay");
        Assert.assertNotNull("Could not load the IChemModelRelay", hub);
        for (Method method : hub.getMethods()) {
            Method matchingMethod = getMatchingMethod(cdk.getClass(), method);
            Assert.assertNotNull(
                "The JChemPaintManager does not implement the IChemModelRelay method " +
                method.getName(), matchingMethod
            );
            Assert.assertEquals(
                "The IChemModelRelay method " + method.getName() + " must have the return " +
                "type " + method.getReturnType().getName(),
                method.getReturnType().getName(),
                matchingMethod.getReturnType().getName()
            );
        }
    }
    
    private Method getMatchingMethod(Class clazz, Method searchedMethod) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(searchedMethod.getName())) {
                Class[] actualParams = method.getParameterTypes();
                Class[] searchedParams = searchedMethod.getParameterTypes();
                if (actualParams.length == searchedParams.length) {
                    boolean paramsMatch = true;
                    for (int i=0; i<actualParams.length && paramsMatch; i++) {
                        if (!actualParams[i].getName().equals(searchedParams[i].getName()))
                            paramsMatch = false;
                    }
                    if (paramsMatch) return method;
                }
            }
        }
        return null;
    }

}
