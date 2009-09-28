/*******************************************************************************
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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Simple error handler that keeps track of validation errors.
 * 
 * @author egonw
 */
public class SimpleErrorHandler implements ErrorHandler {

    private List<XMLError> errors;

    public SimpleErrorHandler() {
        errors = new ArrayList<XMLError>();
    }

    public List<XMLError> getErrors() {
        return errors;
    }

    public void error(SAXParseException exception) throws SAXException {
        errors.add(
            new XMLError(
                exception.getMessage(),
                exception.getLineNumber(),
                exception.getColumnNumber()
            )
        );
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        errors.add(
            new XMLError(
                exception.getMessage(),
                exception.getLineNumber(),
                exception.getColumnNumber()
            )
        );
    }

    public void warning(SAXParseException exception) throws SAXException {
        errors.add(
            new XMLError(
                exception.getMessage(),
                exception.getLineNumber(),
                exception.getColumnNumber()
            )
        );
    }

}
