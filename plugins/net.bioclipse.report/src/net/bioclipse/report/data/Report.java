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

import net.bioclipse.core.domain.IStringMatrix;

public class Report implements IReport {

	List<IReportContent> content;

	public Report() {
		this.content = new ArrayList<>();
	}

	public List<IReportContent> getContent() {
		return Collections.unmodifiableList(content);
	}
	
	@Override
	public IReport createHeader(String authors, String title) {
		content.add(new Header().setValue(title, authors));
		return this;
	}

	@Override
	public IReport startSection(String sectionTitle) {
		content.add(new Section().setValue(sectionTitle, "level1"));
		return this;
	}

	@Override
	public IReport startSubSection(String sectionTitle) {
		content.add(new Section().setValue(sectionTitle, "level2"));
		return this;
	}

	@Override
	public IReport startSubSubSection(String sectionTitle) {
		content.add(new Section().setValue(sectionTitle, "level3"));
		return this;
	}

	@Override
	public IReport addText(String text, String... styles) {
		Text textObj = new Text();
		textObj.setValue(text);
		for (String style : styles) textObj.addStyle(Text.Style.valueOf(style));
		content.add(textObj);
		return this;
	}

	@Override
	public IReport addLink(String link, String text) {
		content.add(new Hyperlink().setValue(link, text));
		return this;
	}

	@Override
	public IReport addTable(IStringMatrix matrix, String caption) {
		content.add(new Table().setValue(matrix, caption));
		return this;
	}

	@Override
	public IReport startParagraph() {
		content.add(new ParagraphStart());
		return this;
	}

	@Override
	public IReport endParagraph() {
		content.add(new ParagraphEnd());
		return this;
	}

	@Override
	public IReport startIndent() {
		content.add(new IndentStart());
		return this;
	}

	@Override
	public IReport endIndent() {
		content.add(new IndentEnd());
		return this;
	}

	@Override
	public IReport forceNewLine() {
		content.add(new NewLine());
		return this;
	}

	@Override
	public IReport createTitle(String title) {
		Text textObj = new Text();
		textObj.setValue(title);
		textObj.addStyle(Text.Style.BOLD);
		textObj.addStyle(Text.Style.CENTER);
		content.add(textObj);
		content.add(new NewLine());
		return this;
	}

	@Override
	public IReport createAuthors(String authors) {
		Text textObj = new Text();
		textObj.setValue(authors);
		textObj.addStyle(Text.Style.ITALIC);
		textObj.addStyle(Text.Style.CENTER);
		content.add(textObj);
		content.add(new NewLine());
		return this;
	}

	public IReport addBox(int height, int width) {
		IReportContent box = new Box().setValue(Integer.valueOf(height), Integer.valueOf(width));
		content.add(box);
		return this;
	}
}
