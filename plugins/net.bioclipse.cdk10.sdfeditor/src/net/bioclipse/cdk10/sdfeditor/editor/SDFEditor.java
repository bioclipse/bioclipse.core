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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.bioclipse.cdk10.jchempaint.ui.editor.JCPPage;
import net.bioclipse.cdk10.sdfeditor.CDK10Manager;
import net.bioclipse.cdk10.sdfeditor.CDK10Molecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class SDFEditor extends FormEditor implements IResourceChangeListener, 
                                                IAdaptable/*, IShowEditorInpu*/{

    private static final Logger logger = Logger.getLogger(SDFEditor.class);

    //If used, if JCP should be shown
    private JCPPage jcpPage;

//    private TextEditor textEditor;
    private StructureTablePage tablePage;

    //Model for the editor: Based on CDK
    StructureTableEntry[] entries;
    ArrayList<String> propHeaders;


    public StructureTableEntry[] getEntries() {
        return entries;
    }


    public void setEntries(StructureTableEntry[] entries) {
        this.entries = entries;
    }


    @Override
    public void init(IEditorSite site, IEditorInput input)
    throws PartInitException {
        super.init(site, input);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        propHeaders=new ArrayList<String>();

        //Parse input with CDK
        parseInput();

        //Tables page
        tablePage=new StructureTablePage(this, propHeaders.toArray(new String[0]));

        //JCP page
        jcpPage=new JCPPage();

        //Texteditor. For now, not added due to memory consumption
//        textEditor = new TextEditor();

    }


    @Override
    protected void addPages() {

            
            try {
                addPage(tablePage/*, getEditorInput()*/);

                int ix=addPage(jcpPage, getEditorInput());
//                jcpPage.activateJCP();
                setPageText(ix, "Single entry");
                
//Texteditor not added
//            int index = addPage(textEditor, getEditorInput());
//            setPageText(index, textEditor.getTitle());

        } catch (PartInitException e) {
            LogUtils.debugTrace(logger, e);
        }


    }

    @Override
    protected void pageChange( int newPageIndex ) {
/*        
        if (getCurrentPage()==0){
            //We are in structureTable
            //Get selected index in table and set in JCP

            int ix=tablePage.getSelectedIndex( tablePage.getSelection() );

        }
        else if (getCurrentPage()==1){
            //We are in JCP
            //Get selected index in JCP and set in table
            Object obj=((IStructuredSelection)tablePage
                            .getSelection()).getFirstElement();
            
            StructureTableEntry entry=(StructureTableEntry)obj;

            IAtomContainer ac=(IAtomContainer)entry.getMoleculeImpl();
            
            IChemModel ml=new ChemModel();
            IMoleculeSet ms=new MoleculeSet();
            ms.addAtomContainer( ac );
            ml.setMoleculeSet( ms );
            
            try {
                jcpPage.updateJCPModel( ml);
            } catch ( BioclipseException e ) {
                logger.debug( "Cannot set new chemModel for JCP." );
            }
            
        }
    
        */
        super.pageChange( newPageIndex );
        
    }
    
    


    @Override
    public void doSave(IProgressMonitor monitor) {
        //TODO
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

    private void parseInput(){

        try {

            new ProgressMonitorDialog(getSite().getShell()).run(false, true, new IRunnableWithProgress(){

                public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {


                    IEditorInput input=getEditorInput();
                    if (!(input instanceof IFileEditorInput)) {
                        logger.debug("Not FIleEditorInput.");
                        //TODO: Close editor?
                        return;
                    }
                    IFileEditorInput finput = (IFileEditorInput) input;

                    IFile file=finput.getFile();
                    if (!(file.exists())){
                        logger.debug("File does not exist.");
                        //TODO: Close editor?
                        return;
                    }

                    InputStream instream;
                    try {
                        instream = file.getContents();
                        
                        CDK10Manager manager= new CDK10Manager();
                        List<CDK10Molecule> molList=manager.loadMolecules(instream);
                    logger.debug("In editor: " + molList.size() + " molecules.");

                    monitor.beginTask("Reading SDFile...", molList.size()+1);

                    ArrayList<StructureTableEntry> newlist=new ArrayList<StructureTableEntry>();

                    for (CDK10Molecule mol : molList){

                        Map<Object, Object> props=mol.getAtomContainer().getProperties();

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
                            Object obj=mol.getAtomContainer().getProperty(key);
                            vals.add(obj);
                        }

                        StructureTableEntry entry=new StructureTableEntry(mol.getAtomContainer(), vals.toArray());
                        newlist.add(entry);
                        monitor.worked(1);
                    }
                    setEntries(newlist.toArray(new StructureTableEntry[0]));

                    monitor.done();

                    } catch (CoreException e) {
                        throw new InvocationTargetException(e);
                    } catch (IOException e) {
                        throw new InvocationTargetException(e);
                    } catch (BioclipseException e) {
                        throw new InvocationTargetException(e);
                    }



            }});

        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

/*
    public void showEditorInput( IEditorInput editorInput ) {

        removePage( 0 );
        
        jcpPage=new JCPPage();
        
        try {
            int ix=addPage(jcpPage, editorInput);
//            jcpPage.activateJCP();
            setPageText(ix, "Structure");
            jcpPage.getDrawingPanel().repaint();
            //FIXME: does not repaint JCP properly!

        } catch (PartInitException e) {
            LogUtils.debugTrace(logger, e);
        }
        
    }
    */
}
