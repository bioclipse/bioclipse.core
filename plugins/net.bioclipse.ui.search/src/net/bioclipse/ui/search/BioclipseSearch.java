package net.bioclipse.ui.search;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class BioclipseSearch {
	private Table table;
	
	public static final BioclipseSearch INSTANCE = new BioclipseSearch();

	private BioclipseSearch() {
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

	public void addSearch(String text, int size) {
		TableItem tableItem = new TableItem(table, SWT.MULTI);
		tableItem.setText(0, text);
		tableItem.setText(1, size+"");
	}

}
