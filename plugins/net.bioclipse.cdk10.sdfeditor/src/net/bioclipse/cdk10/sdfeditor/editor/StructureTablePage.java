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
package net.bioclipse.cdk10.sdfeditor.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bioclipse.cdk10.sdfeditor.Activator;
import net.bioclipse.ui.dialogs.WSFileDialog;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.navigator.CommonNavigator;

public class StructureTablePage extends FormPage implements ISelectionListener{

    private static final Logger logger = Logger.getLogger(
                                                      StructureTablePage.class);

    //Declare constants for use in table
    public static final int INDEX_COLUMN=0;
    public static final int STRUCTURE_COLUMN=1;

    private Table table;
    private TableViewer viewer;
    private String[] colHeaders;

    /** Registered listeners */
    private List<ISelectionChangedListener> selectionListeners;

    /** Store last selection */
    private StructureEntitySelection selectedRows;
    
    //Store the parent MPE
    SDFEditor sdfEditor;

    //Store dirty state
    private boolean dirty;
    
    IAction editAction, exportAction, doubleClickAction;

    public StructureTablePage(FormEditor editor, String[] colHeaders) {
        super(editor, "bc.structuretable", "Structure table");
        this.colHeaders=colHeaders;
        selectionListeners=new ArrayList<ISelectionChangedListener>();
        
        if ( editor instanceof  SDFEditor) {
            sdfEditor = (SDFEditor) editor;
        }
        
        dirty=false;

//      selectedRows=new StructureEntitySelection();
    }

