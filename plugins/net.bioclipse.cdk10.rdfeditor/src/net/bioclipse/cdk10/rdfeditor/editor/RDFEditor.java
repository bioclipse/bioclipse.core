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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bioclipse.cdk10.business.CDK10Manager;
import net.bioclipse.cdk10.business.CDK10Reaction;
import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPbasedMPE;
import net.bioclipse.cdk10.rdfeditor.outline.RDFOutlinePage;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

public class RDFEditor extends FormEditor 
       implements IJCPbasedMPE, IResourceChangeListener, IAdaptable {

    private static final Logger logger = Logger.getLogger(RDFEditor.class);

    private int oldIx = -1;

    //If used, if JCP should be shown
    private RDFJCPPage jcpPage;

//    private TextEditor textEditor;
    private ReactionTablePage tablePage;

    //Model for the editor: Based on CDK
    ReactionTableEntry[] entries;
    ArrayList<String> propHeaders;

    //Index of the current model
    int currentModel;

    //Store indices for pages
    public static int TABLE_PAGE_INDEX;
    public static int JCP_PAGE_INDEX;

    //The OutlinePage
    IContentOutlinePage fOutlinePage;
    
    public int getCurrentModel() {
        return currentModel;
    }
    
    public void setCurrentModel( int currentModel ) {
        this.currentModel = currentModel;
    }

    public ReactionTableEntry[] getEntries() {
        return entries;
    }

    public void setEntries(ReactionTableEntry[] entries) {
        this.entries = entries;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
                throws PartInitException {
        super.init(site, input);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        propHeaders = new ArrayList<String>();

        //Parse input with CDK
        parseInput();

        //Tables page
        tablePage = new ReactionTablePage(this, 
                                           propHeaders.toArray(new String[0]));

        //JCP page
        jcpPage = new RDFJCPPage(null);
        
        //Make page aware that this is its parent MPE
        jcpPage.setMPE(this);
        
        //We start with index 0 of the file
        currentModel = 0;

        //Texteditor. For now, not added due to memory consumption
//        textEditor = new TextEditor();

    }

    @Override
    protected void addPages() {

        try {
            //We need to call addPages with editor input to be treated 
            //as editor, and hence respond to dirty changes
            TABLE_PAGE_INDEX = addPage(tablePage, getEditorInput());

            JCP_PAGE_INDEX = addPage(jcpPage, getEditorInput());
//                jcpPage.activateJCP();
            setPageText(JCP_PAGE_INDEX, "Single entry");
            
//Texteditor not added
//            int index = addPage(textEditor, getEditorInput());
//            setPageText(index, textEditor.getTitle());
            
            IFile file = getFileFromInput();
            if (file != null) {
                setPartName( file.getName() );
            }

        } 
        catch (PartInitException e) {
            LogUtils.debugTrace(logger, e);
        }
    }

    @Override
    protected void pageChange( int newPageIndex ) {
        if (getCurrentPage() == TABLE_PAGE_INDEX) {
            if (newPageIndex == JCP_PAGE_INDEX) {
                //We are in structureTable but should switch to JCP
                //Get selected index in table and set in JCP

                int ix = currentModel;
                System.out.println("We should switch to JCP with index: " + ix);

                //Handle case when we have no selected index (like first time)
                if (ix<0) ix=0;

                if (ix == oldIx){
                    //No need to set new model, just update

                    // ugly trigger for a repaint
                    jcpPage.cl.controlResized( null );
                    super.pageChange( newPageIndex );
                    return;
                }
                
                Object obj = entries[ix].getReactionImpl();
                if ( obj instanceof IReaction ) {
                    //What else could it be than an AC? :-)
                	IReaction ac = (IReaction) obj;

                    IChemModel ml = getChemModelByIndex( ix );
                    if (ml == null) {
                        logger.debug( "Error getting chemmodel by index: "
                                      + ix );
                        return;
                    }
                    try {
                        jcpPage.updateJCPModel( ml );
                    } catch ( BioclipseException e ) {
                        logger.debug( "Cannot set new chemModel for JCP." );
                    }

                } 
                else {
                    logger.debug("Cannot display in second type the object: " +
                    		     obj.getClass().getName());
                }
                //Store current index to avoid unnecessary updating
                oldIx = ix;

                //Set visible so we can listen for selections
                jcpPage.setVisible( true );
            }
        }
        
        else if (getCurrentPage() == JCP_PAGE_INDEX) {
            if (newPageIndex == TABLE_PAGE_INDEX){
                //We are in JCP, but have switched to structure table
                //We know the selected index so this should be trivial
                
                tablePage.setSelection( 
                          new ReactionEntitySelection(entries[currentModel]) );

                //Set visible so we can listen for selections
                jcpPage.setVisible( false );
            }
        }

        /*
            Object obj=((IStructuredSelection)tablePage
                            .getSelection()).getFirstElement();
            
            ReactionTableEntry entry=(ReactionTableEntry)obj;

            IAtomContainer ac=(IAtomContainer)entry.getReactionImpl();
            
            IChemModel ml=new ChemModel();
            IReactionSet ms=new ReactionSet();
            ms.addAtomContainer( ac );
            ml.setReactionSet( ms );
            
            try {
                jcpPage.updateJCPModel( ml);
            } catch ( BioclipseException e ) {
                logger.debug( "Cannot set new chemModel for JCP." );
            }
            
        }
    
        */
        super.pageChange( newPageIndex );
        
    }


    private IChemModel getChemModelByIndex( int ix ) {
        if (ix>=entries.length) return null;
        
        Object obj = entries[ix].getReactionImpl();
        if (!( obj instanceof IReaction )) {
            return null;
        }
        //What else could it be than an AC? :-)
        IReaction react = (IReaction) obj;

        IChemModel ml = new ChemModel();
        IReactionSet rs = new ReactionSet();
        rs.addReaction( react );
        ml.setReactionSet( rs );
        return ml;
    }
    
    @Override
    public boolean isDirty() {
        return jcpPage.isDirty() || tablePage.isDirty();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {

        //This is the file we should write to
        IFile file = getFileFromInput();
        if (file == null){
            logger.error( "Could not get file. Save canceled. ");
            showMessage(  "Could not get file. Save canceled. ");
            return;
        }
        
        //So, serialize the entries[] to a ChemFile
        //Extract mols from entries
        List<CDK10Reaction> reactions = ReactionSaveHelper
                                   .extractCDK10Reactions( entries, propHeaders );
        
        CDK10Manager manager = new CDK10Manager();
        try {
            manager.saveReactionsAsRDF(reactions, file.getLocation().toOSString());
            jcpPage.setDirty( false );
            tablePage.setDirty( false );

            //Refresh the IFile for the workspace to be up2date
            file.refreshLocal( 0, new NullProgressMonitor() );

        } catch ( InvocationTargetException e ) {
            LogUtils.debugTrace( logger, e );
            logger.error( "There was an error saving file: " + file );
            showMessage( "There was an error saving file: " + file  );
        } catch ( InterruptedException e ) {
            logger.debug( "Save of file: " + file.getName() 
                          + " was interrupted");
        } catch ( CoreException e ) {
            logger.error( "There was an error refreshing WS: " + file );
            showMessage( "There was an error refreshing WS: " + file  );
        }
    }

    private void showMessage(String message) {
        MessageDialog.openInformation( getSite().getShell(),
                                       "Message",
                                       message );
    }

    @Override
    public void doSaveAs() {
        //TODO
    }

    @Override
    public boolean isSaveAsAllowed() {
        //TODO
        return false;
    }

    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        jcpPage.dispose();
        super.dispose();
    }

    public void resourceChanged(IResourceChangeEvent event) {
        //TODO
    }

    /**
     * Get contents from input and parse into model object
     */
    private void parseInput(){

        try {

            //TODO: should not be progMonDial, as it opens before Workbench.
            //Maybe a WorkbenchProgress or just a BG thread?
            new ProgressMonitorDialog( getSite().getShell() )
                .run( false, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) 
                       throws InvocationTargetException, InterruptedException {

                    IFile file = getFileFromInput();
                    if (file == null) {
                        logger.debug("Could not get file from editor input.");
                        //TODO: Close editor?
                        return;
                    }

                    InputStream instream;
                    try {
                        instream = file.getContents();
                        
                        CDK10Manager manager= new CDK10Manager();
                        List<CDK10Reaction> reactionList = manager.loadReactions(instream);
	                    logger.debug("In editor: " + reactionList.size() + " reactions.");
	
	                    monitor.beginTask("Reading RDFile...", reactionList.size()+1);
	
	                    ArrayList<ReactionTableEntry> newlist 
	                        = new ArrayList<ReactionTableEntry>();
	                    int count = 0;
	                    for (CDK10Reaction reaction : reactionList) {
	
	                        Map<Object, Object> props = reaction.getReaction().getProperties();
	
	                        for (Object obj : props.keySet()){
	//                            System.out.println("Key: '" + obj.toString() + "'; val: '" + props.get(obj) + "'" );
	                            if (obj instanceof String) {
	                                String key = (String) obj;
	                                if (!(propHeaders.contains(key))){
	                                    propHeaders.add(key);
	                                    logger.debug("Header added: " + key);
	                                }
	                            }
	                        }
	
	                        //Read vals for this molecule
	                        ArrayList<Object> vals=new ArrayList<Object>();
	                        for (String key : propHeaders){
	                            Object obj = reaction.getReaction().getProperty(key);
	                            vals.add(obj);
	                        }
	
	                        ReactionTableEntry entry = new ReactionTableEntry( count, 
	                            		reaction.getReaction(),vals.toArray() );
	                        newlist.add(entry);
	                        monitor.worked(1);
	                        count++;
	                    }
	                    setEntries(newlist.toArray(new ReactionTableEntry[0]));
	
	                    monitor.done();
	
	                    } catch (CoreException e) {
	                        throw new InvocationTargetException(e);
	                    } catch (IOException e) {
	                        throw new InvocationTargetException(e);
	                    } catch (BioclipseException e) {
	                        throw new InvocationTargetException(e);
	                    }
                	}
                });

        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get IFIle from editorInput
     * @return
     */
    private IFile getFileFromInput() {

        IEditorInput input = getEditorInput();
        if (!(input instanceof IFileEditorInput)) {
            return null;
        }
        IFileEditorInput finput = (IFileEditorInput) input;

        IFile file = finput.getFile();
        if ( !(file.exists()) ){
            return null;
        }
        return file;
    }
    
    public IChemModel getNextModel() {

        System.out.println("Current model index: " + getCurrentModel());
        
        int ix=getCurrentModel();
        if (ix<0) ix=0;

        //get next
        ix++;

        //Handle case when we have no selected index (like first time)
        if (ix>=entries.length){
            logger.debug( "Too high index!" );
            ix=entries.length-1;
        }
        if (ix<0) ix=0;
        
        setCurrentModel( ix );

        //Set selection in table
        tablePage.setSelection( new ReactionEntitySelection(entries[ix]) );

        return getChemModelByIndex( ix );
  
    }


    public IChemModel getPrevModel() {

        int ix = getCurrentModel();
        if (ix<0) ix=0;

        //get previous
        ix--;

        //Handle case when we have no selected index (like first time)
        if (ix >= entries.length) {
            logger.debug( "Too high index!" );
            ix = entries.length - 1;
        }
        if (ix<0) ix=0;
        
        setCurrentModel( ix );

        //Set selection in table
        tablePage.setSelection( new ReactionEntitySelection(entries[ix]) );

        return getChemModelByIndex( ix );
    }

    public IChemModel getModel( int ix ) {

        if (ix<0) ix=0;
        
        setCurrentModel( ix );

        //Set selection in table
        tablePage.setSelection( new ReactionEntitySelection(entries[ix]) );

        return getChemModelByIndex( ix );
    }


    public Object getAdapter(Class adapter) {
        
        if (IContentOutlinePage.class.equals(adapter)) {
            if (fOutlinePage == null) {
                fOutlinePage= new RDFOutlinePage(getEditorInput(), this);
            }
            return fOutlinePage;
        }
        return super.getAdapter(adapter);
    }
    
}
