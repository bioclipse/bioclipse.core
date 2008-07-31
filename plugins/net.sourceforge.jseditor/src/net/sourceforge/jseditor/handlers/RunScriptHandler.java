package net.sourceforge.jseditor.handlers;

import java.util.Map;

import net.sourceforge.jseditor.editors.JSEditor;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunScriptHandler extends AbstractHandler implements IHandler {

    private static final Logger logger = Logger.getLogger(RunScriptHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IEditorPart editor=HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof JSEditor)) {
			logger.error("A JS command was run but jseditor is not active editor");
			return null;
		}
		JSEditor jsEditor = (JSEditor) editor;

		//FIXME: continue here masak
		logger.error("NOT IMPLEMENTED YET!");

		return null;
	}

	
}
