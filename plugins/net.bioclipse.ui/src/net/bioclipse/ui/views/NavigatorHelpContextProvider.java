package net.bioclipse.ui.views;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;


public class NavigatorHelpContextProvider implements IContextProvider {

    private IWorkbenchAdapter BCNavgator;
    
    public NavigatorHelpContextProvider(NavigatorRoot navigator) {
        BCNavgator = (IWorkbenchAdapter) navigator.getAdapter( IWorkbenchAdapter.class );
    }
    
    @Override
    public int getContextChangeMask() {
        return IContextProvider.NONE;
    }

    @Override
    public IContext getContext( Object target ) {
        /* If I know how to get what's selected in the BC navigator I could 
         * make different help for different kind of object...*/
        Object parent = BCNavgator.getParent( target );
        Object[] children = BCNavgator.getChildren( target );
        System.out.println("Taget: "+target+"\nParent"+parent+"\nChildren:"+children);
        return HelpSystem.getContext( "net.bioclipse.ui.xmlEditorHelp" );
    }

    @Override
    public String getSearchExpression( Object target ) {
        return null;
    }

}
