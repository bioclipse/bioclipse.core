/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.jseditor.wizards;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * Creates a new molecule and opens it in JChemPaint.
 * 
 * @author egonw
 */
public class NewJavaScriptWizard extends Wizard implements INewWizard {

    public static final String WIZARD_ID =
        "net.bioclipse.scripting.ui.wizards.NewJavaScriptWizard"; //$NON-NLS-1$
    
    public static String newline = System.getProperty("line.separator");
    
    private static final String FILE_CONTENT =
        "// JavaScript" + newline;
    
    private IWorkbenchWindow activeWindow;
    
    /**
     * Creates a wizard for creating a new file resource in the workspace.
     */
    public NewJavaScriptWizard() {
        super();
    }

    public void addPages() {
    }

    public boolean canFinish() {
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        setWindowTitle("New JavaScript");
        setNeedsProgressMonitor(true);
        activeWindow = workbench.getActiveWorkbenchWindow();
    }

    public boolean performFinish() {
      //Open editor with content (String) as content
        IEditorInput input = createEditorInput();
        IWorkbenchPage page = activeWindow.getActivePage();
        try {
            page.openEditor(input, "net.bioclipse.jseditor.editor");
        } catch (PartInitException e) {
            e.printStackTrace();
        }
        return true;
    }

    private IEditorInput createEditorInput() {
        IStorage storage = new StringStorage(FILE_CONTENT);
        IEditorInput input = new StringInput(storage);
        return input;
    }

}
