/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.widgets;

import java.io.File;
import java.text.DecimalFormat;

import nu.psnet.quickimage.core.ImageHolder;
import nu.psnet.quickimage.core.ImageOrganizer;
import nu.psnet.quickimage.core.QManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Per Salomonsson
 * 
 */
public class QStatusCanvas extends Canvas {
    private File file;
    private String filesize = "";
    private int height = 0;
    private int width = 0;
    private String filename = "";
    private int depth = 0;
    private Image image;
    private DecimalFormat df = new DecimalFormat("0.000");
    private Color COLOR_DARK_GRAY;
    private QManager manager;

    public QStatusCanvas(QManager manager, final Composite parent, int style) {
        // super(parent, style | SWT.BORDER);
        super(parent, style | SWT.FLAT);
        this.manager = manager;

        this.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                paint(event.gc);
            }
        });
        COLOR_DARK_GRAY = parent.getDisplay().getSystemColor(
                SWT.COLOR_DARK_GRAY);
        // separator = new Color(getDisplay(), 140,140,140);
        // setBackground(new Color(parent.getDisplay(), 255,0,0));
    }

    public void updateWithCurrent() {
        if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
            image = manager.getImageOrganizer().getCurrent().getFullsize();
            if (image != null) {
                depth = image.getImageData().depth;
                width = image.getBounds().width;
                height = image.getBounds().height;
            }
        }

        ImageHolder holder = manager.getImageOrganizer().getCurrent();
        filename = holder.getDisplayName();
        filesize = df.format(holder.getImageSize());
        if(holder.getImageSize() == 0)
            filesize = "unknown";
        redraw();
    }

    void paint(GC gc) {
        if (manager.getImageOrganizer().getActiveView() == ImageOrganizer.VIEW_FULLSIZE) {
            gc.drawString("Size (kb): " + filesize, 5, 1);
            gc.drawString("Depth: " + depth, 140, 1);
            gc.drawString(width + " x " + height, 225, 1);
            gc.drawString("Name: " + filename, 325, 1);
            gc.setForeground(COLOR_DARK_GRAY);
            gc.drawLine(135, 0, 135, 24);
            gc.drawLine(210, 0, 210, 24);
            gc.drawLine(320, 0, 320, 24);
        } else {
            gc.drawString("Size (kb): " + filesize, 5, 1);
            gc.drawString("Name: " + filename, 140, 1);
            gc.setForeground(COLOR_DARK_GRAY);
            gc.drawLine(135, 0, 135, 24);
        }
    }
}
