package net.sourceforge.jseditor.handlers;

import net.bioclipse.scripting.ui.Activator;
import net.sourceforge.jseditor.editors.JSEditor;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunScriptHandler extends AbstractHandler implements IHandler {

    private static final Logger logger
        = Logger.getLogger(RunScriptHandler.class);

    public Object execute(ExecutionEvent event) throws ExecutionException {
		
        IEditorPart editor=HandlerUtil.getActiveEditor(event);
        if (!(editor instanceof JSEditor)) {
            logger.error("The active editor must contain a JavaScript file "
                         + "for the action 'Run' to make sense.");
            return null;
        }
        JSEditor jsEditor = (JSEditor) editor;

        if ( editor.isDirty() ) {
            if ( !askToSaveAndRun(editor) )
                return null;
            editor.doSave( null );
        }

        IEditorInput input = jsEditor.getEditorInput();
        
        Activator.getDefault()
                 .getJavaJsConsoleManager()
                 .executeFile( (IFile)input.getAdapter( IFile.class ) );
        
        return null;
    }

    private boolean askToSaveAndRun( IEditorPart editor ) {

        return MessageDialog.openConfirm(
                  getShell(),
                  "Save Resource",
                  String.format("'%s' has been modified. Save changes?",
                                editor.getTitle())
               );
    }

    private Shell getShell() {

        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
}
