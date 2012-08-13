package net.bioclipse.usermanager.handlers;

import java.util.Map;

import net.bioclipse.usermanager.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.internal.commands.CommandService;
import org.eclipse.ui.internal.commands.CommandServiceFactory;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.IEvaluationService;

public class LogoutHandler  extends AbstractHandler implements IElementUpdater {

    public Object execute( ExecutionEvent event ) throws ExecutionException {

        Activator.getDefault().getUserManager().logOut();
        fireHandlerChanged( new HandlerEvent(this,true,true));

        IEvaluationService es = (IEvaluationService)PlatformUI.getWorkbench().getService( IEvaluationService.class );
        es.requestEvaluation( "net.bioclipse.usermanager.isLoggedIn" );
        
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);         
        commandService.refreshElements("net.bioclipse.usermanager.commands.login", null);

        return null;
    }

    @Override
    public void updateElement( UIElement element, Map parameters ) {
        element.setTooltip( "Hej hopp!" );
        // TODO Auto-generated method stub
        // When rebased here's where to update the tooltip... But will it work if I log-in to e.g. OpenTox when I create a new account?
    }

}
