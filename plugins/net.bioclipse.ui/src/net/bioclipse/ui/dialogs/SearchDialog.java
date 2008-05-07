package net.bioclipse.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class SearchDialog extends Dialog {

    public SearchDialog(Shell parentShell) {
            super(parentShell);
    }

    public SearchDialog(IShellProvider parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FormLayout());

        final TabFolder tabFolder = new TabFolder(container, SWT.BORDER);

//            final TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
//        tabItem1.setText("Testilytest");
//
//        final Group outerGroup = new Group(tabFolder, SWT.SHADOW_NONE);
//        tabItem1.setControl(outerGroup);
//        outerGroup.setLayout(new GridLayout());
//
//        final Group queryGroup1 = new Group(outerGroup, SWT.SHADOW_NONE);
//        queryGroup1.setText("Search Query");
//
//        Label label = new Label(queryGroup1, SWT.BORDER);
//        final FormData formData2 = new FormData();
//        formData2.top = new FormAttachment(0, 10);
//        formData2.bottom = new FormAttachment(0, 20);
//        formData2.left = new FormAttachment(0, 10);
//        formData2.right = new FormAttachment(0, 50);
//        label.setLayoutData(formData2);
//        label.setText("Free text:");
//
//        Text text = new Text(queryGroup1, SWT.BORDER);
//        final FormData formData3 = new FormData();
//        formData3.top = new FormAttachment(0, 10);
//        formData3.bottom = new FormAttachment(0, 20);
//        formData3.left = new FormAttachment(0, 110);
//        formData3.right = new FormAttachment(0, 150);
//        label.setLayoutData(formData2);

        final Group outerGroup = createTabItem("Free text", tabFolder);

        final Group queryGroup1 = new Group(outerGroup, SWT.SHADOW_NONE);
        queryGroup1.setText("Search Query");
        queryGroup1.setLayout(new RowLayout(SWT.HORIZONTAL));
        new Label(queryGroup1, SWT.BORDER).setText("Free text:");
        new Text(queryGroup1, SWT.BORDER);

        createWhereToSearchGroup( outerGroup,
                new String[] {
                    "Some database",
                    "Some other database"
        } );

        final Group outerGroup2 = createTabItem("Structure", tabFolder);

        final Group queryGroupG = new Group(outerGroup2, SWT.SHADOW_NONE);
        queryGroupG.setLayout(new RowLayout(SWT.HORIZONTAL));

        final Group queryGroup2 = new Group(queryGroupG, SWT.SHADOW_NONE);
        queryGroup2.setText("Search Query");
        queryGroup2.setLayout(new RowLayout(SWT.VERTICAL));
        final Group queryGroup2a = new Group(queryGroup2, SWT.SHADOW_NONE);
        queryGroup2a.setLayout(new RowLayout(SWT.HORIZONTAL));
        new Label(queryGroup2a, SWT.BORDER).setText("SMILES:");
        new Text(queryGroup2a, SWT.BORDER);

        final Group queryGroup2c = new Group(queryGroupG, SWT.SHADOW_NONE);
        queryGroup2c.setLayout(new RowLayout(SWT.HORIZONTAL));
        new Composite(queryGroup2c, SWT.NULL);

        final Group queryGroup2b = new Group(queryGroup2, SWT.SHADOW_NONE);
        queryGroup2b.setLayout(new RowLayout(SWT.HORIZONTAL));
        new Label(queryGroup2b, SWT.BORDER).setText("ID:");
        new Text(queryGroup2b, SWT.BORDER);

        createWhereToSearchGroup( outerGroup2,
                new String[] {
                    "Pubchem",
                    "Chebi"
        } );

        final Group outerGroup3 = createTabItem("Sequence", tabFolder);

        final Group queryGroup3 = new Group(outerGroup3, SWT.SHADOW_NONE);
        queryGroup3.setText("Search Query");
        queryGroup3.setLayout(new RowLayout(SWT.HORIZONTAL));
        new Label(queryGroup3, SWT.BORDER).setText("Sequence ID:");
        new Text(queryGroup3, SWT.BORDER);

        createWhereToSearchGroup( outerGroup3,
                   new String[] {
                    "EMBL",
                    "GenBank",
                    "UniProt",
        } );

        return container;
    }

    private Group createTabItem(final String name, final TabFolder tabFolder) {
        final TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
        tabItem1.setText(name);

        final Group outerGroup = new Group(tabFolder, SWT.SHADOW_NONE);
        tabItem1.setControl(outerGroup);
        outerGroup.setLayout(new RowLayout(SWT.VERTICAL));

        return outerGroup;
    }

    private void createWhereToSearchGroup(final Group outerGroup,
                                          final String[] names) {

      final Group group = new Group(outerGroup, SWT.SHADOW_NONE);
      group.setText("Where to Search");
      group.setLayout(new RowLayout(SWT.VERTICAL));

      TableViewer table = new TableViewer( group, SWT.CHECK );

        table.setContentProvider( new IStructuredContentProvider() {

            String[] content;

            public Object[] getElements(Object inputElement) {
                return content;
            }

            public void dispose() {}

            public void inputChanged( Viewer viewer, Object oldInput,
                                      Object newInput ) {

                content = (String[])newInput;
            }
        });

        table.setInput( names );
    }
}
