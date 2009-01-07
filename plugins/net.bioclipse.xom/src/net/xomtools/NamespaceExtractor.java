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
import nu.xom.XPathContext;

public abstract class NamespaceExtractor {

	public abstract Nodes getContent(Node entryNode, String localName);
	public abstract Nodes getContent(Node entryNode);
	
	protected boolean hasElement(Node node, String localName, String namespace) {
		return node.query(".//ns:" + localName, new XPathContext("ns", namespace)).size() > 0;
	}

	protected Nodes findNodes(Node entryNode, String namespace) {
		return findNodes(entryNode, null, namespace);
	}

	protected Nodes findNodes(Node entryNode, String localName, String namespace) {
		Nodes cmlNodes = new Nodes();
		
		// this should give me all direct childs
		Nodes results = entryNode.query("./*");
		if (results == null || results.size() == 0) {
			return cmlNodes;
		}
		
		for (int i=0; i<results.size(); i++) {
			// check if node is a CML node
			Element node = (Element)results.get(i);
		    if (namespace.equals(node.getNamespaceURI()) &&
		    	(localName == null || localName.equals(node.getLocalName()))) {
		    	// OK, found a matching node
		    	cmlNodes.append(node);
		    } else {
		    	// do a depth first
		    	append(cmlNodes, findNodes(node, localName, namespace));
		    }
		}
		return cmlNodes;
	}

	public Node findNodeByID(Node entryNode, String id) {
		// this should give me all direct childs
		Nodes results = entryNode.query("./*");
		if (results == null || results.size() == 0) {
			return null;
		}
		
		for (int i=0; i<results.size(); i++) {
			// check if node is a CML node
			Element node = (Element)results.get(i);
		    if (node.getAttribute("id") != null &&
		    	id.equals(node.getAttributeValue("id"))) {
		    	return node;
		    } else {
		    	// do a depth first
		    	Node childNode = findNodeByID(node, id);
		    	if (childNode != null) return childNode;
		    }
		}
		return null;
	}

	protected Nodes append(Nodes nodes, Nodes nodesToAppend) {
		if (nodes == null) return nodesToAppend;
		if (nodesToAppend == null) return nodes;
		
		for (int i=0; i<nodesToAppend.size(); i++) {
			nodes.append(nodesToAppend.get(i));
		}
		return nodes;
	}

}
