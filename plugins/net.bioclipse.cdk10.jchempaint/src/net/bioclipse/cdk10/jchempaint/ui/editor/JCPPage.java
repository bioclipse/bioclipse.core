/*******************************************************************************
 * Copyright (c) 2006-2007 Tobias Helmus, Stefan Kuhn
 *               2006-2008 Ola Spjuth, Egon Willighagen
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tobias Helmus <tohel@users.sf.net>
 *     Stefan Kuhn
 *     Ola Spjuth
 *     Egon Willighagen <egonw@users.sf.net>
 ******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk10.jchempaint.outline.CDKChemObject;
import net.bioclipse.core.business.BioclipseException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.PopupController2D;
import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;

/**
 * An EditorPage for JchemPaint
 *
 */
public class JCPPage extends EditorPart
    implements IJCPEditorPart, IChemObjectListener, ICDKChangeListener,
               MouseMotionListener, ISelectionChangedListener, ISelectionListener{

    private static final Logger logger = Logger.getLogger(JCPPage.class);

    //The body of the editor
    private Composite body;
    
    
    /**
     * The ChemModel, containing the molecule
     */
    private IChemModel chemModel;

    /**
     * The JCP model, containing the model
     */
    private JChemPaintModel jcpModel;

    // More JCP fields
    private DrawingPanel drawingPanel;
    private JCPScrollBar jcpScrollBar;
    private boolean isDirty = false;
    public ControlListener cl;

    //Store for access on page
    IEditorSite site;
    IEditorInput input;
    
    //The underlying file
    IFile inputFile;;

    //Store highlighted selections
    private IAtomContainer selectedContent = null;
    
    //Custom colorer for the renderer
    private IAtomColorer colorer;

    //The AWT frame
    java.awt.Frame jcpFrame;
    
    /*
     *  GETTERS AND SETTERS
     */

    public IChemModel getChemModel() {
        return chemModel;
    }
    
    public void setChemModel( IChemModel chemModel ) {
        this.chemModel = chemModel;
    }
    
    public JChemPaintModel getJcpModel() {
        return jcpModel;
    }
    
    public void setJcpModel( JChemPaintModel jcpModel ) {
        this.jcpModel = jcpModel;
    }

    public IAtomColorer getColorer() {
        return colorer;
    }
    
    public void setColorer( IAtomColorer colorer ) {
        this.colorer = colorer;
    }
    
    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    public JChemPaintModel getJCPModel() {
        return jcpModel;
    }

    public Composite getJcpComposite() {
        return body;
    }

    public JCPScrollBar getJcpScrollBar() {
        return jcpScrollBar;
    }


    /*
     *  CONSTRUCTORS
     */
    
    IChemModel newModel;

    public JCPPage(IChemModel chemModel_in) {
        super();
        newModel=chemModel_in;
    }
    
    public JCPPage(IChemModel chemModel_in, IAtomColorer colorer) {
        this(chemModel_in);
        this.colorer=colorer;
    }

    @Override
    public void createPartControl(Composite parent) {
        
        body = new JCPComposite(parent, SWT.EMBEDDED | SWT.H_SCROLL | SWT.V_SCROLL);
        GridLayout layout = new GridLayout();
        body.setLayout(layout);

        body.addFocusListener(new JCPCompFocusListener((JCPComposite) body));
        
        drawingPanel = new DrawingPanel(body.getDisplay());

        jcpScrollBar = new JCPScrollBar(this, true, true);

        
        //Update JCPModel from chemModel, get from editor input
        try {
            if (newModel==null){
                logger.debug( "No molecule provided by editor" );
                return;
            }
            updateJCPModel(newModel);
        } catch ( BioclipseException e1 ) {
            logger.debug( "No valid JCPModel - could not be opened with JCP" );
            showMessage( "No valid JCPModel - could not be opened with JCP" );
            return;
        }


        

        //Listen for Eclipse selections
        getSite().getPage().addSelectionListener(this);
    }

    


    private void showMessage(String message) {
        MessageDialog.openInformation(
                                      getEditorSite().getShell(),
                                      "Message",
                                      message);
    }

    /**
     * We need to clean up. This is far from finished code: TODO
     */
    @Override
    public void dispose() {
        
        jcpModel.getRendererModel().removeCDKChangeListener( this );

        getSite().getPage().removeSelectionListener(this);
        drawingPanel.removeAll();
        super.dispose();
    }

    /**
     * Sets the JCPModel from the chemModel and takes care of all listeners
     * @param composite
     * @return 
     * @throws BioclipseException 
     * @throws JCPException 
     */
    public void updateJCPModel(IChemModel newModel) throws BioclipseException {
        
        //TODO: Remove any old listeners to chemModel
        //FIXME!!
        
        //Remove old listeners and components
        if (jcpFrame!=null)
            jcpFrame.removeAll();

        if (cl!=null)
            body.removeControlListener( cl);


        //Cache new model in page
        chemModel=newModel;

        //Add listeners and create JCPModel from ChemModel
        chemModel.addListener(this);

        jcpModel = new JChemPaintModel(chemModel);
        jcpModel.getControllerModel().setAutoUpdateImplicitHydrogens(true);
        jcpModel.getRendererModel().setShowEndCarbons(true);
        HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");

        List<IAtomContainer> acS = ChemModelManipulator.getAllAtomContainers(chemModel);
        Iterator<IAtomContainer> molsI = acS.iterator();
        while(molsI.hasNext()){
            IMolecule molecule = (IMolecule)molsI.next();
            if (molecule != null)
            {
                try{
                    hydrogenAdder.addImplicitHydrogensToSatisfyValency(molecule);
                }catch(Exception ex){
                    //do nothing
                }
            }
        }

        PopupController2D inputAdapter = new BCJCPPopupController(
                                                                  (ChemModel) jcpModel.getChemModel(),
                                                                  jcpModel.getRendererModel(),
                                                                  jcpModel.getControllerModel(), 
                                                                  null, null, 
                                                                  null, new HashMap()
        );
        inputAdapter.addCDKChangeListener(this);
        jcpModel.getRendererModel().addCDKChangeListener(this);
        jcpModel.getControllerModel().setDrawMode(Controller2DModel.LASSO);
//      drawingPanel.addMouseListener(this);


        //More listeners
        cl = new JCPControlListener(this);
        body.addControlListener(cl);

        //Set up drawing panel for JCP
        drawingPanel = new DrawingPanel(body.getDisplay());
        drawingPanel.setJChemPaintModel(jcpModel);
        jcpFrame = SWT_AWT.new_Frame(body);
        
        //Add the new drawingpanel to JCPFrame
        jcpFrame.add(drawingPanel);

        drawingPanel.addMouseMotionListener(this);
        
        //If colorer exists, use it
        if (colorer!=null)
            drawingPanel.setAtomColorer( colorer );

        //FIXME: Right now, there's a problem with repaint if switched to this
        //from SDFileEditor.

        //Doesn't help :-(
        jcpModel.fireChange(new ChemObjectChangeEvent(getJcpModel().getRendererModel()));
        drawingPanel.repaint();
        jcpFrame.repaint();
        setFocus();
        body.redraw();
        
        
    }

    
    

    /**
     * Not used
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    /**
     * Not used
     */
    @Override
    public void doSaveAs() {
    }

    @Override
    public IEditorSite getEditorSite() {
        return site;
    }

    @Override
    public IEditorSite getSite() {
        return site;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        this.site=site;
        this.input=input;
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        //TODO: change when available
        return false;
    }


    @Override
    public void setFocus() {
        body.setFocus();
    }


    public void stateChanged(IChemObjectChangeEvent event) {
        if(event.getSource() instanceof Renderer2DModel) {
            getDrawingPanel().repaint();
            if (!this.isDirty() && jcpModel.isModified()) {
                setDirty(true);
            }
        }
    }

    public void stateChanged(EventObject event) {
        //Added by Ola to get a repaint on stateChanged.
        //Don't know if this is the way to go, but it works
        //TODO: Verify solution
        if(event.getSource() instanceof Renderer2DModel) {
            getDrawingPanel().repaint();
        }
        if (!this.isDirty() && jcpModel.isModified()) {
            setDirty(true);
        }
    }

    public void setDirty(boolean bool) {
        boolean hasChanged = this.isDirty == bool;
        this.isDirty = bool;
        if (!bool) jcpModel.resetIsModified();
        if (hasChanged) fireSetDirtyChanged();
    }

    private void fireSetDirtyChanged() {
        Runnable r= new Runnable() {
            public void run() {
                firePropertyChange(PROP_DIRTY);
            }
        };
        Display fDisplay = getSite().getShell().getDisplay();
        fDisplay.asyncExec(r);

    }

    public void mouseDragged(MouseEvent e) {
        drawingPanel.repaint();        
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    
    public void selectionChanged(SelectionChangedEvent event) {
        reactOnSelection(event.getSelection());
    }


    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if(part==this)
            return;
        reactOnSelection(selection);
    }


    /**
     * React upon selections in Eclipse
     * @param selection
     */
    private void reactOnSelection(ISelection selection) {

        if (jcpModel==null) return;
        
        if (!( selection instanceof IStructuredSelection )) return;

        //Check if selection includes atoms/bonds and highlight in that case
        Iterator it=((IStructuredSelection)selection).iterator();

        if (it.hasNext()) {

            if (selectedContent == null) {
                selectedContent = new AtomContainer();
            } else {
                selectedContent.removeAllElements();
            }

            while( it.hasNext()){
                Object obj=it.next();
                if(obj instanceof CDKChemObject){
                    obj=((CDKChemObject)obj).getChemobj();
                    if (obj instanceof IAtom){
                        selectedContent.addAtom((IAtom)obj);
                    }
                    if (obj instanceof IBond){
                        selectedContent.addBond((IBond)obj);
                    }
                }
            }
            jcpModel.getRendererModel().setExternalSelectedPart(selectedContent);
            this.getDrawingPanel().repaint();
        }

        
    }

    public void goNextModel() {

        // TODO Auto-generated method stub
        
        
        
    }


    
}
