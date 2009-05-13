/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.scripting.ui.actions;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class ScriptAction extends Action implements ICheatSheetAction {

    private static final Logger logger = Logger.getLogger(ScriptAction.class);

    public void run( String[] params, ICheatSheetManager manager ) {

        if (params.length<=0){
            logger.error("No script parameter provided to ScriptAction.");
            notifyResult( false );
            return;
        }

        //Execute parameters in JSConsole
        for (String cmd : params){
            if (cmd!=null){
                try {
                    ScriptingConsoleView jsview = (ScriptingConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( "net.bioclipse.scripting.ui.views.JsConsoleView" );
                    jsview.carryOutCommandAndWait( cmd );
                } catch ( PartInitException e ) {
                    LogUtils.handleException( e, logger );
                    notifyResult( false );
                    return;
                }
            }
        }
        notifyResult( true );
        
    }
    
}
