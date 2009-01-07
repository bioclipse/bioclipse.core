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
import nu.xom.Elements;

public class PrefixKiller {

	public static void killPrefix(Element entryNode) {
		// kill the prefix
		entryNode.setNamespacePrefix(null);
		
		// now recurse
		Elements children = entryNode.getChildElements();
		for (int i=0; i<children.size(); i++) {
			Element child = children.get(i);
			killPrefix(child);
		}
	}

}
