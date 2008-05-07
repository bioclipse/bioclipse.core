package net.bioclipse.cdk10.jchempaint.ui.editor;

import javax.swing.JMenu;

public class JCPMenu extends JMenu {

    public JCPMenu(String translation) {
        super(translation);
    }
    

    @Override
    public void menuSelectionChanged(boolean arg0) {
        this.getPopupMenu().menuSelectionChanged(arg0);
        super.menuSelectionChanged(arg0);
        this.getPopupMenu().repaint();
    }
}
