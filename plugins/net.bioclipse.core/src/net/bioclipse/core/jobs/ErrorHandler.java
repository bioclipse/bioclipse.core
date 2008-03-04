package net.bioclipse.core.jobs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author ola
 *
 */
public class ErrorHandler {

    public static void showErrorDialog(final Shell currentShell, final String message, final IStatus status) {
        if (currentShell == null) {
            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                public void run() {
                    ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),"Error",message,status, IStatus.ERROR);
                }
            });
        }
        else {
            ErrorDialog.openError(currentShell,"Error",message,status);
        }
    }

    public static IStatus solveException(Throwable e, String pluginId) {
        IStatus returnValue = null;
        if (e instanceof CoreException) {
            returnValue = ((CoreException) e).getStatus();

        } else if(e instanceof InvocationTargetException) {
            Status status = new Status(IStatus.ERROR,pluginId,500,e.getMessage(),e.getCause());
            returnValue = solveException(new CoreException(status),pluginId);

        }
        else {
            if (e.getMessage() != null) {
            	//Null as throwable instead of e to remove duplicate error posting.
                returnValue = new Status(IStatus.ERROR,pluginId,500,e.getMessage(),null);
            }
            else {
                returnValue = new Status(IStatus.ERROR,pluginId,500,"UNKNOWN ERROR",e);
            }
        }
        return returnValue;

    }

}
