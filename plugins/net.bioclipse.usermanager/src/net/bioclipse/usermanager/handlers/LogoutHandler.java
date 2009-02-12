package net.bioclipse.usermanager.handlers;

import net.bioclipse.usermanager.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

public class LogoutHandler  extends AbstractHandler {

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        Activator.getDefault().getUserManager().logOut();
        fireHandlerChanged( new HandlerEvent(this,true,true));

        IEvaluationService es = (IEvaluationService)PlatformUI.getWorkbench().getService( IEvaluationService.class );
        es.requestEvaluation( "net.bioclipse.usermanager.isLoggedIn" );
        
        return null;
    }
    

}
