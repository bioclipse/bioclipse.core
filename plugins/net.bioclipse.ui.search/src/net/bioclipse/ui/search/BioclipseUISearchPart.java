package net.bioclipse.ui.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class BioclipseUISearchPart extends ViewPart {

	private BioclipseSearch search;

	public BioclipseUISearchPart() {
		this.search = BioclipseSearch.INSTANCE;
	}
	
	@Override
	public void createPartControl(Composite arg0) {
		search.createControls(arg0);
	}

	@Override
	public void setFocus() {
		search.setFocus();
	}
}
