/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - first implementation
 *******************************************************************************/
package net.xomtools;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
public class CMLExtractor extends NamespaceExtractor {
        public final static String CML_NAMESPACE = "http://www.xml-cml.org/schema";
        private final static String BAD_CML_NAMESPACE = "http://www.xmlcml.org/schema";
        private final static String CML2CORE_NAMESPACE = "http://www.xml-cml.org/schema/cml2/core";
        public Nodes getContent(Node entryNode, String localName) {
                Nodes nodes = new Nodes();
                append(nodes, findNodes(entryNode, localName, CML_NAMESPACE));
                append(nodes, findNodes(entryNode, localName, CML2CORE_NAMESPACE));
                append(nodes, findNodes(entryNode, localName, BAD_CML_NAMESPACE));
                return nodes;
        }
        public Nodes getContent(Node entryNode) {
                return getContent(entryNode, null);
        }
        public boolean hasCMLElement(Node node, String localName) {
                return hasElement(node, localName, CML_NAMESPACE) |
                       hasElement(node, localName, CML2CORE_NAMESPACE) |
                       hasElement(node, localName, BAD_CML_NAMESPACE);
        }
        public String findAReasonableName(Node node) {
                String partname = "molecule";
                // try to find a name, as replacement for the partname
                Nodes molecules = getContent(node, "molecule");
                if (molecules.size() > 0) {
                        // ok, just pick something from the first molecule
                        Element moleculeElem = (Element)molecules.get(0);
                        if (moleculeElem.getAttribute("name") != null) {
                                partname = moleculeElem.getAttribute("name").getValue();
                        } else {
                                Nodes possibleNames = getContent(node, "name");
                                if (possibleNames.size() == 0)
                                        possibleNames = getContent(node, "label");
                                if (possibleNames.size() > 0) {
                                        partname = possibleNames.get(0).getValue();
                                }
                        }
                }
                return partname;
        }
}
