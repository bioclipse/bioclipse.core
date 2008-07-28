/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.widgets;

import java.util.ArrayList;

import nu.psnet.quickimage.core.ImageHolder;
import nu.psnet.quickimage.core.ImageOrganizer;
import nu.psnet.quickimage.core.QManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * @author Per Salomonsson
 * 
 */
public class QuickImageCanvas extends Canvas {
    private Image originalImage;
    private Image workingImage;
    private Image backImage;
    private Composite parent;
    private int clientw, clienth, imgx, imgy, imgw, imgh, scrolly, scrollx,
            mousex, mousey = 1;
    private Color COLOR_WIDGET_BACKGROUND;
    private boolean listenForMouseMovement = false;
    private Cursor handOpen, handClosed;
    private double zoomScale = 1;
    private QManager manager;

    public QuickImageCanvas(QManager manager, final Composite parent, int style) {
        super(parent, style | SWT.BORDER | SWT.NO_BACKGROUND | SWT.V_SCROLL
                | SWT.H_SCROLL);

        this.manager = manager;
        COLOR_WIDGET_BACKGROUND = getDisplay().getSystemColor(SWT.COLOR_WHITE);
        
        this.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                updateScrollbarPosition();
            }
        });

        this.addMouseListener(new MouseListener() {
            public void mouseDoubleClick(MouseEvent e) {
                eventMouseDoubleClick(e);
            }

            public void mouseDown(MouseEvent e) {
                eventMouseDown(e);
            }

            public void mouseUp(MouseEvent e) {
                listenForMouseMovement = false;
                setCursor(handOpen);
            }
        });

        this.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                if (listenForMouseMovement) {
                    followMouse(e);
                }
            }
        });

        this.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                paint(event.gc);
            }
        });

        getHorizontalBar().setEnabled(true);
        getHorizontalBar().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateHorizontalScroll((ScrollBar) event.widget);
            }
        });

        getVerticalBar().setEnabled(true);
        getVerticalBar().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateVerticalScroll((ScrollBar) event.widget);
            }
        });

        this.parent = parent;
        // updateFullsizeData();
        // setCurrentFullsizeImage(manager.getImageOrganizer().getCurrent().getFullsize());
        // workingImage =
        // manager.getImageOrganizer().getCurrent().getFullsize();
    }

    private void eventMouseDown(MouseEvent e) {
        listenForMouseMovement = true;
        mousex = e.x;
        mousey = e.y;
        setCursor(handClosed);

        if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_THUMB) {
            manager.getImageOrganizer().selectHolder(e.x, e.y);
            manager.getStatusCanvas().updateWithCurrent();
            manager.getImageEditor().setPartName(
                    manager.getImageOrganizer().getCurrent().getDisplayName());
            redraw();
        }
    }

    private void eventMouseDoubleClick(MouseEvent e) {
        if (!manager.getImageOrganizer().isSingle()) {
            if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
                manager.getImageOrganizer().getCurrent().getFullsize()
                        .dispose();
                manager.getImageEditor().toggleView();
            } else {
                if (manager.getImageOrganizer().selectHolder(e.x, e.y)) {
                    manager.getImageEditor().toggleView();
                }
            }
        }

        manager.getStatusCanvas().updateWithCurrent();

        // redraw();
    }

    public void setIconsPath(String path) {
        this.handOpen = new Cursor(getDisplay(), new ImageData(path
                + "cursor_hand_open.gif"), 10, 0);
        this.handClosed = new Cursor(getDisplay(), new ImageData(path
                + "cursor_hand_closed.gif"), 10, 0);
        setCursor(handOpen);
    }

    private void followMouse(MouseEvent e) {

        if (clientw < imgw) {
            int mouseDiffX = (mousex - e.x);
            mousex = e.x;
            imgx -= mouseDiffX;
            getHorizontalBar().setSelection(
                    getHorizontalBar().getSelection() + mouseDiffX);
            int minx = clientw - imgw;
            int maxx = 0;
            if (imgx < minx)
                imgx = minx;
            if (imgx > maxx)
                imgx = maxx;
            scrollx = getHorizontalBar().getSelection();
        }
        if (clienth < imgh) {
            int mouseDiffY = (mousey - e.y);
            mousey = e.y;
            imgy -= mouseDiffY;
            getVerticalBar().setSelection(
                    getVerticalBar().getSelection() + mouseDiffY);
            int miny = clienth - imgh;
            int maxy = 0;
            if (imgy < miny)
                imgy = miny;
            if (imgy > maxy)
                imgy = maxy;

            scrolly = getVerticalBar().getSelection();
        }

        redraw();
    }

    //
    // public void setSourceImage(File file)
    // {
    // if (workingImage != null)
    // workingImage.dispose();
    //
    // workingImage = new Image(getDisplay(), new
    // ImageData(file.getAbsolutePath()));
    // updateScrollbarPosition();
    // }

    void paint(GC gc) {
        if (backImage != null)
            backImage.dispose();

        backImage = new Image(getDisplay(), clientw, clienth);

        GC backGC = new GC(backImage);
        backGC.setBackground(COLOR_WIDGET_BACKGROUND);
        backGC.setClipping(getClientArea());
        backGC.fillRectangle(getClientArea());
        // backGC.drawImage(workingImage, imgx, imgy);

        ImageOrganizer organizer = manager.getImageOrganizer();

        if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
            backGC.drawImage(workingImage, imgx, imgy);
        } else
        // draw thumbnails
        {
            int tw = organizer.getThumbWidth();

            int cols = (clientw / organizer.getThumbWidth());
            if (cols < 1)
                cols = 1;
            int rows = organizer.getCount() / cols;
            if (organizer.getCount() > rows * cols)
                rows++;

            ArrayList holders = organizer.getHolders();
            int index = 0;
            for (int i = 0; i < rows; i++) {
                if (imgy + i * tw > clienth) // make sure that only the
                    // neccessary thumbs are
                    // initiated (and not all ==
                    // very SLOW)
                    break;
                for (int k = 0; k < cols; k++) {
                    if (index == organizer.getCount())
                        break;

                    ((ImageHolder) holders.get(index)).drawThumb(backGC,
                            k * tw, imgy + i * tw);
                    index++;
                }
            }
        }

        gc.drawImage(backImage, 0, 0);
        backGC.dispose();
    }

    private void updateScrollVisibility() {
        // only show when neccessary
        getHorizontalBar().setVisible(clientw < imgw);
        getVerticalBar().setVisible(clienth < imgh);
    }

    private void updateVerticalScroll(ScrollBar bar) {
        imgy -= bar.getSelection() - scrolly;
        scrolly = bar.getSelection();
        redraw();
    }

    private void updateHorizontalScroll(ScrollBar bar) {
        imgx -= bar.getSelection() - scrollx;
        scrollx = bar.getSelection();
        redraw();
    }

    public void zoomFit() {

        zoomScale = 1;
        double scaleWidth, scaleHeight = 0;

        scaleWidth = originalImage.getBounds().width - getClientArea().width;
        scaleHeight = originalImage.getBounds().height - getClientArea().height;

        if (scaleWidth > 0)
            scaleWidth = scaleWidth / originalImage.getBounds().width;
        if (scaleHeight > 0)
            scaleHeight = scaleHeight / originalImage.getBounds().height;

        if (scaleWidth > scaleHeight && scaleWidth > 0)
            zoomScale = 1 - scaleWidth;
        else if (scaleHeight > scaleWidth && scaleHeight > 0)
            zoomScale = 1 - scaleHeight;

        if (zoomScale < 0.001)
            zoomScale = 0.001;

        onZoom();
    }

    public void zoomIn() {
        if (zoomScale < 1)
            zoomScale *= 2;
        else {
            zoomScale += 0.5;
            if (zoomScale > 4)
                zoomScale = 4;
        }

        onZoom();
    }

    public void zoomOut() {
        if (zoomScale <= 1) {
            if (zoomScale > 0.001)
                zoomScale /= 2;
        } else
            zoomScale -= 0.5;

        onZoom();
    }

    public void zoomOriginal() {
        zoomScale = 1;
        onZoom();
    }

    private void onZoom() {
        int w = (int) (originalImage.getBounds().width * zoomScale);
        int h = (int) (originalImage.getBounds().height * zoomScale);
        if (w < 1)
            w = 1;
        if (h < 1)
            h = 1;

        ImageData imageData = originalImage.getImageData().scaledTo(w, h);
        if (workingImage != null && !workingImage.isDisposed())
            workingImage.dispose();

        workingImage = new Image(getDisplay(), imageData);
        updateScrollbarPosition();
    }

    public void updateThumbData() {
        updateScrollbarPosition();
    }

    public void updateFullsizeData() {
        disposeImages();
        zoomScale = 1;
        originalImage = manager.getImageOrganizer().getCurrent().getFullsize();
        workingImage = new Image(getDisplay(), originalImage.getImageData());

        updateScrollbarPosition();
    }

    public void rotate() {
        ImageData originalData = workingImage.getImageData();
        PaletteData originalPalette = originalData.palette;
        ImageData tmpData;
        PaletteData tmpPalette;

        if (originalPalette.isDirect)
            tmpPalette = new PaletteData(originalPalette.redMask,
                    originalPalette.greenMask, originalPalette.blueMask);
        else
            tmpPalette = new PaletteData(originalPalette.getRGBs());

        tmpData = new ImageData(originalData.height, originalData.width,
                originalData.depth, tmpPalette);

        tmpData.transparentPixel = originalData.transparentPixel;

        for (int i = 0; i < originalData.width; i++) {
            for (int k = 0; k < originalData.height; k++) {
                tmpData.setPixel(k, originalData.width - 1 - i, originalData
                        .getPixel(i, k));
            }
        }

        if (workingImage != null)
            workingImage.dispose();
        workingImage = new Image(getDisplay(), tmpData);
        updateScrollbarPosition();
    }

    private void updateScrollbarPosition() {
        clientw = getClientArea().width;
        clienth = getClientArea().height;
        if (clientw < 1)
            clientw = 1;
        if (clienth < 1)
            clienth = 1;

        if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
            imgh = workingImage.getBounds().height;
            imgw = workingImage.getBounds().width;
        } else {
            ImageOrganizer organizer = manager.getImageOrganizer();
            int cols = (clientw / organizer.getThumbWidth());
            if (cols < 1)
                cols = 1;
            int rows = organizer.getCount() / cols;
            if (organizer.getCount() > rows * cols)
                rows++;

            imgh = organizer.getThumbWidth() * rows;
            imgw = clientw / organizer.getThumbWidth();
        }

        updateScrollVisibility();
        getVerticalBar().setSelection(0);
        getHorizontalBar().setSelection(0);
        imgx = clientw / 2 - imgw / 2;
        imgy = clienth / 2 - imgh / 2;

        if (imgx < 0)
            imgx = 0;
        if (imgy < 0)
            imgy = 0;

        scrollx = getHorizontalBar().getSelection();
        scrolly = getVerticalBar().getSelection();

        ScrollBar vertical = getVerticalBar();
        vertical.setMaximum(imgh);
        vertical.setThumb(Math.min(clienth, imgh));
        vertical.setIncrement(40);
        vertical.setPageIncrement(clienth);

        ScrollBar horizontal = getHorizontalBar();
        horizontal.setMaximum(imgw);
        horizontal.setThumb(Math.min(clientw, imgw));
        horizontal.setIncrement(40);
        horizontal.setPageIncrement(clientw);

        redraw();
    }

    private void disposeImages() {
        if (originalImage != null && !originalImage.isDisposed())
            originalImage.dispose();
        if (workingImage != null && !workingImage.isDisposed())
            workingImage.dispose();
        if (backImage != null && !backImage.isDisposed())
            backImage.dispose();
    }
    
    public void dispose() {
        disposeImages();

        super.dispose();
    }
}