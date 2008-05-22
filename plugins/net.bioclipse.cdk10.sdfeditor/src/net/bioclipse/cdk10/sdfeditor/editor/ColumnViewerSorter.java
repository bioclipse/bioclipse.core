package net.bioclipse.cdk10.sdfeditor.editor;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


abstract class ColumnViewerSorter extends ViewerComparator {
    public static final int ASC = 1;

    public static final int NONE = 0;

    public static final int DESC = -1;

    private int direction = 0;

    private TableViewerColumn column;

    private ColumnViewer viewer;
    
    private int colIndex;

    public ColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column) {
        this.column = column;
        this.viewer = viewer;
        this.column.getColumn().addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if( ColumnViewerSorter.this.viewer.getComparator() != null ) {
                    if( ColumnViewerSorter.this.viewer.getComparator() == ColumnViewerSorter.this ) {
                        int tdirection = ColumnViewerSorter.this.direction;

                        if( tdirection == ASC ) {
                            setSorter(ColumnViewerSorter.this, DESC);
                        } else if( tdirection == DESC ) {
                            setSorter(ColumnViewerSorter.this, NONE);
                        }
                    } else {
                        setSorter(ColumnViewerSorter.this, ASC);
                    }
                } else {
                    setSorter(ColumnViewerSorter.this, ASC);
                }
            }
        });
    }

    //Constructor for storing colindices
    public ColumnViewerSorter(TableViewer viewer2, TableViewerColumn col2,
            int colIndex2) {
        this(viewer2,col2);
        this.colIndex=colIndex2;
    }

    public void setSorter(ColumnViewerSorter sorter, int direction) {
        if( direction == NONE ) {
            column.getColumn().getParent().setSortColumn(null);
            column.getColumn().getParent().setSortDirection(SWT.NONE);
            viewer.setComparator(null);
        } else {
            column.getColumn().getParent().setSortColumn(column.getColumn());
            sorter.direction = direction;

            if( direction == ASC ) {
                column.getColumn().getParent().setSortDirection(SWT.DOWN);
            } else {
                column.getColumn().getParent().setSortDirection(SWT.UP);
            }

            if( viewer.getComparator() == sorter ) {
                viewer.refresh();
            } else {
                viewer.setComparator(sorter);
            }

        }
    }

    public int compare(Viewer viewer, Object e1, Object e2) {
        return direction * doCompare(viewer, e1, e2);
    }

    protected abstract int doCompare(Viewer viewer, Object e1, Object e2);

    
    public int getColIndex() {
    
        return colIndex;
    }

    
    public void setColIndex( int colIndex ) {
    
        this.colIndex = colIndex;
    }

    
}

