package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.swt.widgets.Composite;

public class JCPComposite extends Composite {

	private boolean hasFocus = false;

	public JCPComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void setHasFocus(boolean b) {
		this.hasFocus = b;
		
	}
	public boolean getFocus() {
		return hasFocus;
	}

	@Override
	public boolean setFocus() {
		this.setHasFocus(true);
		return super.setFocus();
	}

}
