/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 *******************************************************************************/
package net.bioclipse.ui.intro;

import java.io.*;
import java.util.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.intro.config.*;
import org.w3c.dom.*;


/**
 * A ContentProvider for the Bioclipse intro page.
 * @author ola
 *
 */
public class DynamicContentProvider implements IIntroXHTMLContentProvider {


    public void init(IIntroContentProviderSite site) {
    }


    public void createContent(String id, PrintWriter out) {
    }

    public void createContent(String id, Composite parent, FormToolkit toolkit) {
    }

    private String getCurrentTimeString() {
        StringBuffer content = new StringBuffer(
                "Dynamic content from Intro ContentProvider: ");
        content.append("Current time is: ");
        content.append(new Date(System.currentTimeMillis()));
        return content.toString();
    }

    /**
     * TODO: This needs some serious updating
     */
    public void createContent(String id, Element parent) {
        Document dom = parent.getOwnerDocument();
        Element para = dom.createElement("p");
        para.setAttribute("id", "someDynamicContentId");
        para.appendChild(dom.createTextNode(getCurrentTimeString()));
        parent.appendChild(para);

    }


    public void dispose() {

    }



}
