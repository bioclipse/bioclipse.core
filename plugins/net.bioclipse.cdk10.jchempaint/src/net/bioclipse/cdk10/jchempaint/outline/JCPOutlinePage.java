/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.outline;


import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPBasedEditor;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.openscience.cdk.interfaces.IChemModel;

/**
 * An Outline Page for CDK Molecule, for JCP primarily and only 
 * MDLEditor currently
 * @author ola
 *
 */
public class JCPOutlinePage extends ContentOutlinePage 
                            implements ISelectionListener, IAdaptable{

    private final String CONTRIBUTOR_ID
            ="net.bioclipse.cdk10.jchempaint.outline.JCPOutlinePage";

    private static final Logger logger = Logger.getLogger(JCPOutlinePage.class);
    
    private IEditorInput editorInput;
    private IJCPBasedEditor editor;
    private TreeViewer treeViewer;

    //The model root
    IChemModel chemModel;

    /**
     * Our constructor
     * @param editorInput
     * @param mdlEditor
     */
    public JCPOutlinePage(IEditorInput editorInput
            , IJCPBasedEditor mdlEditor) {
        super();
        this.editorInput=editorInput;
        this.editor=mdlEditor;

    }


    /**
     * Set up the treeviewer for the outline with Providers for MDLEditor
     */
    public void createControl(Composite parent) {

        super.createControl(parent);

        treeViewer= getTreeViewer();
        treeViewer.setContentProvider(new StructureContentProvider());
        treeViewer.setLabelProvider(new StructureLabelProvider());
//        viewer.setSorter(new NameSorter());
        treeViewer.addSelectionChangedListener(this);

        if (editor.getJcpModel()==null) return;
        
        chemModel=editor.getJcpModel().getChemModel();
        
        treeViewer.setInput(chemModel);
        treeViewer.expandToLevel(2);

        getSite().getPage().addSelectionListener(this);
    }


    /**
     * Update selected items if selected in editor
     */
    public void selectionChanged(IWorkbenchPart selectedPart,
                                 ISelection selection) {
        // Does nothing for now. See selectionChanged in
        // net.bioclipse.jmol.views.outline.JmolContentOutlinePage
        // for implementation inspiration.
    }



    /**
     * This is our ID for the TabbedPropertiesContributor
     */
    public String getContributorId() {
        return CONTRIBUTOR_ID;
    }


    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }


}
