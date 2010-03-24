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

public class XMLError {

    private int lineNumber = -1;
    private int columnNumber = -1;
    private String message;

    public XMLError(String message) {
        this.message = message;
    }

    public XMLError(String message, int lineNumber, int columnNumber) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public String toString() {
        if (lineNumber != -1 && columnNumber != -1)
            return "l" + lineNumber + ",c" + columnNumber 
                   + ": " + message;
        return message;
    }

}
