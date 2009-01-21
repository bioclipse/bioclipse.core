package net.bioclipse.rhino.editors;

import org.eclipse.swt.graphics.RGB;
/**
 * 
 * This file is part of the Bioclipse Rhino Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
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
	public final static String COMMENT_LINE = "comment_line";
	public final static String COMMENT_SECTION = "comment_section";
	
	public static String[] getPartitionScannerTypes() {
		return new String[] {
				COMMENT_LINE,
				COMMENT_SECTION };
	}
}
