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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Text extends OneStringContent implements IReportContent {

	public enum Style {
		ITALIC,
		BOLD,
		CENTER
	}

	List<Style> styles = new ArrayList<Style>();

	public void addStyle(Style style) {
		if (style == null) return;
		styles.add(style);
	}

	public List<Style> getStyles() {
		return Collections.unmodifiableList(styles);
	}

}
