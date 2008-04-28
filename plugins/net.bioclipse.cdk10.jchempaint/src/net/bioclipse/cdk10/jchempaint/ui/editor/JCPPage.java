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
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * An EditorPage for JchemPaint
 *
 */
public class JCPPage extends EditorPart
	implements IJCPEditorPart, IChemObjectListener, ICDKChangeListener,
	           MouseMotionListener {

	//The body of the editor
	private Composite body;
	
	// JCP fields
	private IChemModel model;
	private DrawingPanel drawingPanel;
	private JChemPaintModel jcpModel;
	private IEditorInput editorInput;
	private JCPScrollBar jcpScrollBar;
	private boolean isDirty = false;
	public ControlListener cl;

	//Store for access on page
	IEditorSite site;
	IEditorInput input;
	
	//The underlying file
	IFile inputFile;;
	
	public JCPPage() {
		super();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		body = new Composite(parent, SWT.NONE);
//		FillLayout layout=new FillLayout();
//		body.setLayout(layout);
//		Text txt=new Text(body, SWT.NONE);
//		txt.setText("This is where the structure should be visualized. TODO: Implement");

		body = new JCPComposite(parent, SWT.EMBEDDED | SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayout layout = new GridLayout();
		body.setLayout(layout);
		if (fillWithJCP(body)) {
			cl = new JCPControlListener(this);
			body.addControlListener(cl);
			jcpScrollBar = new JCPScrollBar(this, true, true);
		}
		else {
			//TODO open message box stating "no valid file - could not be opnened with JCP"
		}
		body.addFocusListener(new JCPCompFocusListener((JCPComposite) body));
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

			jcpModel.getRendererModel().addCDKChangeListener(this);
			jcpModel.getControllerModel().setDrawMode(Controller2DModel.LASSO);
			drawingPanel.setJChemPaintModel(jcpModel);
			drawingPanel.addMouseMotionListener(this);
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
		//TODO: implement
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		//TODO: change when available
		return false;
	}


	@Override
	public void setFocus() {
		//TODO: change when available
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
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(EventObject event) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
