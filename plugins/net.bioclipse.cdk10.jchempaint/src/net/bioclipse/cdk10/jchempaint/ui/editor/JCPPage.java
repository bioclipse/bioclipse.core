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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * An EditorPage for JchemPaint
 *
 */
public class JCPPage extends EditorPart
    implements IJCPEditorPart, IChemObjectListener, ICDKChangeListener,
               MouseMotionListener, ISelectionChangedListener, ISelectionListener{

    //The body of the editor
    private Composite body;
    
    // JCP fields
    private IChemModel model;
    private DrawingPanel drawingPanel;
    private JChemPaintModel jcpModel;
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

    
    public IAtomColorer getColorer() {
        return colorer;
    }

    
    public void setColorer( IAtomColorer colorer ) {
        this.colorer = colorer;
    }

    public JCPPage() {
        super();
    }
    
    public JCPPage(IAtomColorer colorer) {
        super();
        this.colorer=colorer;
    }

    @Override
    public void createPartControl(Composite parent) {
        
        body = new JCPComposite(parent, SWT.EMBEDDED | SWT.H_SCROLL | SWT.V_SCROLL);
        GridLayout layout = new GridLayout();
        body.setLayout(layout);
        if (fillWithJCP(body)) {
            cl = new JCPControlListener(this);
            body.addControlListener(cl);
            jcpScrollBar = new JCPScrollBar(this, true, true);
        }
        else {
            //TODO open message box stating "no valid file - could not be opened with JCP"
        }
        body.addFocusListener(new JCPCompFocusListener((JCPComposite) body));

        if (colorer!=null)
            drawingPanel.setAtomColorer( colorer );
        
        getSite().getPage().addSelectionListener(this);
    }

    /**
     * Adds the the drawingPanel to the jcpComposite and fills jcp with model
     * @param composite
     * @return 
     * @throws JCPException 
     */
    private boolean fillWithJCP(Composite composite) {
        //Get model from the editorInput that is the parsed resourceString
        try {
            model = getModelFromEditorInput();
        } catch (BioclipseException e) {
            e.printStackTrace();
            return false;
        }
        
        if (model != null) {
            model.addListener(this);
            drawingPanel = new DrawingPanel(composite.getDisplay());
            jcpModel = new JChemPaintModel(model);
            jcpModel.getControllerModel().setAutoUpdateImplicitHydrogens(true);
            jcpModel.getRendererModel().setShowEndCarbons(true);
            HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");

            List<IAtomContainer> acS = ChemModelManipulator.getAllAtomContainers(model);
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
            drawingPanel.setJChemPaintModel(jcpModel);
            drawingPanel.addMouseMotionListener(this);
//            drawingPanel.addMouseListener(this);
            java.awt.Frame jcpFrame = SWT_AWT.new_Frame(composite);
            jcpFrame.add(drawingPanel);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the IChemModel from the parsedResource
     * @return
     * @throws BioclipseException 
     */
    private IChemModel getModelFromEditorInput() throws BioclipseException{
        
        Object file = input.getAdapter(IFile.class);
        if (!(file instanceof IFile)) {
            throw new BioclipseException(
                    "Invalid editor input: Does not provide an IFile");
        }

        IFile inputFile = (IFile) file;
        
        try {
            InputStream instream=inputFile.getContents();
            
            MDLV2000Reader reader = new MDLV2000Reader(instream);
            return (IChemModel)reader.read(new ChemModel());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
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

    public void stateChanged(IChemObjectChangeEvent event) {
        if(event.getSource() instanceof Renderer2DModel) {
            getDrawingPanel().repaint();
            if (!this.isDirty() && jcpModel.isModified()) {
                setDirty(true);
            }
        }
    }

    public void stateChanged(EventObject event) {
        if (!this.isDirty() && jcpModel.isModified()) {
            setDirty(true);
        }
    }

    private void setDirty(boolean bool) {
        this.isDirty = bool;
        fireSetDirtyChanged();
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


    private void reactOnSelection(ISelection selection) {

        if (jcpModel==null) return;

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

    
}
