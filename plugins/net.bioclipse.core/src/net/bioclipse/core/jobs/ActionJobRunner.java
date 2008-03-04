package net.bioclipse.core.jobs;

import java.lang.reflect.InvocationTargetException;

import net.bioclipse.core.Activator;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.UIJob;


public class ActionJobRunner {

    private static IProgressService service = PlatformUI.getWorkbench()
    .getProgressService();

    private static Shell shell = null;

    private static ActionJobRunner instance = null;

    private ActionJobRunner() {
        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    public static ActionJobRunner getInstance() {
        if (instance == null) {
            instance = new ActionJobRunner();
        }
        return instance;
    }


    public void runSingleAction(final IActionJob actionJob,
            boolean showDialog,
            final boolean isUIJob,
            final ImageDescriptor image) {

        if (Display.getCurrent() == null)
            showDialog = false;

        final Job runJob;
        if (isUIJob) {
            runSingleActionInUI(actionJob, showDialog, image);
        } else {
            runJob = new ActionJobWrapper(actionJob);
            if (showDialog) {
                service.showInDialog(shell, runJob);
            }
            if (image != null) {
                runJob.setProperty(IProgressConstants.ICON_PROPERTY, image);
            }
            runJob.setRule(actionJob.getRule());
            runJob.schedule(actionJob.getDelay());
        }
    }

    public void runRunnableInUI(final IRunnableWithProgress action, final boolean cancelable) {
        runRunnableInUI(shell,action,cancelable);
    }
    
    public void runRunnableInUI(Shell parentShell, final IRunnableWithProgress action, final boolean cancelable) {
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(parentShell);
            dialog.run(true,cancelable,action);
        } catch (InvocationTargetException e) {
            ErrorHandler.showErrorDialog(null, e.getMessage(), ErrorHandler
                    .solveException(e.getCause(), Activator.PLUGIN_ID));
        } catch (InterruptedException e) {
            ErrorHandler.showErrorDialog(null, e.getMessage(), ErrorHandler
                    .solveException(e.getCause(), Activator.PLUGIN_ID));
        }

    }
    

    public void runSingleActionInUI(final IActionJob actionJob,
            boolean showDialog,
            final ImageDescriptor image) {

        if (Display.getCurrent() == null)
            showDialog = false;

        final UIJob runJob;

        runJob = new ActionUIJobWrapper(actionJob);

        if (showDialog) {
            service.showInDialog(shell, runJob);
        }

        if (image != null) {
            runJob.setProperty(IProgressConstants.ICON_PROPERTY, image);
        }
        runJob.setRule(actionJob.getRule());
        runJob.schedule();

    }

}
