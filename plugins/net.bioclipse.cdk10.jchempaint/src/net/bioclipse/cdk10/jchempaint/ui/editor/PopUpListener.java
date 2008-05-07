package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

public class PopUpListener implements ActionListener {

    private JCPAction action;
    private JCPMultiPageEditorContributor contributor;

    public PopUpListener(JCPMultiPageEditorContributor contributor, JCPAction a) {
        this.contributor = contributor;
        this.action = a;
    }

    public void actionPerformed(ActionEvent e) {
        action.run(e);
        DrawingPanel drawingPanel = ((MDLMolfileEditor)contributor.getActiveEditorPart()).getDrawingPanel();
        drawingPanel.repaint();
    }


}
