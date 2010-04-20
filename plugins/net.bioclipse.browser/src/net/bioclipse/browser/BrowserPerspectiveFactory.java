package net.bioclipse.browser;

import net.bioclipse.browser.views.ExtractsView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * 
 * @author ola
 *
 */
public class BrowserPerspectiveFactory implements IPerspectiveFactory {

    IPageLayout storedLayout;

    /**
     * This perspective's ID
     */
    public static final String ID_PERSPECTIVE =
        "net.bioclipse.browser.perspective";

    public static final String ID_NAVIGATOR = 
        "net.bioclipse.navigator";

    public static final String ID_JAVSCRIPT_CONSOLE = 
        "net.bioclipse.scripting.ui.views.JsConsoleView";

    /**
     * Create initial layout
     */
    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }

    private void defineLayout( IPageLayout layout ) {

        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        layout.setFixed(false);
        layout.addPerspectiveShortcut(ID_PERSPECTIVE);

        //Add layouts for views
        IFolderLayout left_folder_layout =
            layout.createFolder(
                    "navigator",
                    IPageLayout.LEFT,
                    0.20f,
                    editorArea);

//        IFolderLayout bottom_folder_layout =
//            layout.createFolder(
//                    "properties",
//                    IPageLayout.BOTTOM,
//                    0.70f,
//                    editorArea);

//        IFolderLayout left_bottom_folder_layout =
//            layout.createFolder(
//                    "leftbottom",
//                    IPageLayout.BOTTOM,
//                    0.70f,
//                    "explorer");

//        IFolderLayout right_bottom_folder_layout =
//            layout.createFolder(
//                    "rightbottom",
//                    IPageLayout.RIGHT,
//                    0.70f,
//                    "properties");

        IFolderLayout right_folder_layout =
            layout.createFolder(
                    "right",
                    IPageLayout.RIGHT,
                    0.70f,
                    editorArea);

        //Add views
        left_folder_layout.addView(ID_NAVIGATOR);
//        bottom_folder_layout.addView(IPageLayout.ID_PROP_SHEET);
//        bottom_folder_layout.addPlaceholder( ID_JAVSCRIPT_CONSOLE);
//        bottom_folder_layout.addView(IPageLayout.ID_PROGRESS_VIEW);
//        bottom_folder_layout.addView(ID_JAVSCRIPT_CONSOLE);

        right_folder_layout.addView(ExtractsView.VIEW_ID);
//        left_bottom_folder_layout.addView(MetaPrint2DReportView.VIEW_ID);

    }

    private void defineActions( IPageLayout layout ) {


        //Add ShowView shortcuts
        layout.addShowViewShortcut(ID_NAVIGATOR);    
        layout.addShowViewShortcut(ID_JAVSCRIPT_CONSOLE);    
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);    
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);    
//        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);    
//        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);    
        
    }
    
    
}
