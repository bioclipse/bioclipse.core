package net.bioclipse.usermanager.dialogs;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;


public class EditAccountDialog extends Dialog {
    
    private HashMap<String, String> properties;
    
    protected EditAccountDialog(Shell shell, 
                                HashMap<String, String> properties) {

        super( shell );
        this.properties = properties;
    }

    protected HashMap<String, String> getProperties() {
        return properties;
    }
}
