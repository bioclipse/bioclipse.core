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

public class Validation {

    public enum EventType {
        OK,
        ERROR
    }
    
    public enum Event {
        IS_WELL_FORMED("The document is well-formed.", EventType.OK),
        NOT_WELL_FORMED("The document is not well-formed.", EventType.ERROR);

        String message;
        EventType type;
        Event(String message, EventType type) {
            this.message = message;
            this.type = type;
        }
        public String toString() {
            return message;
        }
    }
}
