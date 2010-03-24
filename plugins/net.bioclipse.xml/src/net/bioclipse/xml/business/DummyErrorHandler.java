/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.xml.business;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Dummy error handler that ignores all problems. It is used in the
 * well-formedness test, where we do not care about validity errors.
 * 
 * @author egonw
 */
public class DummyErrorHandler implements ErrorHandler {

    public void error(SAXParseException exception) throws SAXException {
    }

    public void fatalError(SAXParseException exception) throws SAXException {
    }

    public void warning(SAXParseException exception) throws SAXException {
    }

}
