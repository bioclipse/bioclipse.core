package net.bioclipse.jseditor.editors;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.ui.editors.text.TextEditor;


public class JsEditorContextProvider implements IContextProvider {

    private TextEditor editor;
    
    public JsEditorContextProvider(TextEditor editor) {
        this.editor = editor;
    }
    @Override
    public int getContextChangeMask() {
        return IContextProvider.NONE;
    }

    @Override
    public IContext getContext( Object target ) {
        return HelpSystem.getContext( "net.bioclipse.ui.jsEditor" );
    }

    @Override
    public String getSearchExpression( Object target ) {
        return target.toString();
    }

}
