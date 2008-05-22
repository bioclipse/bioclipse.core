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

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;

public class MoleculeListLabelProviderNew extends OwnerDrawLabelProvider{


    @Override
    protected void measure(Event event, Object element) {

        int xsize = event.width;
        int ysize = event.height;

        //Get width from widget column
        if (event.widget instanceof Table) {
            Table table = (Table) event.widget;
            
            //Column 1 is always structure, 0 is index
            xsize=table.getColumn(StructureTablePage.STRUCTURE_COLUMN).getWidth();
        }
        
        //Minimum sizes
        if (ysize<100) ysize=100;
        if (xsize<100) xsize=100;

        event.setBounds(new Rectangle(event.x, event.y, xsize,
                ysize));
    }


    @Override
    protected void paint(Event event, Object element) {
        if (element instanceof StructureTableEntry) {
            StructureTableEntry entry = (StructureTableEntry) element;
            entry.draw(event);
        }
        
    }
    
}
