package net.bioclipse.rhino.editors;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
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
public class JsEditorConfiguration extends SourceViewerConfiguration {
	private JsCommentScanner tagScanner;
	private JsDefaultScanner scanner;
	private JsColorManager colorManager;

	public JsEditorConfiguration(JsColorManager colorManager) {
		this.colorManager = colorManager;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				JsEditorConstants.COMMENT_LINE,
				JsEditorConstants.COMMENT_SECTION
			};
	}

	protected JsDefaultScanner getDefaultScanner() {
		if (scanner == null) {
			scanner = new JsDefaultScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(JsEditorConstants.DEFAULT))));
		}
		return scanner;
	}
	
	protected JsCommentScanner getCommentScanner() {
		if (tagScanner == null) {
			tagScanner = new JsCommentScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(JsEditorConstants.COLOR_COMMENT))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		/* Code section */
		DefaultDamagerRepairer dr_def =
			new DefaultDamagerRepairer(getDefaultScanner());
		reconciler.setDamager(dr_def, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr_def, IDocument.DEFAULT_CONTENT_TYPE);
		
		/* Comment section */
		DefaultDamagerRepairer dr_comment =
			new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr_comment, JsEditorConstants.COMMENT_SECTION);
		reconciler.setRepairer(dr_comment, JsEditorConstants.COMMENT_SECTION);		
		
		/* Comment line */
		reconciler.setDamager(dr_comment, JsEditorConstants.COMMENT_LINE);
		reconciler.setRepairer(dr_comment, JsEditorConstants.COMMENT_LINE);

		return reconciler;
	}

}