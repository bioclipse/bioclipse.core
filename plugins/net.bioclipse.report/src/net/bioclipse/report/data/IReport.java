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

import java.util.List;

import net.bioclipse.core.domain.IStringMatrix;

public interface IReport {

	public IReport createHeader(String authors, String title);
	public IReport createTitle(String title);
	public IReport createAuthors(String authors);
	public IReport startSection(String sectionTitle);
	public IReport startSubSection(String sectionTitle);
	public IReport startSubSubSection(String sectionTitle);
	public IReport addText(String text, String... styles);
	public IReport addLink(String link, String text);
	public IReport addTable(IStringMatrix matrix, String caption);
	public IReport addBox(int height, int width);

	public IReport startParagraph();
	public IReport endParagraph();

	public IReport startIndent();
	public IReport endIndent();
	
	public IReport forceNewLine();
	
	public List<IReportContent> getContent();

}
