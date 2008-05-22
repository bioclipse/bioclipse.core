package net.bioclipse.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {

    protected Map<RGB, Color> colorTable = new HashMap<RGB, Color>(10);

    public void dispose() {
        for (Color c : colorTable.values())
             c.dispose();
    }
    
    public Color getColor(RGB rgb) {
        if ( !colorTable.containsKey(rgb) )
            colorTable.put(rgb, new Color(Display.getCurrent(), rgb));

        return colorTable.get(rgb);
    }
}
