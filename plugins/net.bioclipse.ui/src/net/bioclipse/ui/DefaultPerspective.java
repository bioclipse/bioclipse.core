/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 *******************************************************************************/

package net.bioclipse.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * A default perspective that initializes the Project Explorer,
 * Properties, and Outline Views
 * @author ola
 */
public class DefaultPerspective implements IPerspectiveFactory {

    IPageLayout storedLayout;

    /**
     * This perspective's ID
     */
    public static final String ID_PERSPECTIVE =
        "net.bioclipse.ui.Perspective";

    public static final String ID_NAVIGATOR = 
        "net.bioclipse.navigator";

    /**
     * Create initial layout
     */
    public void createInitialLayout(IPageLayout layout) {

        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        layout.setFixed(false);
        layout.addPerspectiveShortcut(ID_PERSPECTIVE);

        //Add layouts for views
        IFolderLayout left_folder_layout =
            layout.createFolder(
                    "explorer",
                    IPageLayout.LEFT,
                    0.20f,
                    editorArea);

        IFolderLayout right_folder_layout =
            layout.createFolder(
                    "outline",
                    IPageLayout.RIGHT,
                    0.70f,
                    editorArea);

        IFolderLayout bottom_folder_layout =
            layout.createFolder(
                    "properties",
                    IPageLayout.BOTTOM,
                    0.70f,
                    editorArea);


        //Add views
        left_folder_layout.addView(ID_NAVIGATOR);
//        bottom_folder_layout.addView(ID_JAVSCRIPT_CONSOLE);
        bottom_folder_layout.addView(IPageLayout.ID_PROP_SHEET);
        bottom_folder_layout.addView(IPageLayout.ID_PROGRESS_VIEW);
        right_folder_layout.addView(IPageLayout.ID_OUTLINE);

        //Add NewWizards shortcuts
        //TODO
//        layout.addNewWizardShortcut("net.bioclipse.wizards.NewFolderWizard");

        //Add ShowView shortcuts
        layout.addShowViewShortcut(ID_NAVIGATOR);    
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);    
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);    
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);    
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);    
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);    

    }
}
