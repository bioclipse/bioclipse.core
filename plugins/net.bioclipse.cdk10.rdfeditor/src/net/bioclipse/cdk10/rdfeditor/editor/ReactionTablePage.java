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
package net.bioclipse.cdk10.rdfeditor.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.bioclipse.cdk10.business.CDK10Manager;
import net.bioclipse.cdk10.business.CDK10Reaction;
import net.bioclipse.cdk10.rdfeditor.Activator;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.navigator.CommonNavigator;

public class ReactionTablePage extends FormPage implements ISelectionListener{

    private static final Logger logger = Logger.getLogger(ReactionTablePage.class);

    //Declare constants for use in table
    public static final int INDEX_COLUMN = 0;
    public static final int REACTION_COLUMN = 1;

    private Table table;
    private TableViewer viewer;
    private String[] colHeaders;

    /** Registered listeners */
    private List<ISelectionChangedListener> selectionListeners;

    /** Store last selection */
    private ReactionEntitySelection selectedRows;
    
    //Store the parent MPE
    RDFEditor rdfEditor;

    //Store dirty state
    private boolean dirty;
    
    IAction editAction, extractAction, doubleClickAction;

    public ReactionTablePage(FormEditor editor, String[] colHeaders) {
        super(editor, "bc.reactiontable", "Reaction table");
        this.colHeaders=colHeaders;
        selectionListeners=new ArrayList<ISelectionChangedListener>();
        
        if ( editor instanceof  RDFEditor) {
        	rdfEditor = (RDFEditor) editor;
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
        tableLayout.addColumnData(new ColumnPixelData(70));
        ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer,ixcol) {
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                ReactionTableEntry s1=(ReactionTableEntry)e1;
                ReactionTableEntry s2=(ReactionTableEntry)e2;
                if (s1.index==s2.index) return 0;
                if (s1.index<s2.index) return -1;
                return +1;
            }
          };

        
        //Add Structure column
        TableViewerColumn col=new TableViewerColumn(viewer,SWT.BORDER);
        col.getColumn().setText("Reaction");
        tableLayout.addColumnData(new ColumnPixelData(400));

        //Add properties columns
        int colIndex=0;
        for (String colkey : colHeaders){
            TableViewerColumn propCol=new TableViewerColumn(viewer,SWT.BORDER);
            propCol.getColumn().setText(colkey);
            propCol.getColumn().setAlignment(SWT.LEFT);
            tableLayout.addColumnData(new ColumnPixelData(100));
            new ColumnViewerSorter(viewer,propCol, colIndex) {
                protected int doCompare(Viewer viewer, Object e1, Object e2) {
                    ReactionTableEntry s1=(ReactionTableEntry)e1;
                    ReactionTableEntry s2=(ReactionTableEntry)e2;
                    Object o1=s1.columns[getColIndex()];
                    Object o2=s2.columns[getColIndex()];

                    return String.valueOf( o1 )
                    .compareTo( String.valueOf( o2 ));
                }
            };

            propCol.setEditingSupport(new ReactionTableEditingSupport(
                                                        viewer,colIndex, this));
            colIndex++;
        }
        
        viewer.addSelectionChangedListener( new ISelectionChangedListener(){
            public void selectionChanged( SelectionChangedEvent event ) {
                
                if ( event.getSelection() instanceof IStructuredSelection ) {
                    IStructuredSelection sel = (IStructuredSelection) 
                                                    event.getSelection();
                    Object obj=sel.getFirstElement();
                    if ( obj instanceof ReactionTableEntry ) {
                        ReactionTableEntry entry = (ReactionTableEntry) obj;
                        rdfEditor.setCurrentModel( entry.index );
                    }
                }
                
            }
            
        });

        viewer.addDoubleClickListener( new IDoubleClickListener(){

            public void doubleClick( DoubleClickEvent event ) {
                    

            }


        });
        
        viewer.setContentProvider(new ReactionListContentProvider());
        viewer.setLabelProvider(new ReactionListLabelProviderNew());
        viewer.setUseHashlookup(true);
        OwnerDrawLabelProvider.setUpOwnerDraw(viewer);

