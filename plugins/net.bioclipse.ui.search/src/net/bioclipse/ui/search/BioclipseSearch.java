package net.bioclipse.ui.search;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

public class BioclipseSearch {
	private Table table;

	public BioclipseSearch() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnSearch = new TableColumn(table, SWT.NONE);
		tblclmnSearch.setWidth(100);
		tblclmnSearch.setText("Search");
		
		TableColumn tblclmnhits = new TableColumn(table, SWT.NONE);
		tblclmnhits.setWidth(100);
		tblclmnhits.setText("#hits");
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}

}
