/*******************************************************************************
 * Copyright (c) 2005-2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Kuhn <shk3@users.sf.net> - original implementation
 *     Carl <carl_marak@users.sf.net>  - converted into table
 *     Ola Spjuth                      - minor fixes
 *     Egon Willighagen                - made into a SWT widget
 *******************************************************************************/
package net.bioclipse.cdk10.jchempaint.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * SWT widget that views molecules using CDK's JChemPaint viewing engine.
 */
public class JChemPaintWidget extends Canvas {
    
    private Renderer2D renderer;
    private IAtomContainer molecule;
    private HashMap coordinates = new HashMap();
    
    private final static int compactSize = 200;
    
    // simple caching: only create a new Image if:
    // a) dimensions have changed, or
    // b) the molecule has changed
    private Dimension oldDimensions;
    private IAtomContainer oldMolecule;
    
    /**
     * The constructor.
     */
    public JChemPaintWidget(Composite parent, int style) {
        super(parent, style);
        
        renderer = new Renderer2D(new Renderer2DModel());
        Dimension screenSize = new Dimension(this.getSize().x, this.getSize().y);
        renderer.getRenderer2DModel().setBackgroundDimension(screenSize);
        renderer.getRenderer2DModel().setDrawNumbers(false);
        setCompactedNess(screenSize);
        
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                JChemPaintWidget.this.widgetDisposed(event);
            }
        });
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                JChemPaintWidget.this.paintControl(event);
            }
        });
        addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event) {
                JChemPaintWidget.this.controlResized(event);
            }
        });
    }

    public void setAtomContainer(IAtomContainer molecule) throws IllegalArgumentException {
        if (!GeometryTools.has2DCoordinates(molecule)) {
            throw new IllegalArgumentException("The AtomContainer does not contain 2D coordinates.");
        }
        this.molecule = molecule;
    }
    
    public Renderer2DModel getRendererModel() {
        return renderer.getRenderer2DModel();
    }
    
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return new Point(200, 200);
    }
    
    private void widgetDisposed(DisposeEvent event) {
        molecule = null;
        renderer = null;
    }
    
    private void controlResized(ControlEvent event) {}
    
    private void paintControl(PaintEvent event) {
        if (molecule == null) {
//            System.out.println("Molecule == NULL; not drawing molecule");
            return;
        } else {
//            System.out.println("Drawing molecule...");
        }
        
        int xsize = this.getSize().x;
        int ysize = this.getSize().y;
        if (molecule == oldMolecule &&
            oldDimensions.width == this.getSize().x &&
            oldDimensions.height == this.getSize().y) {
//            System.out.println(("Nothing has changed. Done creating new Image."));
            return;
        }
        if (oldDimensions == null ||
            oldDimensions.width != this.getSize().x ||
            oldDimensions.height != this.getSize().y) {
            oldDimensions = new Dimension(xsize,ysize);
        }
        setCompactedNess(oldDimensions);

        BufferedImage bufImage = new BufferedImage(
                xsize, ysize, BufferedImage.TYPE_INT_RGB );

        Graphics graphics = bufImage.createGraphics();
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, xsize, ysize );

        try {
            GeometryTools.translateAllPositive(molecule, coordinates);
            GeometryTools.scaleMolecule(molecule, oldDimensions, 0.8, coordinates);          
            GeometryTools.center(molecule, oldDimensions, coordinates);
//            GeometryTools.translateAllPositive(molecule, coordinates);
//            GeometryTools.scaleMolecule(molecule, oldDimensions, 0.8, coordinates);          
//            GeometryTools.center(molecule, oldDimensions, coordinates);

            renderer.getRenderer2DModel().setRenderingCoordinates(coordinates);
            renderer.getRenderer2DModel().setBackgroundDimension(oldDimensions);
            renderer.paintMolecule(
                molecule, 
                (Graphics2D)graphics,
                false, true
            );

            Image swtimage = new Image(
                Display.getDefault(),
                convertToSWT(bufImage)
            );

            this.setBackgroundImage(swtimage);
            
            this.oldMolecule = this.molecule;
        } catch (Exception exception) {
            System.out.println("Could not create 2D coordinate, and cannot display the best structure sofar.");
            System.out.println("Error: " + exception.getMessage());
            exception.printStackTrace();
        }
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


}