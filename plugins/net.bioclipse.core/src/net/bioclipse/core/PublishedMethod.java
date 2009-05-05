/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishedMethod {

    /**
     * Field that defines the list of comma-separated parameters, but only as
     * 'Type name'. This field must not describe what the parameters really
     * represent, only give the type and name. Examples:
     * <pre>
     * IMolecule molecule
     * IMolecule mol, boolean someOption
     * </pre>
     */
    String params() default "";

    /**
     * Summary of the method, describing the parameters as well as what the
     * method does.
     */
    String methodSummary();
}
