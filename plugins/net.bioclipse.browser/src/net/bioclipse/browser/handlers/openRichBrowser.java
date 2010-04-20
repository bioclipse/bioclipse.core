package net.bioclipse.browser.handlers;

import java.io.IOException;
import java.net.URL;

import net.bioclipse.browser.Activator;
import net.bioclipse.browser.editors.RichBrowserEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.part.NullEditorInput;

/**
 * A handler to open an empty Browser
 * @author ola
 */
public class openRichBrowser extends AbstractHandler{

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        System.out.println("Opening editor...");
        //Open the editor
        try {
            IEditorPart editor=PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage()
              .openEditor( new NullEditorInput(), RichBrowserEditor.EDITOR_ID );
            if (editor!=null){
                //Locate local test HTML page
                URL url = FileLocator.find( 
                                  Platform.getBundle( Activator.PLUGIN_ID), 
                                  new Path( "html/testRichBrowser.html" ),null);
                String path = FileLocator.toFileURL(url).getPath();
//                ((RichBrowserEditor)editor).setURL( "http://www.bioclipse.net");
                ((RichBrowserEditor)editor).setURL( path );
            }
        } catch ( PartInitException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}