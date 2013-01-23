package net.bioclipse.ui.advanced;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.ui.advanced.preferences.PreferenceConstants;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.osgi.service.prefs.Preferences;


public class AdvancedEnabledSourceProvider extends AbstractSourceProvider {

    public final static String  ADVANCED_STATE = "net.bioclipse.ui.advances.AdvancedState";

    private final static String ENABLED        = "Enabled";
    private final static String DISABLED       = "Disabled";
    public AdvancedEnabledSourceProvider() {

        IEclipsePreferences pref = InstanceScope.INSTANCE
                        .getNode( "net.bioclipse.ui.advanced" );;
        pref.addPreferenceChangeListener( new IPreferenceChangeListener() {

            @Override
            public void preferenceChange( PreferenceChangeEvent event ) {

                fireSourceChanged( ISources.WORKBENCH, ADVANCED_STATE,
                                   currentState( event.getNewValue() ) );
            }
        } );
    }

    private String currentState( Object state ) {

        if ( state instanceof String )
            return ((String) state).equals( "true" ) ? ENABLED : DISABLED;
        else
            return DISABLED;
    }
    @Override
    public void dispose() {

        // TODO Auto-generated method stub

    }

    @Override
    public Map getCurrentState() {
        Map<String,String> currentState = new HashMap<String,String>(1);
        Preferences preference = InstanceScope.INSTANCE
                        .getNode( "net.bioclipse.ui.advanced" );
        String state = preference.getBoolean( PreferenceConstants.P_BOOLEAN,
                                              false ) ? ENABLED : DISABLED;
        currentState.put( ADVANCED_STATE, state );
        return currentState;
    }

    @Override
    public String[] getProvidedSourceNames() {

        return new String[] { ADVANCED_STATE };
    }

}
