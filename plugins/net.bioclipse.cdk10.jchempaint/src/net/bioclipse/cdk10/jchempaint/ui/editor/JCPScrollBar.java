package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Dimension;

import javax.vecmath.Point2d;

import net.bioclipse.cdk10.jchempaint.ui.editor.IJCPEditorPart;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ScrollBar;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * Scroll Bar for jcp_SWT
 * 
 * @author Miguel Rojas
 *
 */
public class JCPScrollBar
{
    private IJCPEditorPart jcpEditor;
    int positionScrollV = 0, positionScrollH = 0;
    public ScrollBar horizontal;
    public ScrollBar vertical;
    
    /**
     * Constructor of ScrollBarJCP
     * 
     * @param jcpComposite      Composite
     * @param horizontalScroll  True, if create horizontal scroll
     * @param verticalScroll    True, if create vertical scroll
     */
    public JCPScrollBar(IJCPEditorPart editor, boolean horizontalScroll, boolean verticalScroll){
        this.jcpEditor = editor;
        if(horizontalScroll)
            createSH();
        if(verticalScroll)
            createSV();
    }
    
    /**
     * create the horizontal scroll
     *
     */
    private void createSH(){
        horizontal = jcpEditor.getJcpComposite().getHorizontalBar();
        horizontal.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent event){
                scrollHorizontally((ScrollBar) event.widget);
            }

            private void scrollHorizontally(ScrollBar scrollBar)
            {
                int position = scrollBar.getSelection();
                moveCoordinaten(positionScrollH - position, "hScroll");
                positionScrollH = position;
                
            }
        });
    }
    
    /**
     * create the vertical scroll
     *
     */
    private void createSV(){
        vertical = jcpEditor.getJcpComposite().getVerticalBar();
        vertical.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent event){
                scrollVertically((ScrollBar) event.widget);
            }

            private void scrollVertically(ScrollBar scrollBar)
            {
                int position = scrollBar.getSelection();
                moveCoordinaten(positionScrollV - position, "vScroll");
                positionScrollV = position;
                
            }
        });
    }
    
    /**
     * move the coordinaten
     * 
     * @param value   Value of the new position
     * @param type    Scroll to move: Horizontal or Vertical
     */
    private void moveCoordinaten(int value, String type){
        IChemModel editorModel = jcpEditor.getJcpModel().getChemModel();

        Renderer2DModel r2dm = jcpEditor.getJcpModel().getRendererModel();
        Dimension dimension = r2dm.getBackgroundDimension();
        int dh = dimension.height;
        int dv = dimension.width;
        if(type.equals("vScroll")){
            dh = dh+value;
            r2dm.setBackgroundDimension(new Dimension(dv,dh));
        }
        else{
            IMoleculeSet setOfMolecules = editorModel.getMoleculeSet();
            for( int i = 0; i < setOfMolecules.getMoleculeCount() ; i++){
                IMolecule molecule = setOfMolecules.getMolecule(i);
                for( int j = 0 ; j < molecule.getAtomCount() ; j++){
                    IAtom atom = molecule.getAtom(j);
                    ((Point2d)r2dm.getRenderingCoordinate(atom)).x+=value;
                }
            }
        }
        jcpEditor.getDrawingPanel().repaint();
    }
    
    
}
