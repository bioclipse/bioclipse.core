/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An editor for Multiple Alignments by providing JalView on one tab and text on second
 * @author ola
 *
 */
public class JalviewEditor extends MultiPageEditorPart {

    private static final Logger logger = Logger.getLogger(JalviewEditor.class);
    
	/** The text editor used in page 1. */
	private TextEditor editor;

    JCPPage jcpPage;
    TextEditor textEditor;
    int textEditorIndex;

	String content;		//Read from EditorInput

	//Path to the underlying file TODO: Update to stream avia Resource but needs JalView re-coding
	String path;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}
	
	@Override
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		jcpPage=new JCPPage();
		textEditor=new TextEditor();
		
		try {
			addPage(jcpPage, getEditorInput());
			textEditorIndex=addPage(textEditor, getEditorInput());
		} catch (PartInitException e) {
			LogUtils.debugTrace(logger, e);
		}
//		createPage0();
//		createPage1();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * Creates page 0 of the multi-page editor,
	 * which consists of Jmol.
	 */
	void createPage0() {

		Composite parent = new Composite(getContainer(), SWT.NONE);

		//Set the layout for parent
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);

		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace=true;
		parent.setLayoutData(layoutData);

		//Add the Jmol composite to the top
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		layout = new GridLayout();
		composite.setLayout(layout);
		layoutData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(layoutData);


		
		int index = addPage(parent);
		setPageText(index, "Jalview");

	}

	

	/**
	 * Creates page 1 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage1() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}


	private String getPathFromEditor() {
		IEditorInput input=getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			System.out.println("Not FIleEditorInput.");
			//TODO: Close editor?
			return null;
		}
		IFileEditorInput finput = (IFileEditorInput) input;

		IFile file=finput.getFile();
		return file.getLocation().toOSString();
	}

	private String getContentsFromEditor(){

		IEditorInput input=getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			System.out.println("Not FIleEditorInput.");
			//TODO: Close editor?
			return null;
		}
		IFileEditorInput finput = (IFileEditorInput) input;

		IFile file=finput.getFile();
		if (!(file.exists())){
			System.out.println("File does not exist.");
			//TODO: Close editor?
			return null;
		}

//		return file.getFullPath().toFile();


		InputStream instream;
		try {
			instream = file.getContents();
			StringBuilder builder = new StringBuilder();

			// read bytes until eof
			for(int i = instream.read(); i != -1; i = instream.read())
			{
				builder.append((char)i);
			}
			instream.close();

			return builder.toString();

		} catch (CoreException e) {
			// TODO Auto-generated catch block
		    LogUtils.debugTrace(logger, e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		    LogUtils.debugTrace(logger, e);
		}

		return null;
	}
	
	
}
