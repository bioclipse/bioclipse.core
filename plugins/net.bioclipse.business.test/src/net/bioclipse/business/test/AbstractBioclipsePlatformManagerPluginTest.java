/*******************************************************************************
 * Copyright (c) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.business.test;

import static org.junit.Assert.*;
import net.bioclipse.business.IBioclipsePlatformManager;
import net.bioclipse.core.business.BioclipseException;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractBioclipsePlatformManagerPluginTest {

    protected static IBioclipsePlatformManager bioclipse;

    @Test public void testIsOnline() {
        Assert.assertTrue(bioclipse.isOnline());
    }

    @Test public void testDownload() throws Exception {
    	String downloadedContent = bioclipse.download(
    		"http://www.bioclipse.net"
    	);
        Assert.assertTrue(
        	downloadedContent.contains("Bioclipse")
        );
    }

    @Test public void testRequireVersion() throws Exception {

        System.setProperty( "eclipse.buildId", "2.2.0.testRunning" );

        bioclipse.requireVersion("2.2");
        bioclipse.requireVersion("2.2.0");
        bioclipse.requireVersion("2.2.0.testRunning");

        try {
            bioclipse.requireVersion("999.999");
            fail("Should throw exception");
        }
        catch ( BioclipseException e ) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion("2.4.1.testRunning");
            fail("Should throw exception");
        }
        catch (BioclipseException e) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion("999.999.999.b");
            fail("Should throw exception");
        }
        catch(BioclipseException e) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion("999.999.999");
            fail("Should throw exception");
        }
        catch(BioclipseException e) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion("2.4.1");
            fail("Should throw exception");
        }
        catch(BioclipseException e) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion( "2" );
            fail("Expected BioclipseException");
        }
        catch ( BioclipseException e ) {
            // This is what we want
        }

        try {
            bioclipse.requireVersion( "a.b.c" );
            fail("Expected BioclipseException");
        }
        catch ( BioclipseException e ) {
            // This is what we want
        }
    }

    @Test public void testRequireVersionTwoParamaters() throws Exception {

        System.setProperty( "eclipse.buildId", "2.2.0.testRunning" );

        bioclipse.requireVersion("2.2", "2.3");
        try {
            bioclipse.requireVersion("998.999", "999.999");
            fail("Should throw exception");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        bioclipse.requireVersion("2.2.0", "2.2.1");
        try {
            bioclipse.requireVersion("2.4.1", "2.5.1");
            fail("Expected BioclipseException");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion("998.999.999", "999.999.999");
            fail("Expected BioclipseException");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion("999.99.99.a", "999.99.99.b");
            fail("Expected BioclipseException");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion("2.1.0.testRunning", "2.2.0.testRunning");
            fail("Expected BioclipseException");
        }
        catch (BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion( "2.2.0.testRunning",
                                      "2.2.0.testRunning" );
            fail("Expected BioclipseException");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion( "2.4.0.testRunning",
                                      "2.4.1.testRunning" );
            fail("Expected BioclipseException");
        }
        catch(BioclipseException e) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion( "2", "3" );
            fail("Expected BioclipseException");
        }
        catch ( BioclipseException e ) {
            // This is what we want
        }
        try {
            bioclipse.requireVersion( "a.b.c", "c.d.e" );
            fail("Expected BioclipseException");
        }
        catch ( BioclipseException e ) {
            // This is what we want
        }
    }
}
