package net.bioclipse.cdk10.jchempaint.ui.editor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class JCPCompFocusListener implements FocusListener {

	private JCPComposite comp;

	public JCPCompFocusListener(JCPComposite jcpComposite) {
		this.comp = jcpComposite;
	}

	public void focusGained(FocusEvent e) {
		comp.setHasFocus(true);

	}

	public void focusLost(FocusEvent e) {
		comp.setHasFocus(false);
	}

}
