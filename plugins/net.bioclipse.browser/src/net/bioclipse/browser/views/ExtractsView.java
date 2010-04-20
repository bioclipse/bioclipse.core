package net.bioclipse.browser.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import net.bioclipse.browser.Activator;
import net.bioclipse.browser.ScrapingModel;
import net.bioclipse.browser.ScrapingPage;
import net.bioclipse.browser.business.IScrapingModelChangedListener;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.dnd.MoleculeTransfer;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ui.business.IUIManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author ola
 *
 */
public class ExtractsView extends ViewPart 
    implements IScrapingModelChangedListener{

    private static final Logger logger = Logger.getLogger(ExtractsView.class);

  public static final String VIEW_ID="net.bioclipse.browser.views.ExtractsView";

    private ExtractsView instance;

    private TreeViewer viewer;

    protected ISelection storedSelection;
    private Action refreshAction;

    private Action openAction;


    public ExtractsView() {
    }

    public ExtractsView getInstance() {
        return instance;
    }

    @Override
    public void createPartControl( Composite parent ) {
        
        this.instance=this;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | 
                                SWT.V_SCROLL);
//        ColumnViewerToolTipSupport.enableFor(viewer);

        //Display a list of BioObjects
        viewer.setContentProvider(new ScrapingContentProvider());
        viewer.setLabelProvider(new ScrapingLabelProvider());

        //Sort Alphabetically
//        viewer.setSorter(new ViewerSorter());
        viewer.addFilter( new ScrapingFilter() );

        GridData gridData = new GridData(GridData.FILL, GridData.FILL,
                                         true, true);
        viewer.getTree().setLayoutData(gridData);
        
        viewer.setInput( Activator.getDefault().getScrapingModel() );

        //We need to chache this for dnd
        viewer.addSelectionChangedListener( new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                storedSelection=event.getSelection();
            }
        });
        
        net.bioclipse.browser.Activator.getDefault().getScrapingModel()
        .addChangedListener(this);
        
        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
                                                          VIEW_ID);
        makeActions();
        hookContextMenu();
        contributeToActionBars();
        addDoubleClickAction();
        
        //Add drag support
        initilizeDrag();
        
        getSite().setSelectionProvider(viewer);

    }
    

    private void makeActions() {
        refreshAction = new Action() {
            public void run() {
                viewer.refresh();
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refreshes viewer");
        refreshAction.setImageDescriptor(Activator.getImageDescriptor(  "icons/refresh2.png" ));
//        refreshAction.setDisabledImageDescriptor( Activator.getImageDescriptor( "icons/smallRun_dis.gif" ));

        openAction = new Action() {
            public void run() {
                Object obj = ((IStructuredSelection)viewer
                        .getSelection()).getFirstElement();
                if ( obj instanceof IBioObject ) {
                    handleOpen((IBioObject)obj);
                }
            }

        };
        openAction.setText("Open");
        openAction.setToolTipText("Open resource in an editor");
        openAction.setImageDescriptor(Activator.getImageDescriptor(  "icons/edit-16.png" ));

    }
    
    private void addDoubleClickAction() {
        viewer.addDoubleClickListener( new IDoubleClickListener() {
            
            public void doubleClick( DoubleClickEvent event ) {
                IStructuredSelection ssel = (IStructuredSelection)event
                    .getSelection();
                Object obj = ssel.getFirstElement();
                if ( obj instanceof IBioObject ) {
                    handleOpen((IBioObject)obj);
                }
            }
        });
        
    }

    private void handleOpen(IBioObject bioobject) {
        
        IUIManager ui = net.bioclipse.ui.business.Activator.getDefault()
        .getUIManager();
        try {
            ui.open( bioobject );
        } catch ( Exception e ) {
            LogUtils.handleException( e, logger, Activator.PLUGIN_ID);
        }
        
    }

    
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu", "net.bioclipse.browser.extractsview.context");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
//                updateActionStates();
                fillContextMenu(manager);
            }

        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(openAction);
        manager.add(refreshAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(openAction);
        manager.add(refreshAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    

    @Override
    public void setFocus() {
    }
    
    public void modelChanging( String url ) {
        //TODO: work on this. Might be a new page..
//        System.out.println("Scraping model is changing page: " + url);
        
        //Add a new page with an animated icon
        //Refresh viewer in UI thread
        Display.getDefault().asyncExec( new Runnable() {

            public void run() {
                viewer.refresh();
            }
        });
    }

    public void pagesChanged( String url ) {
//        System.out.println("Extracted page changed: " + url);
        
        ScrapingModel scrapeModel = net.bioclipse.browser.Activator
        .getDefault().getScrapingModel();
        
        for (final ScrapingPage page : scrapeModel.getScrapingPages()){
            if (page.getUrl().equals( url )){

                //Refresh viewer in UI thread
                Display.getDefault().asyncExec( new Runnable() {

                    public void run() {
                        viewer.refresh(page);
                        viewer.expandToLevel( page, 2 );
                    }
                });
            }
        }        
    }

    /**
     * The underlying model has changed and needs to be refreshed
     * @param url
     */
    public void modelChanged(final String url) {

        //Refresh viewer in UI thread
        Display.getDefault().asyncExec( new Runnable() {

            public void run() {
                viewer.refresh();
                ScrapingModel scrapeModel = net.bioclipse.browser.Activator
                .getDefault().getScrapingModel();
                for (final ScrapingPage page : scrapeModel.getScrapingPages()){
                    if (page.getUrl().equals( url )){
                        viewer.expandToLevel( page, 2 );
                    }
                }
            }
        });

    }


    void initilizeDrag() {
        int operations = DND.DROP_COPY|DND.DROP_MOVE;
        Transfer[] transferTypes = new Transfer[] { MoleculeTransfer.getInstance(),
                                                    PluginTransfer.getInstance()};
        DragSource source = new DragSource( viewer.getControl(), operations );
        source.setTransfer( transferTypes );
        source.addDragListener( new DragSourceAdapter() {

            public void dragSetData( DragSourceEvent event ) {
                IStructuredSelection ssel=(IStructuredSelection) storedSelection;
                List<IMolecule> mols=new ArrayList<IMolecule>();
                for (Object obj : ssel.toList()){
                    if ( obj instanceof IMolecule ) {
                        mols.add((IMolecule) obj);
                    }
                }
                IMolecule[] molarray=mols.toArray(new IMolecule[0]);
                
                if(MoleculeTransfer.getInstance().isSupportedType( event.dataType )){
                    event.data = molarray;
                }
                else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
                    byte[] data = MoleculeTransfer.getInstance().toByteArray(molarray);
                    event.data = new PluginTransferData("molecule-transfer-format", data);
                 }
            }
        });
    }

    
}
