/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.usermanager;

import net.bioclipse.usermanager.business.UserManagerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author jonalv
 *
 */
@RunWith(value=Suite.class)
@SuiteClasses( value = { UserContainerTest.class,
                         UserManagerTest.class } )
public class AllUsermanagerTestsSuite {

}
