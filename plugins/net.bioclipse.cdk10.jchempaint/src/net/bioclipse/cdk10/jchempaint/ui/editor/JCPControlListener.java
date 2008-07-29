/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * Control Listener for JCP_SWT 
 * 
 * @author Miguel Rojas
 *
 */
public class JCPControlListener implements ControlListener {

    private IJCPEditorPart jcpEditor;
    
    /**
     * Constructor of JCPMultiPageEditor
     * 
     * @param editor  JCPMultiPageEditor value
     */
    public JCPControlListener(IJCPEditorPart editor){
        this.jcpEditor = editor;
    }
    /**
     * Sent when the location (x, y) of a control changes relative
     * to its parent (or relative to the display, for <code>Shell</code>s).
     *
     * @param e an event containing information about the move
     */
    public void controlMoved(ControlEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Sent when the size (width, height) of a control changes.
     *
     * @param e an event containing information about the resize
     */
    public void controlResized(ControlEvent e)
    {
        rendererPositions();
        jcpEditor.getDrawingPanel().repaint();
    }
    
    /**
     * rendering the positions of the compound within of the Composite
     *
     */
    private void rendererPositions(){
        IChemModel editorModel = jcpEditor.getJcpModel().getChemModel();
        
        if(editorModel.getMoleculeSet() != null && 
        		editorModel.getMoleculeSet().getAtomContainer(0) != null)
        	renderPosition_IAtomContainer(editorModel.getMoleculeSet().getAtomContainer(0));
        else if(editorModel.getReactionSet() != null){
        	IReaction reaction = editorModel.getReactionSet().getReaction(0);
        	renderPosition_IReaction(reaction);
        }
    }
    
    /**
     * Rendering the position of the IAtomContainer within of the Composite.
     * 
     * @param molecule The IAtomContainer to renderer
     */
    private void renderPosition_IAtomContainer(IAtomContainer molecule){
        Renderer2DModel r2dm = jcpEditor.getJcpModel().getRendererModel();

        double scaleFactor = GeometryTools.getScaleFactor(molecule ,r2dm.getBondLength(),r2dm.getRenderingCoordinates());
        GeometryTools.scaleMolecule(molecule, scaleFactor,r2dm.getRenderingCoordinates());
        
        /* height*/
        double[] result = getRectangle(molecule,r2dm);
        int height = jcpEditor.getJcpComposite().getClientArea().height;
        int MoleculeHeight = ((int)result[1]-(int)result[3]);
        
        
        /* width */
        int width = jcpEditor.getJcpComposite().getClientArea().width;
        int MoleculeWidth = ((int)result[0]-(int)result[2]);

        //System.err.println("w h "+width+"  "+height);
        
        Point size = new Point(MoleculeHeight,MoleculeWidth);
        Rectangle rect = jcpEditor.getJcpComposite().getClientArea();
        ScrollBar vScroll = jcpEditor.getJcpScrollBar().vertical;
        ScrollBar hScroll = jcpEditor.getJcpScrollBar().horizontal;
        
        int space = 20;
        int resultPlus = space;
        int resultH = (int)result[1]+resultPlus;
        if(height>(MoleculeHeight+space)){
            resultPlus = (height-(MoleculeHeight))/2;
            vScroll.setVisible(false);
            resultH=height;
        }else {
            vScroll.setVisible(true);
            vScroll.setPageIncrement((size.x+space)/10);
            vScroll.setMaximum(size.x+space);
            vScroll.setThumb(Math.min(size.x+space, rect.height-space));
            resultH=size.x+space;
        }

        resultPlus = space;
        int resultW = (int)result[0]+resultPlus;
        if(width>(MoleculeWidth+space)){
            resultPlus = (width-(MoleculeWidth))/2;
            hScroll.setVisible(false);
            resultW=width;
        }else {
            hScroll.setVisible(true);
            hScroll.setMaximum(size.y+space);
            vScroll.setPageIncrement((size.y+space)/10);
            hScroll.setThumb(Math.min(size.y+space, rect.width-space));
            resultW=size.y+space;
        }
//        System.err.println(resultW+"  "+resultH);
        r2dm.setBackgroundDimension(new Dimension(resultW,resultH));
        GeometryTools.center(molecule, r2dm.getBackgroundDimension(),r2dm.getRenderingCoordinates());
//        for(int i=0;i<molecule.getAtomCount();i++){
//            System.err.println(i+" "+molecule.getAtom(i).getPoint2d()+"  "+((Point2d)r2dm.getRenderingCoordinate(molecule.getAtom(i))));
//        }
    
    }
    /**
     * Rendering the position of the IReaction within of the Composite.
     * 
     * @param reaction The IReaction to renderer
     */
    private void renderPosition_IReaction(IReaction reaction){
    	Renderer2DModel r2dm = jcpEditor.getJcpModel().getRendererModel();
    	List<IAtomContainer> acList = ReactionManipulator.getAllAtomContainers(reaction);
    	for(Iterator<IAtomContainer> it = acList.iterator(); it.hasNext();){
    		IAtomContainer molecule = it.next();
    		double scaleFactor = GeometryTools.getScaleFactor(molecule ,r2dm.getBondLength(),r2dm.getRenderingCoordinates());
            GeometryTools.scaleMolecule(molecule, scaleFactor,r2dm.getRenderingCoordinates());
    	}
    	/* height*/
        double[] result = getRectangle(reaction,r2dm);
        int height = jcpEditor.getJcpComposite().getClientArea().height;
        int reactionHeight = ((int)result[1]-(int)result[3]);

        /* width */
        int width = jcpEditor.getJcpComposite().getClientArea().width;
        int reactionWidth = ((int)result[0]-(int)result[2]);
        
        Point size = new Point(reactionHeight,reactionWidth);
        Rectangle rect = jcpEditor.getJcpComposite().getClientArea();
        ScrollBar vScroll = jcpEditor.getJcpScrollBar().vertical;
        ScrollBar hScroll = jcpEditor.getJcpScrollBar().horizontal;
        int space = 20;
        int resultPlus = space;
        int resultH = (int)result[1]+resultPlus;
        if(height>(reactionHeight+space)){
            resultPlus = (height-(reactionHeight))/2;
            vScroll.setVisible(false);
            resultH=height;
        }else {
            vScroll.setVisible(true);
            vScroll.setPageIncrement((size.x+space)/10);
            vScroll.setMaximum(size.x+space);
            vScroll.setThumb(Math.min(size.x+space, rect.height-space));
            resultH=size.x+space;
        }

        resultPlus = space;
        int resultW = (int)result[0]+resultPlus;
        if(width>(reactionWidth+space)){
            resultPlus = (width-(reactionWidth))/2;
            hScroll.setVisible(false);
            resultW=width;
        }else {
            hScroll.setVisible(true);
            hScroll.setMaximum(size.y+space);
            vScroll.setPageIncrement((size.y+space)/10);
            hScroll.setThumb(Math.min(size.y+space, rect.width-space));
            resultW=size.y+space;
        }
        r2dm.setBackgroundDimension(new Dimension(resultW,resultH));
    	GeometryTools.center(reaction, r2dm.getBackgroundDimension(),r2dm.getRenderingCoordinates());
            
    	
    }
    /**
     * get the rectangle position of a molecule
     * 
     * @param mol  the molecule to extract the position
     * @return     coordenates of the rectangle
     */
    public double[] getRectangle(IAtomContainer mol, Renderer2DModel r2dm){
        
        double maxX = Double.MAX_VALUE,maxY=Double.MAX_VALUE,minX=Double.MIN_VALUE,minY=Double.MIN_VALUE;
        for(int j = 0 ; j < mol.getAtomCount(); j++){
            IAtom atom = mol.getAtom(j);    
                
            if(maxX > ((Point2d)r2dm.getRenderingCoordinate(atom)).x)
                maxX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x;
            if(maxY > ((Point2d)r2dm.getRenderingCoordinate(atom)).y)
                maxY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y;
            
            if(minX < ((Point2d)r2dm.getRenderingCoordinate(atom)).x)
                minX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x;
            if(minY < ((Point2d)r2dm.getRenderingCoordinate(atom)).y)
                minY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y;
        
        }
        double[] result ={minX,minY,maxX,maxY}; 
        return result;
    }
    /**
     * get the rectangle position of a reaction
     * 
     * @param reaction  the reaction to extract the position
     * @return          coordenates of the rectangle
     */
    public double[] getRectangle(IReaction reaction, Renderer2DModel r2dm){
        
        double maxX = Double.MAX_VALUE,maxY=Double.MAX_VALUE,minX=Double.MIN_VALUE,minY=Double.MIN_VALUE;
        List<IAtomContainer> acList = ReactionManipulator.getAllAtomContainers(reaction);
        for(Iterator<IAtomContainer> it = acList.iterator(); it.hasNext();){
	    	IAtomContainer mol = it.next();
        	for(int j = 0 ; j < mol.getAtomCount(); j++){
	            IAtom atom = mol.getAtom(j);    
	                
	            if(maxX > ((Point2d)r2dm.getRenderingCoordinate(atom)).x)
	                maxX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x;
	            if(maxY > ((Point2d)r2dm.getRenderingCoordinate(atom)).y)
	                maxY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y;
	            
	            if(minX < ((Point2d)r2dm.getRenderingCoordinate(atom)).x)
	                minX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x;
	            if(minY < ((Point2d)r2dm.getRenderingCoordinate(atom)).y)
	                minY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y;
	        
	        }
        }
        double[] result ={minX,minY,maxX,maxY}; 
        return result;
    }

}
