/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * An EditorPage for JchemPaint
 *
 */
public class JCPPage extends EditorPart{

	//The body of the editor
	private Composite body;

	//Store for access on page
	IEditorSite site;
	IEditorInput input;
	
	public JCPPage() {
		super();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		body = new Composite(parent, SWT.NONE);
		FillLayout layout=new FillLayout();
		body.setLayout(layout);

		Text txt=new Text(body, SWT.NONE);
		txt.setText("This is where the structure should be visualized. TODO: Implement");

	}

	

	/**
	 * Not used
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/**
	 * Not used
	 */
	@Override
	public void doSaveAs() {
	}

	@Override
	public IEditorSite getEditorSite() {
		return site;
	}

	@Override
	public IEditorSite getSite() {
		return site;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.site=site;
		this.input=input;
	}

	@Override
	public boolean isDirty() {
		//TODO: implement
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		//TODO: change when available
		return false;
	}


	@Override
	public void setFocus() {
		//TODO: change when available
	}
	
	

}
