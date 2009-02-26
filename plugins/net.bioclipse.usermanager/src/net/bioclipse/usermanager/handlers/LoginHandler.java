package net.bioclipse.usermanager.handlers;

import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.UserContainer;
import net.bioclipse.usermanager.dialogs.UserManagerLoginDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;


public class LoginHandler extends AbstractHandler {

    
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        
//        String param=event.getParameter( "net.bioclipse.usermanager.loginParameter" );
        
//        System.out.println("Param: " + param + " detected");
        
        UserContainer sandboxUserContainer = Activator
        .getDefault()
        .getUserManager()
        .getSandBoxUserContainer();

        UserManagerLoginDialog loginDialog = 
            new UserManagerLoginDialog( PlatformUI
                                        .getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getShell(),
                                        sandboxUserContainer );

        loginDialog.open();
        if(loginDialog.getReturnCode() == Window.OK) {
            if( loginDialog.isUserContainerEdited() ) {
                Activator.getDefault().getUserManager()
                .switchUserContainer(sandboxUserContainer);
            }
        }
        
        fireHandlerChanged( new HandlerEvent(this,true,true));

        IEvaluationService es = (IEvaluationService)PlatformUI.getWorkbench().getService( IEvaluationService.class );
        es.requestEvaluation( "net.bioclipse.usermanager.isLoggedIn" );
        
        
        return null;
    }

}
