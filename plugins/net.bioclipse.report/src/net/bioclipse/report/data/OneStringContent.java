/* Copyright (c) 2015  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.report.data;

public class OneStringContent implements IReportContent {

	String[] content = new String[1];

	@Override
	public IReportContent setValue(Object... content) {
		if (content.length > 0) {
			if (content[0] instanceof String) {
				this.content[0] = (String)content[0];
			} else {
				throw new IllegalArgumentException("First argument must be a String");
			}
		}
		return this;
	}

	@Override
	public String[] getContent() {
		return content;
	}

}
