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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import net.bioclipse.core.business.ChemicalStructureProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.openscience.cdk.Atom;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * A class that represents an item in a StructureTable. It consists of an
 * AtomContainer and a String[] of properties.
 * @author ola
 *
 */
public class StructureTableEntry implements ChemicalStructureProvider {

    private Renderer2D renderer;
    private IAtomContainer molecule;
    private HashMap coordinates = new HashMap();

    private final static int compactSize = 150;

    //Values in table
    Object[] columns;

    //The cached image
    Image image;
    
    //The index of the entry
    int index;
    

    public StructureTableEntry(int index, IAtomContainer molecule, Object[] objects) {
        this.molecule=molecule;
        this.columns=objects;
        this.index=index;
    }


    /**
     * Draw based on object in event
     * @param event
     */
    public void draw(Event event) {

        //If on structure column, draw structure
        if (event.index==StructureTablePage.STRUCTURE_COLUMN){
            if (image!=null)
                event.gc.drawImage(image, event.x, event.y);
            else{
                image=computeStructureImage(event);
                if (image==null){
                    System.out.println("Could not create structure image.");
                    event.gc.drawText("Error", event.x, event.y);
                    return;
                }
                event.gc.drawImage(image, event.x, event.y);
            }
        }
        //If any otehr column, draw text
        else{
            drawProperty(event);
        }


    }

    /**
     * Render all except structure as text
     * @param event
     */
    private void drawProperty(Event event) {

        //Draw index on column 0
        if (event.index==StructureTablePage.INDEX_COLUMN){
            event.gc.drawText(String.valueOf( index ), event.x, event.y);
            return;
        }

        
        if (event.index>=columns.length+2){
            event.gc.drawText("???", event.x, event.y);
            return;
        }

        //Minus 2 because of 0=index, 1= structure
        String str=String.valueOf(columns[event.index-2]);
        if (str==null)
            str="N/A";
        event.gc.drawText(str, event.x, event.y);
    }


    /**
     * Render structure using JCPWidget as Image
     * @param event
     */
    private Image computeStructureImage(Event event) {

        int xsize = event.width;
        int ysize = event.height;

        int xsizeIX = 0;  //width of index column

        //Get width from widget column
        if (event.widget instanceof Table) {
            Table table = (Table) event.widget;
            xsize=table.getColumn(StructureTablePage.STRUCTURE_COLUMN).getWidth();
            xsizeIX=table.getColumn(StructureTablePage.INDEX_COLUMN).getWidth();
        }

        renderer = new Renderer2D(new Renderer2DModel());
        Dimension screenSize = new Dimension(xsize, ysize);
        renderer.getRenderer2DModel().setBackgroundDimension(screenSize);
        renderer.getRenderer2DModel().setDrawNumbers(false);
        setCompactedNess(screenSize);


        BufferedImage bufImage = new BufferedImage(
                xsize, ysize, BufferedImage.TYPE_INT_RGB );

        Graphics graphics = bufImage.createGraphics();
        graphics.setColor( Color.WHITE );
        
        graphics.fillRect( 0, 0, xsize, ysize );

        IAtomContainer drawMolecule=molecule;

        //If no 2D coords
        if (GeometryTools.has2DCoordinates(molecule)==false){
            //Test if 3D coords
            if (GeometryTools.has3DCoordinates(molecule)==true){
                //Collapse on XY plane
                try {
                    drawMolecule=(IAtomContainer) molecule.clone();

                    //For each molecule,
                    for (int i=0; i< drawMolecule.getAtomCount(); i++){
                        IAtom atom=drawMolecule.getAtom(i);
                        Point3d p3=atom.getPoint3d();
                        Point2d p2=new Point2d();
                        p2.x=p3.x;
                        p2.y=p3.y;
                        atom.setPoint3d(null);
                        atom.setPoint2d(p2);
                    }
                } catch (CloneNotSupportedException e) {
                    return null;
                }

            }
        }

        GeometryTools.translateAllPositive(drawMolecule,coordinates);
        GeometryTools.scaleMolecule(drawMolecule, screenSize, 0.8,coordinates);
        GeometryTools.center(drawMolecule, screenSize,coordinates);

        renderer.getRenderer2DModel().setRenderingCoordinates(coordinates);
        renderer.getRenderer2DModel().setBackgroundDimension(screenSize);
        renderer.paintMolecule(
                drawMolecule,
                (Graphics2D)graphics,
                false, true
        );

        Image swtimage = new Image(
                Display.getDefault(),
                convertToSWT(bufImage)
        );

        return swtimage;
    }


    private void setCompactedNess(Dimension dimensions) {
        if (dimensions.height < compactSize ||
                dimensions.width < compactSize) {
            renderer.getRenderer2DModel().setIsCompact(true);
        } else {
            renderer.getRenderer2DModel().setIsCompact(false);
        }
    }

    private static ImageData convertToSWT( BufferedImage bufferedImage ) {

        ColorModel colorModel = bufferedImage.getColorModel();
        if ( colorModel instanceof DirectColorModel )
            return convertDirectColorImageToSWT( bufferedImage );

        if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            return convertIndexColorModelToSWT( bufferedImage );
        }

        throw new IllegalArgumentException("Unrecognized color model " +
                bufferedImage.getColorModel());
    }

    private static ImageData convertDirectColorImageToSWT(
            BufferedImage bufferedImage ) {

        DirectColorModel colorModel = (DirectColorModel)
        bufferedImage.getColorModel();

        PaletteData palette = new PaletteData(
                colorModel.getRedMask(),
                colorModel.getGreenMask(),
                colorModel.getBlueMask() );

        ImageData data = new ImageData(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                colorModel.getPixelSize(),
                palette);

        WritableRaster raster = bufferedImage.getRaster();

        int[] pixelArray = new int[3];
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                raster.getPixel(x, y, pixelArray);
                int pixel = palette.getPixel(
                        new RGB( pixelArray[0],
                                pixelArray[1],
                                pixelArray[2]) );
                data.setPixel(x, y, pixel);
            }
        }

        return data;
    }

    private static ImageData convertIndexColorModelToSWT(
            BufferedImage bufferedImage ) {

        IndexColorModel colorModel = (IndexColorModel)
        bufferedImage.getColorModel();

        int size = colorModel.getMapSize();

        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];

        colorModel.getReds(reds);
        colorModel.getGreens(greens);
        colorModel.getBlues(blues);

        RGB[] rgbs = new RGB[size];

        for (int i = 0; i < rgbs.length; i++)
            rgbs[i] = new RGB(
                    reds[i] & 0xFF,
                    greens[i] & 0xFF,
                    blues[i] & 0xFF );

        PaletteData palette = new PaletteData(rgbs);
        ImageData data = new ImageData(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                colorModel.getPixelSize(),
                palette );

        data.transparentPixel = colorModel.getTransparentPixel();
        WritableRaster raster = bufferedImage.getRaster();

        int[] pixelArray = new int[1];
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                raster.getPixel(x, y, pixelArray);
                data.setPixel(x, y, pixelArray[0]);
            }
        }
        return data;
    }


    /**
     * Return the AtomContainer for access via ChemicalStructureProvider
     * for e.g. Jmol view to display it.
     */
    public Object getMoleculeImpl() {
        return molecule;
    }



}
