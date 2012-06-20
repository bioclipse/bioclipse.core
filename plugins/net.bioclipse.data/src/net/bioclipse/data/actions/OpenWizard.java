package net.bioclipse.data.actions;

import net.bioclipse.data.wizards.NewDataProjectWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A class to open the Install Sample Data wizard from menu
 * @author ola
 *
 */
public class OpenWizard extends AbstractHandler{

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        
        NewDataProjectWizard wizard = new NewDataProjectWizard();
        
        wizard.init(HandlerUtil.getActiveWorkbenchWindow( event ).getWorkbench()
                    , null);
        
        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell( 
                                                               event ), wizard);
        dialog.create();
        dialog.open();

        return null;
    }
}
