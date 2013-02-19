/* *****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.ui.editors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;

public class XMLEditor extends TextEditor {

    private ColorManager colorManager;

    public XMLEditor() {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        setDocumentProvider(new XMLDocumentProvider());
        PlatformUI.getWorkbench().getHelpSystem()
        .setHelp( Display.getCurrent().getActiveShell(),
                  "net.bioclipse.ui.accountWizardHelp" );
    }
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

}