        ReactionTableEntry[] mlist=((RDFEditor)getEditor()).getEntries();
        if (mlist!=null){
            logger.debug("Setting table input with: " + mlist.length +
                                                                 " reaction.");
            viewer.setInput(mlist);
        }
        else{
            logger.debug("Editor reactionList is empty.");
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

        /** Action to export Reaction to file */
        extractAction= new Action() {
            public void run() {
                
                ISelection sel=viewer.getSelection();
                if (sel==null){
                    showMessage( "Please select one or more " +
                    		"Reactions to export." );
                    return;
                }

                //Extract the reactions to save
                IStructuredSelection ssel = (IStructuredSelection) sel;
 
                List<ReactionTableEntry> selectedEntries
                                        =new ArrayList<ReactionTableEntry>();
                for (Object obj : ssel.toList()){
                    ReactionTableEntry entry = (ReactionTableEntry)obj;
                    selectedEntries.add( entry );
                }

                List<CDK10Reaction> reactions = ReactionSaveHelper.extractCDK10Reactions
                        (selectedEntries.toArray(new ReactionTableEntry[0] )
                         , rdfEditor.propHeaders );
                
                
                //Get a filename to save as
//                FileDialog dialog = new FileDialog (getEditorSite().getShell()
//                                                    , SWT.SAVE);
//                dialog.setFilterNames (new String [] {"MDL Files","SDF Files"
//                        , "All Files (*.*)"});
//                dialog.setFilterExtensions (new String [] {"*.mol", "*.sdf"
//                        , "*.*"}); //Windows wild cards
                
                
                //Check navigator for last selected project
                IViewPart v=PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().findView( "net.bioclipse.navigator" );
                CommonNavigator nav=(CommonNavigator)v; 
                
                String location = null;
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
//                dialog.setFilterPath (location);
//
//                dialog.setFileName ("exported.mol");
//                
//                final String saveFile=dialog.open();

                SaveAsDialog dialog=new SaveAsDialog(getEditorSite().getShell());
                dialog.setTitle( "Extract reaction(s)" );
//                dialog.setMessage( "Select where to save extracted 
//                           reactions",IMessageProvider.INFORMATION );
                dialog.setOriginalName( "exported.mol");

                int a=dialog.open();
                if (a==dialog.CANCEL){
                    logger.debug("Extract reactions canceled");
                    return;
                }
                IPath saveIPath=dialog.getResult();

                IFile saveIFile=ResourcesPlugin.getWorkspace().getRoot()
                .getFile(  saveIPath );
                String saveFile=saveIFile.getLocation().toOSString();

                //USe manager to save file
                CDK10Manager manager= new CDK10Manager();
                try {
                    manager.saveReactionsAsRDF(reactions, saveFile);
                    
                    saveIFile.refreshLocal( 0, new NullProgressMonitor() );
                } catch ( InvocationTargetException e ) {
                    LogUtils.debugTrace( logger, e );
                    logger.error( "There was an error saving file: " + saveFile );
                    showMessage( "There was an error saving file: " + saveFile  );
                    return;
                } catch ( InterruptedException e ) {
                    logger.debug( "Save of file: " + saveFile 
                                  + " was interrupted");
                } catch ( CoreException e ) {
                    logger.error( "There was an error refreshing file: " + saveFile );
                    showMessage( "There was an error refreshing file: " + saveFile  );
                }                
                
            }
        };
        extractAction.setText("Extract Reaction(s)");
        extractAction.setToolTipText("Extract the selected Reactions to file");
        extractAction.setImageDescriptor(Activator
            .imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/export.gif"));

        /** Action to edit Reaction. Switches to Single Reaction Page */
        editAction= new Action() {
            public void run() {
                
                ISelection sel=viewer.getSelection();
                switchPage( sel );
                
            }
        };
        editAction.setText("Edit Reaction");
        editAction.setToolTipText("Edit the Reaction in Reaction editor");
        editAction.setImageDescriptor(Activator
                                  .imageDescriptorFromPlugin(
                                  Activator.PLUGIN_ID,"icons/molecule2D.gif"));

    }    
    
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ReactionTablePage.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {

        manager.add(editAction);
        manager.add(extractAction);

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
        manager.add(extractAction);
//        manager.add(new Separator());
//        drillDownAdapter.addNavigationActions(manager);
    }
    

    /**
     * Switch page to single Reaction view, with selected object
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
      
      rdfEditor.setCurrentModel( ix );
      rdfEditor.pageChange( 1 );
      rdfEditor.getActivePageInstance().setFocus();
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
        if (!( obj instanceof ReactionTableEntry )) {
            logger.debug("DC on something else than " +
                "ReactionTableEntry. Not handled.");
            return -1;
        }

        if (!( getEditorInput() instanceof IFileEditorInput )) {
            showMessage( "This operation is only available " +
                "if backed by a File." );
            return -1;
        }
        
        //Our casted editor
        RDFEditor rdfEditor=(RDFEditor)getEditor();
        
        //This is the DC'ed entry
        ReactionTableEntry entry = (ReactionTableEntry) obj;

        //These are all entries in editor
        ReactionTableEntry[] entries = rdfEditor.entries;

        int ix=-1;
        //Find index of the DC'ed entry
        for (int i=0; i<rdfEditor.entries.length; i++){
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
        Set<ReactionTableEntry> newSet=new HashSet<ReactionTableEntry>();

        //Recurse
        for(TableItem item : itm){
            if (item.getData() instanceof ReactionTableEntry) {
               ReactionTableEntry entry = (ReactionTableEntry) item.getData();
               System.out.println("** Added selected in structtab: " 
                                  + entry.getReactionImpl().hashCode());
               newSet.add(entry);
            }
        }

        selectedRows=new ReactionEntitySelection(newSet);
        return selectedRows;
        
    }

    public void removeSelectionChangedListener(
                    ISelectionChangedListener listener) {
        if(selectionListeners.contains(listener))
            selectionListeners.remove(listener);
    }

    public void setSelection(ISelection selection) {
        if (!(selection instanceof ReactionEntitySelection)) return;
        this.selectedRows=(ReactionEntitySelection)selection;
        
        //Also set selected in viewer
        viewer.setSelection( (ReactionEntitySelection)selection );
        
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(
                                      viewer.getControl().getShell(),
                                      "Message",
                                      message);
    }


    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {

        if (part.equals( this )) return;
        if (part.equals( rdfEditor )) return;

        if (!( selection instanceof IStructuredSelection )) return;
        IStructuredSelection sel = (IStructuredSelection) selection;

        if (selectedRows!=null)
            if (selectedRows.equals( selection )) return;
        
//        System.out.println("Table listen: " + selection.toString());

        //Collect supported selected objects
        HashSet<ReactionTableEntry> set=new HashSet<ReactionTableEntry>();
        for (Object obj : sel.toList()){
            if ( obj instanceof ReactionTableEntry ) {
                ReactionTableEntry entry = (ReactionTableEntry) obj;
                set.add( entry );
            }
        }

        //Set as selection in table
        selectedRows=new ReactionEntitySelection(set);
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