    /**
     * Add content to form
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {

        FormToolkit toolkit = managedForm.getToolkit();
        ScrolledForm form = managedForm.getForm();
        form.setText("Structure table");
//      form.setBackgroundImage(FormArticlePlugin.getDefault().getImage(BOGUS));
        final Composite body = form.getBody();
        FillLayout layout=new FillLayout();
        body.setLayout(layout);

        viewer = new TableViewer(body, SWT.BORDER  | SWT.MULTI | 
                                            SWT.FULL_SELECTION | SWT.VIRTUAL);
        table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        toolkit.adapt(table, true, true);

        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);

        //Add index column
        TableViewerColumn ixcol=new TableViewerColumn(viewer,SWT.BORDER);
        ixcol.getColumn().setText("Index");
        tableLayout.addColumnData(new ColumnPixelData(50));
        ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer,ixcol) {
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                StructureTableEntry s1=(StructureTableEntry)e1;
                StructureTableEntry s2=(StructureTableEntry)e2;
                if (s1.index==s2.index) return 0;
                if (s1.index<s2.index) return -1;
                return +1;
            }
          };

        
        //Add Structure column
        TableViewerColumn col=new TableViewerColumn(viewer,SWT.BORDER);
        col.getColumn().setText("Structure");
        tableLayout.addColumnData(new ColumnPixelData(100));

        //Add properties columns
        int colIndex=0;
        for (String colkey : colHeaders){
            TableViewerColumn propCol=new TableViewerColumn(viewer,SWT.BORDER);
            propCol.getColumn().setText(colkey);
            propCol.getColumn().setAlignment(SWT.LEFT);
            tableLayout.addColumnData(new ColumnPixelData(100));
            new ColumnViewerSorter(viewer,propCol, colIndex) {
                protected int doCompare(Viewer viewer, Object e1, Object e2) {
                    StructureTableEntry s1=(StructureTableEntry)e1;
                    StructureTableEntry s2=(StructureTableEntry)e2;
                    Object o1=s1.columns[getColIndex()];
                    Object o2=s2.columns[getColIndex()];

                    return String.valueOf( o1 )
                    .compareTo( String.valueOf( o2 ));
                }
            };

            propCol.setEditingSupport(new StructureTableEditingSupport(
                                                        viewer,colIndex, this));
            colIndex++;
        }
        
        viewer.addSelectionChangedListener( new ISelectionChangedListener(){
            public void selectionChanged( SelectionChangedEvent event ) {
                
                if ( event.getSelection() instanceof IStructuredSelection ) {
                    IStructuredSelection sel = (IStructuredSelection) 
                                                    event.getSelection();
                    Object obj=sel.getFirstElement();
                    if ( obj instanceof StructureTableEntry ) {
                        StructureTableEntry entry = (StructureTableEntry) obj;
                        sdfEditor.setCurrentModel( entry.index );
                    }
                }
                
            }
            
        });

        viewer.addDoubleClickListener( new IDoubleClickListener(){

            public void doubleClick( DoubleClickEvent event ) {
                    

            }


        });
        
        viewer.setContentProvider(new MoleculeListContentProvider());
        viewer.setLabelProvider(new MoleculeListLabelProviderNew());
        viewer.setUseHashlookup(true);
        OwnerDrawLabelProvider.setUpOwnerDraw(viewer);

        StructureTableEntry[] mlist=((SDFEditor)getEditor()).getEntries();
        if (mlist!=null){
            logger.debug("Setting table input with: " + mlist.length +
                                                                 " molecules.");
            viewer.setInput(mlist);
        }
        else{
            logger.debug("Editor moleculeList is empty.");
        }

        //Set sorter
        cSorter.setSorter(cSorter, ColumnViewerSorter.ASC);

        
        //Configure and add actions
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        
        
        
        //Post selections in Table to Eclipse
        getEditor().getSite().setSelectionProvider(viewer);
        
        getEditor().getSite().getPage().addSelectionListener(this);

    }

    /**
     * Create the actions
     */
    private void makeActions() {

        doubleClickAction = new Action() {
            public void run() {
                
                ISelection sel=viewer.getSelection();
                
                switchPage( sel );

            }

        };

        /** Action to export structure to file */
        exportAction= new Action() {
            public void run() {
                
                ISelection sel=viewer.getSelection();
                if (sel==null){
                    showMessage( "Please select a result ot export." );
                    return;
                }
                
                //Get a filename to save as
                FileDialog dialog = new FileDialog (getEditorSite().getShell()
                                                    , SWT.SAVE);
                dialog.setFilterNames (new String [] {"MDL Files","SDF Files"
                        , "All Files (*.*)"});
                dialog.setFilterExtensions (new String [] {"*.mol", "*.sdf"
                        , "*.*"}); //Windows wild cards
                

                //Check navigator for last selected project
                IViewPart v=PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().findView( "net.bioclipse.navigator" );
                CommonNavigator nav=(CommonNavigator)v; 
                
                String location=null;
                if (nav!=null){
                    if ( nav.getCommonViewer().getSelection() instanceof IResource ) {
                        IResource res = (IResource) nav.getCommonViewer().getSelection();
                        IProject project=res.getProject();
                        location=project.getLocation().toOSString();
                    }
                }
                else{
                    location=ResourcesPlugin.getWorkspace().getRoot()
                    .getLocation().toOSString();

                }
                
                //On Mac OS X, this is fixed in Eclipse > 20080409
                //see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=101948
                dialog.setFilterPath (location);

                dialog.setFileName ("exported.mol");
                
                final String saveFile=dialog.open();

                
                
//                WSFileDialog dialog=new WSFileDialog(getEditorSite().getShell(),
//                                             SWT.SINGLE,"Specify file to save");

                
                WorkspaceModifyOperation op=new WorkspaceModifyOperation(){

                    @Override
                    protected void execute( IProgressMonitor monitor )
                                                      throws CoreException,
                                                      InvocationTargetException,
                                                      InterruptedException {

                        //TODO: Do the export
                        logger.debug("Saving to file: " + saveFile);
                        
                    }
                    
                };
                
                
                
            }
        };
        exportAction.setText("Export structure");
        exportAction.setToolTipText("Export the enrty to file");
        exportAction.setImageDescriptor(Activator
            .imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/export.gif"));

        /** Action to edit structure. Switches to Single Structure Page */
        editAction= new Action() {
            public void run() {
                
                ISelection sel=viewer.getSelection();
                switchPage( sel );
                
            }
        };
        editAction.setText("Edit structure");
        editAction.setToolTipText("Edit the structure in structure editor");
        editAction.setImageDescriptor(Activator
                                  .imageDescriptorFromPlugin(
                                  Activator.PLUGIN_ID,"icons/molecule2D.gif"));

    }    
    
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                StructureTablePage.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {

        manager.add(editAction);
        manager.add(exportAction);

        manager.add(new Separator());
//      drillDownAdapter.addNavigationActions(manager);

        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    
    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void contributeToActionBars() {
        IActionBars bars = getEditorSite().getActionBars();
//        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(exportAction);
//        manager.add(new Separator());
//        drillDownAdapter.addNavigationActions(manager);
    }
    

    /**
     * Switch page to single structure view, with selected object
     * @param sel
     */
    private void switchPage( ISelection sel ) {

        if (sel==null){
            logger.debug("No selection in DC");
            return;
        }
        int ix = getSelectedIndex( sel );

      if (ix<0){
          //Should not happen
          showMessage( "Index of selection is negative!?!" );
          return;
      }
      
      logger.debug( "Switch page with index: " + ix );
      
      sdfEditor.setCurrentModel( ix );
      sdfEditor.pageChange( 1 );
      sdfEditor.getActivePageInstance().setFocus();
    }
    
    
    /*
     * Below is for providing selections from table to e.g. outline view
     */

    public int getSelectedIndex( ISelection selection ) {

        if (!( selection instanceof IStructuredSelection ))
                return -1;

        IStructuredSelection ssel 
            = (IStructuredSelection) selection;
        Object obj=ssel.getFirstElement();
        if (!( obj instanceof StructureTableEntry )) {
            logger.debug("DC on something else than " +
                "StructureTableEntry. Not handled.");
            return -1;
        }

        if (!( getEditorInput() instanceof IFileEditorInput )) {
            showMessage( "This operation is only available " +
                "if backed by a File." );
            return -1;
        }
        
        //Our casted editor
        SDFEditor sdfEditor=(SDFEditor)getEditor();
        
        //This is the DC'ed entry
        StructureTableEntry entry = (StructureTableEntry) obj;

        //These are all entries in editor
        StructureTableEntry[] entries = sdfEditor.entries;

        int ix=-1;
        //Find index of the DC'ed entry
        for (int i=0; i<sdfEditor.entries.length; i++){
            if (entries[i].equals( entry )){
                ix=i;
            }
        }
        return ix;
    }
    
    
    public void addSelectionChangedListener(ISelectionChangedListener listener){
        if(!selectionListeners.contains(listener))
        {
            selectionListeners.add(listener);
        }
    }

    public ISelection getSelection() {

        TableItem[] itm=(TableItem[])viewer.getTable().getSelection();
        if (itm==null || itm.length<=0) return null;

        //Hold new selection
        Set<StructureTableEntry> newSet=new HashSet<StructureTableEntry>();

        //Recurse
        for(TableItem item : itm){
            if (item.getData() instanceof StructureTableEntry) {
               StructureTableEntry entry = (StructureTableEntry) item.getData();
               System.out.println("** Added selected in structtab: " 
                                  + entry.getMoleculeImpl().hashCode());
               newSet.add(entry);
            }
        }

        selectedRows=new StructureEntitySelection(newSet);
        return selectedRows;
        
    }

    public void removeSelectionChangedListener(
                    ISelectionChangedListener listener) {
        if(selectionListeners.contains(listener))
            selectionListeners.remove(listener);
    }

    public void setSelection(ISelection selection) {
        if (!(selection instanceof StructureEntitySelection)) return;
        this.selectedRows=(StructureEntitySelection)selection;
        
        //Also set selected in viewer
        viewer.setSelection( (StructureEntitySelection)selection );
        
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(
                                      viewer.getControl().getShell(),
                                      "Message",
                                      message);
    }


    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {

        if (part.equals( this )) return;
        if (part.equals( sdfEditor )) return;

        if (!( selection instanceof IStructuredSelection )) return;
        IStructuredSelection sel = (IStructuredSelection) selection;

        if (selectedRows!=null)
            if (selectedRows.equals( selection )) return;
        
//        System.out.println("Table listen: " + selection.toString());

        //Collect supported selected objects
        HashSet<StructureTableEntry> set=new HashSet<StructureTableEntry>();
        for (Object obj : sel.toList()){
            if ( obj instanceof StructureTableEntry ) {
                StructureTableEntry entry = (StructureTableEntry) obj;
                set.add( entry );
            }
        }

        //Set as selection in table
        selectedRows=new StructureEntitySelection(set);
        setSelection( selectedRows);
        
    }

    /**
     * We provide our own dirty state
     * @param dirty
     */
    public void setDirty( boolean dirty ) {
        if (this.dirty==dirty) return;
        this.dirty=dirty;
        firePropertyChange(PROP_DIRTY);
    }


    /**
     * We provide our own dirty state.
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * A formPage is not an editor, and hence we override 
     * in order to respond to dirty changes
     */
    @Override
    public boolean isEditor() {
        return true;
    }
    
}
