package net.sourceforge.jseditor.handlers;

import net.sourceforge.jseditor.editors.JSEditor;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunScriptHandler extends AbstractHandler implements IHandler {

    private static final Logger logger
        = Logger.getLogger(RunScriptHandler.class);

    public Object execute(ExecutionEvent event) throws ExecutionException {
		
        IEditorPart editor=HandlerUtil.getActiveEditor(event);
        if (!(editor instanceof JSEditor)) {
            logger.error("The active editor must contain a Javascript file "
                         + "for the action 'Run' to make sense.");
            return null;
        }
        JSEditor jsEditor = (JSEditor) editor;

//        if (editor.isDirty())
//            return null; // error handling, anyone?
        
        IEditorInput input = jsEditor.getEditorInput();
        logger.debug(input.toString());
        
        return null;
    }

}
