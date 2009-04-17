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
                    jsview.simulateInputWithReturn( cmd );
                    notifyResult( true );
                } catch ( PartInitException e ) {
                    LogUtils.handleException( e, logger );
                    notifyResult( false );
                }
            }
        }
        
    }
    
}
