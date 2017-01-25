/* Copyright (c) 2015  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.report.serializer;

import java.util.List;

import net.bioclipse.report.data.Box;
import net.bioclipse.report.data.Header;
import net.bioclipse.report.data.Hyperlink;
import net.bioclipse.report.data.IReport;
import net.bioclipse.report.data.IReportContent;
import net.bioclipse.report.data.IndentEnd;
import net.bioclipse.report.data.IndentStart;
import net.bioclipse.report.data.NewLine;
import net.bioclipse.report.data.ParagraphEnd;
import net.bioclipse.report.data.ParagraphStart;
import net.bioclipse.report.data.Section;
import net.bioclipse.report.data.Text;

public class HTMLSerializer implements ISerializer {

	public String serialize(IReport report) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html>\n");
		for (IReportContent content : report.getContent()) {
			if (content instanceof Header) {
				Header header = (Header)content;
				buffer.append("  <head>\n");
				buffer.append("    <title>").append(header.getContent()[0]).append("</title>\n");
				buffer.append("  </head>\n");
			} else if (content instanceof Text) {
				Text text = (Text)content;
				List<Text.Style> styles = text.getStyles();
				if (styles.contains(Text.Style.ITALIC)) buffer.append("<i>");
				if (styles.contains(Text.Style.BOLD)) buffer.append("<b>");
				for (String line : text.getContent()) {
					buffer.append(line);
				}
				if (styles.contains(Text.Style.ITALIC)) buffer.append("</i>");
				if (styles.contains(Text.Style.BOLD)) buffer.append("</b>");
			} else if (content instanceof ParagraphStart) {
				buffer.append("<p>");
			} else if (content instanceof ParagraphEnd) {
				buffer.append("</p>");
			} else if (content instanceof IndentStart) {
				buffer.append("<ul>");
			} else if (content instanceof IndentEnd) {
				buffer.append("</ul>");
			} else if (content instanceof NewLine) {
				buffer.append("<br />");
			} else if (content instanceof Box) {
				Integer[] dims = (Integer[])((Box)content).getContent();
				buffer.append("<div style=\"width:").append(dims[1].intValue())
				  .append("px;height:").append(dims[0].intValue())
				  .append("px;border:1px solid #000; display: inline-block\" ></div>");
			} else if (content instanceof Section) {
				Section section = (Section)content;
				String title = section.getContent()[0];
				String level = section.getContent()[1];
				if ("level1".equals(level)) {
					buffer.append("<h1>").append(title).append("</h1>");
				} else if ("level2".equals(level)) {
					buffer.append("<h2>").append(title).append("</h2>");
				} else if ("level3".equals(level)) {
					buffer.append("<h3>").append(title).append("</h3>");
				}
			} else if (content instanceof Hyperlink) {
				Hyperlink link = (Hyperlink)content;
				buffer.append("<a href=\"").append(link.getContent()[0]).append("\">")
				      .append(link.getContent()[1]).append("</a>");
			}
		}
		buffer.append("</html>\n");
		return buffer.toString();
	}

}
