package net.bioclipse.ui.editors;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.ui.editors.text.TextEditor;


public class TextEditorHelpContextProvider implements IContextProvider {

    private TextEditor editor;
    
    public TextEditorHelpContextProvider(TextEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public int getContextChangeMask() {
        return IContextProvider.NONE;
    }

    @Override
    public IContext getContext( Object target ) {
        return HelpSystem.getContext( "net.bioclipse.ui.xmlEditorHelp" );
    }

    @Override
    public String getSearchExpression( Object target ) {
        return "XML editor";
    }

}
