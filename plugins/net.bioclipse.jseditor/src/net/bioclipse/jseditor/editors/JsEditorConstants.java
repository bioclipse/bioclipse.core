package net.bioclipse.jseditor.editors;

import org.eclipse.swt.graphics.RGB;
/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
public class JsEditorConstants {
	
	public final static RGB COLOR_COMMENT = new RGB(34, 139, 34);
	public final static RGB COLOR_QUOTATIONMARK = new RGB(0, 127, 255);
	public final static RGB COLOR_EMAIL = new RGB(0, 255, 0);
	public final static RGB COLOR_STATEMENTS = new RGB(146, 0, 10);
	//public final static RGB COLOR_GLOBAL = new RGB(197, 179, 88);
	public final static RGB COLOR_VAR = new RGB(204, 0, 204);
	public final static RGB DEFAULT = new RGB(0, 0, 0);
	
	/* partition scanner */
	public final static String QUOTATIONMARK_LINE = "quotationmark_line";
	public final static String COMMENT_LINE = "comment_line";
	public final static String COMMENT_SECTION = "comment_section";
	
	public static String[] getPartitionScannerTypes() {
		return new String[] {
				QUOTATIONMARK_LINE,
				COMMENT_LINE,
				COMMENT_SECTION };
	}
}
