package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;

import org.eclipse.swt.widgets.Display;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.PopupController2D;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * Wrapping around PopupController2D for grabing mouse events which should 
 * just activate the editor and not do any painting
 * 
 * @author tohel
 *
 */
public class BCJCPPopupController extends PopupController2D {

	private JCPComposite comp;
	protected boolean focus;
	private boolean focussing;

	public BCJCPPopupController(IChemModel chemModel, Renderer2DModel r2dm, Controller2DModel c2dm, Vector lastAction, JButton moveButton, JCPComposite comp, HashMap funcgroups) {
		super(chemModel, r2dm, c2dm, lastAction, moveButton, funcgroups);
		this.comp = comp;
	}

	public BCJCPPopupController(IChemModel chemModel, Renderer2DModel r2dm, Controller2DModel c2dm) {
		super(chemModel, r2dm, c2dm);
	}

	public BCJCPPopupController(IChemModel chemModel, Renderer2DModel r2dm) {
		super(chemModel, r2dm);
	}
	
	public void mousePressed(final MouseEvent event)
	{
		if (this.getCompFocus()) {
			this.focussing = false;
			BCJCPPopupController.super.mousePressed(event);
		}
		else {
			this.focussing = true;
			this.setCompFocus();
		}
		
	}
	private boolean getCompFocus() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				BCJCPPopupController.this.focus = comp.getFocus();
			}
		});
		return this.focus;
	}
	
	private void setCompFocus() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				comp.setFocus();
			}
		});
	}

	@Override
	public void mouseReleased(final MouseEvent event) {	
		if (this.getCompFocus() && !this.focussing) {
			super.mouseReleased(event);
		}
		
	}
}
