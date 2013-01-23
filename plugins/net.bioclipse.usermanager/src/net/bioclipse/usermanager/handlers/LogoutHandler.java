package net.bioclipse.usermanager.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.business.IUserManager;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.IEvaluationService;
import org.osgi.service.prefs.BackingStoreException;

public class LogoutHandler  extends AbstractHandler implements IElementUpdater {

    private Logger logger = Logger.getLogger( this.getClass() );
    
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        
        IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
        boolean promptOnLogout = prefStore.getBoolean( Activator.PROMPT_ON_LOGOUT );
        int buttonPressed;
        
        if(promptOnLogout) {
            MessageDialogWithToggle dlg = MessageDialogWithToggle
                    .openOkCancelConfirm(PlatformUI.getWorkbench().getDisplay()
                                         .getActiveShell(),
                                         "Confirm Logout", 
                                         "Do you want to logout?",
                                         "Always logout without prompt",
                                         false, null, null);
            buttonPressed = dlg.getReturnCode();
            
            if (buttonPressed == MessageDialogWithToggle.OK) {
                prefStore.setValue( Activator.PROMPT_ON_LOGOUT, !dlg.getToggleState() );
                try {
                    InstanceScope.INSTANCE.getNode( Activator.PLUGIN_ID ).flush();
                } catch ( BackingStoreException e ) {
                    logger.error( e.getStackTrace() );
                    e.printStackTrace();
                }
            }
        } else
            buttonPressed = MessageDialogWithToggle.OK;
        
        /* If you click on the icon you will logout. Only exception to that is 
         * if you chosen cancel in the dialog, i.e. no dialog => logout */
        switch ( buttonPressed ) {
            case MessageDialogWithToggle.OK:          
                Activator.getDefault().getUserManager().logOut();
                fireHandlerChanged( new HandlerEvent(this,true,true));

                IEvaluationService es = (IEvaluationService)PlatformUI.getWorkbench().getService( IEvaluationService.class );
                es.requestEvaluation( "net.bioclipse.usermanager.isLoggedIn" );

                ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);         
                commandService.refreshElements("net.bioclipse.usermanager.commands.login", null);
                break;
                
            default:
                break;
        }
        
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
