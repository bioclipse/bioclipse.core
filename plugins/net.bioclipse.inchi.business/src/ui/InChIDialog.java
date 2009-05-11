package ui;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class InChIDialog extends MessageDialog {

    Map<String,String> inchiMap;
    final Clipboard cb;

    public InChIDialog(Shell parentShell, String dialogTitle,
            Image dialogTitleImage,Map<String,String> inchiMap, String dialogMessage, int dialogImageType,
            String[] dialogButtonLabels, int defaultIndex) {
        super( parentShell, dialogTitle, dialogTitleImage, dialogMessage,
               dialogImageType, dialogButtonLabels, defaultIndex );
        cb= new Clipboard(parentShell.getDisplay());
        this.inchiMap = inchiMap;
    }
    
    @Override
    protected Control createCustomArea( Composite parent ) {

        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        
            parent.setLayout( gridLayout );
            Text text;
            Label label;
            for(String s:inchiMap.keySet()) {
                label = new Label(parent,SWT.NONE);
                label.setText( s );
                //label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
                text = new Text(parent,SWT.READ_ONLY);
                
                text.setBackground( parent.getBackground() );
                text.setText( inchiMap.get( s ));
                text.addKeyListener( new KeyAdapter() {
                   @Override
                    public void keyReleased( KeyEvent e ) {
                    
                       if(e.character!='W' && e.character!='w')
                           return;

                       int onmask;
                       String vers = System.getProperty( "os.name" ).toLowerCase();
                       if( vers.indexOf( "mac" ) != -1)
                           onmask = SWT.COMMAND;
                       else
                           onmask = SWT.CTRL;
                       
                       if ( (e.keyCode == onmask) ) {
                           
                           String textData= ((Text)e.getSource()).getText();
                           TextTransfer textTransfer = TextTransfer.getInstance();
                           cb.setContents(new Object[] { textData },
                                          new Transfer[] { textTransfer });
                       }
                     }
                });
            }

           return parent;
    }


    public static void openInformation(Shell parent, String title,
                                       Map<String,String> inchiMap,String message) {
        MessageDialog dialog = new InChIDialog(parent, title, null, // accept
                                                 // the
                                                 // default
                                                 // window
                                                 // icon
                                                    inchiMap,message, INFORMATION,
                                                 new String[] { IDialogConstants.OK_LABEL }, 0);
        // ok is the default
        dialog.open();
        return;
    }
}
