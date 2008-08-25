/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.sdfeditor.outline;

import net.bioclipse.cdk10.sdfeditor.Activator;
import net.bioclipse.cdk10.sdfeditor.editor.SDFEditor;
import net.bioclipse.cdk10.sdfeditor.editor.StructureTableEntry;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


public class SDFOutlinePage extends ContentOutlinePage implements ISelectionListener{

//    private static final Logger logger = Logger.getLogger(SDFOutlinePage.class);

    //Image for entries
    Image image2d=Activator.imageDescriptorFromPlugin(
           Activator.PLUGIN_ID, "icons/molecule2D.png" ).createImage();

    //Store where we come from to get viewer model
    IEditorInput editorInput;
    SDFEditor editor;

    /**
     * Constructor, called from SDFileEditor
     * @param editorInput
     * @param editor
     */
    public SDFOutlinePage(IEditorInput editorInput, SDFEditor editor) {
        this.editor=editor;
        this.editorInput=editorInput;
    }

    /**
     * Set up the Treeviewer
     */
    @Override
    public void createControl( Composite parent ) {
        
        //Creates the TreeViewer
        super.createControl( parent );

        /**
         * The Content provider
         */
        getTreeViewer().setContentProvider(new ITreeContentProvider(){

            StructureTableEntry[] entries;
            public Object[] getChildren( Object parentElement ) {
                return null;
            }

            public Object getParent( Object element ) {
                return entries;
            }

            public boolean hasChildren( Object element ) {
                return false;
            }

            public Object[] getElements( Object inputElement ) {
                if ( inputElement instanceof StructureTableEntry[] ) {
                    entries = (StructureTableEntry[]) inputElement;
                    return entries;
                }
                return new Object[0];
            }

            public void dispose() {
            }

            public void inputChanged( Viewer viewer, Object oldInput,
                                      Object newInput ) {
            }
            
        });

        
        /**
         * The Label provider
         */
        getTreeViewer().setLabelProvider( new ILabelProvider(){

            public Image getImage( Object element ) {
                return image2d;
            }

            public String getText( Object element ) {
                if ( element instanceof StructureTableEntry ) {
                    StructureTableEntry entry = (StructureTableEntry) element;
                    return entry.toString();
                }
                return element.toString();
            }

            public void addListener( ILabelProviderListener listener ) {
            }

            public void dispose() {
            }

            public boolean isLabelProperty( Object element, String property ) {
                return false;
            }

            public void removeListener( ILabelProviderListener listener ) {
            }
        });
        
        getTreeViewer().setInput( editor.getEntries() );

        //Listen for selections in Eclipse
        getSite().getPage().addSelectionListener(this);

        
    }

    /**
     * Forward selections to treeviewer
     */
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        
        if (part.equals( this )) return;
        if (!( selection instanceof IStructuredSelection )) return;
        IStructuredSelection sel = (IStructuredSelection) selection;

        //Only set selection if something new
        if (((IStructuredSelection)getTreeViewer().getSelection()).toList().containsAll( sel.toList() ))
            return;
        else
            getTreeViewer().setSelection( selection );
        
    }
    
}
