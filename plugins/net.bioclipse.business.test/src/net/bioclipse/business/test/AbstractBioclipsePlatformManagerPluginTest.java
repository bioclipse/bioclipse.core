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
import net.bioclipse.core.api.BioclipseException;

import org.junit.Assert;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setVersion() {
        System.setProperty( "eclipse.buildId", "2.2.0.testRunning" );
    }

    @Test
    public void handleTwoThreeAndFourVersionsNumbers() throws Exception {
        bioclipse.requireVersion("2.2");
        bioclipse.requireVersion("2.2.0");
        bioclipse.requireVersion("2.2.0.testRunning");
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionIfTooLargeVersion() throws BioclipseException {
        bioclipse.requireVersion("999.999");
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionIfTooLargeVersion2() throws BioclipseException {
        bioclipse.requireVersion("2.4.1.testRunning");
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionIfTooLargeVersion3() throws BioclipseException {
        bioclipse.requireVersion("999.999.999.b");
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionIfTooLargeVersion4() throws BioclipseException {
        bioclipse.requireVersion("999.999.999");
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionIfTooLargeVersion5() throws BioclipseException {
        bioclipse.requireVersion("2.4.1");
    }

    @Test ( expected = BioclipseException.class )
    public void requireMoreThanOneDigitInVersionNumber()
                throws BioclipseException {
        bioclipse.requireVersion( "2" );
    }

    @Test ( expected = BioclipseException.class )
    public void throwExceptionOnNonDigits() throws BioclipseException {
        bioclipse.requireVersion( "a.b.c" );
    }

    @Test
    public void testSpanCoveringCurrentVersionThreeDigits() throws Exception {
        bioclipse.requireVersion("2.2.0", "2.2.1");
    }

    @Test
    public void testSpanCoveringCurrentVersionTwoDigits() throws Exception {
        bioclipse.requireVersion("2.2", "2.3");
    }

    @Test ( expected = BioclipseException.class )
    public void tooHighSpanTwoDigits() throws BioclipseException {
        bioclipse.requireVersion("998.999", "999.999");
    }

    @Test ( expected = BioclipseException.class )
    public void tooHighSpanThreeDigits() throws BioclipseException {
        bioclipse.requireVersion("2.4.1", "2.5.1");
    }

    @Test ( expected = BioclipseException.class )
    public void tooHighSpanThreeDigitsAndspecifier() throws BioclipseException {
        bioclipse.requireVersion("999.99.99.a", "999.99.99.b");
    }

    @Test ( expected = BioclipseException.class )
    public void rightVersionWrongSpecifier() throws BioclipseException {
        bioclipse.requireVersion("2.1.0.testRunning", "2.2.0.testRunning");
    }

    @Test ( expected = BioclipseException.class )
    public void emptySpan() throws BioclipseException {
        bioclipse.requireVersion( "2.2.0.testRunning",
                                  "2.2.0.testRunning" );
    }

    @Test ( expected = BioclipseException.class )
    public void spanTooHighThirdDigit() throws BioclipseException {
        bioclipse.requireVersion( "2.4.0.testRunning",
                                  "2.4.1.testRunning" );
    }

    @Test ( expected = BioclipseException.class )
    public void demandMoreThanOneDigit() throws BioclipseException {
        bioclipse.requireVersion( "2", "3" );
    }

    @Test ( expected = BioclipseException.class )
    public void noNonDigits() throws BioclipseException {
        bioclipse.requireVersion( "a.b.c", "c.d.e" );
    }
}
