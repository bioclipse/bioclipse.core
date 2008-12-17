/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
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
