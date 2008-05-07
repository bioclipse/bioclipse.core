package net.bioclipse.actions;

import net.bioclipse.ui.dialogs.SearchDialog;
import net.bioclipse.ui.Activator;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class SearchAction  implements IWorkbenchWindowActionDelegate {

    public void dispose() {
    }
    public void init(IWorkbenchWindow window) {
    }

    public void run(IAction action) {

        Logger logger = Activator.getLogger();
        logger.debug("SearchAction.run()");

        SearchDialog loginDialog = 
            new SearchDialog( PlatformUI
                              .getWorkbench()
                              .getActiveWorkbenchWindow()
                              .getShell() );
        
        loginDialog.open();
        if(loginDialog.getReturnCode() == Window.OK) {
            logger.debug("SearchAction succeeded");
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
