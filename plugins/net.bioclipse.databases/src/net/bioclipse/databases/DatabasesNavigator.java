/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.databases;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;


/**
 * @author jonalv
 *
 */
public class DatabasesNavigator extends CommonNavigator 
                                implements IDatabasehangeListener {

    public DatabasesNavigator() {
        Activator.getDefault().publishDatabasesChangeEvent( this );
    }
    
    @Override
    protected IAdaptable getInitialInput() {
        return new DatabasesRoot();
    }

    public void fireRefresh() {
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                getCommonViewer().refresh();                
            }
        });
    }
}
