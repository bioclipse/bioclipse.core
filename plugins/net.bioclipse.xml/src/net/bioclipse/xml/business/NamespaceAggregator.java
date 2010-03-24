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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * {@link ContentHandler} that extracts XML namespaces.
 * 
 * @author egonw
 */
public class NamespaceAggregator extends DefaultHandler2
    implements ContentHandler {

    private List<String> namespaces;
    
    public NamespaceAggregator() {
        namespaces = new ArrayList<String>();
    }
    
    public List<String> getNamespaces() {
        return namespaces;
    }
    
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        super.startPrefixMapping(prefix, uri);
        if (!namespaces.contains(uri))
            namespaces.add(uri);
    }
}
