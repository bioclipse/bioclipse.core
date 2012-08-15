package net.bioclipse.usermanager.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.business.IUserManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
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
        IUserManager userManager = Activator.getDefault().getUserManager();
        String toolTipText = "";
        if (userManager.isLoggedIn()) {
            toolTipText = "You are logged in as: " + userManager.getLoggedInUserName();
            HashMap<String, Boolean> accountInfo = userManager.getLoggedInUser().getLoggedInAccounts();
            if ( accountInfo != null && !accountInfo.isEmpty() ) {
                String key = "";
                Set<String> keys = accountInfo.keySet();
                Iterator<String> itr = keys.iterator();
                while ( itr.hasNext() ) {
                    key = itr.next();
                    toolTipText += "\n  " + key.substring( key.lastIndexOf( '.' ) + 1 ) +" login ";
                    if ( accountInfo.get( key ) )
                        toolTipText += "OK";
                    else
                        toolTipText += "failed";
                }
            }
        }
        else
            /*It should not end up here, but just in case...*/
            toolTipText = "You're hopefully logged in...";
        
        element.setTooltip( toolTipText );
    }

}
