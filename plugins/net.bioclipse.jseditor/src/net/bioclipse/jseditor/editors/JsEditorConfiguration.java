package net.bioclipse.jseditor.editors;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
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
public class JsEditorConfiguration extends SourceViewerConfiguration {
	private JsCommentScanner tagScanner;
	private JsDefaultScanner scanner;
	private JsColorManager colorManager;
	private JsQuotationmarkScanner quotationmarkScanner;

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
	
	protected JsQuotationmarkScanner getQuotationmarkScanner() {
		if (quotationmarkScanner == null) {
			quotationmarkScanner = new JsQuotationmarkScanner(colorManager);
			quotationmarkScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(JsEditorConstants.COLOR_QUOTATIONMARK))));
		}
		return quotationmarkScanner;
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
		
		/* Quotation mark section */
		DefaultDamagerRepairer dr_quotationmark =
			new DefaultDamagerRepairer(getQuotationmarkScanner());
		reconciler.setDamager(dr_quotationmark, JsEditorConstants.QUOTATIONMARK_LINE);
		reconciler.setRepairer(dr_quotationmark, JsEditorConstants.QUOTATIONMARK_LINE);

		return reconciler;
	}

}