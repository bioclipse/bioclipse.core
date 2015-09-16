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

public class Box implements IReportContent {

	Integer[] content = new Integer[2];

	@Override
	public IReportContent setValue(Object... content) {
		if (content.length > 0) {
			if (content[0] instanceof Integer) {
				this.content[0] = (Integer)content[0];
			} else {
				throw new IllegalArgumentException("First argument must be a Integer");
			}
		}
		if (content.length > 1) {
			if (content[1] instanceof Integer) {
				this.content[1] = (Integer)content[1];
			} else {
				throw new IllegalArgumentException("Second argument must be a Integer");
			}
		}
		return this;
	}

	@Override
	public Object[] getContent() {
		return content;
	}

	
}
