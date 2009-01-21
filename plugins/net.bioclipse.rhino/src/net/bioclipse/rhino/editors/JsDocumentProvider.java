package net.bioclipse.rhino.editors;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
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
public class JsDocumentProvider extends FileDocumentProvider {
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);

		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
						new JsPartitionScanner(),
					JsEditorConstants.getPartitionScannerTypes());
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}