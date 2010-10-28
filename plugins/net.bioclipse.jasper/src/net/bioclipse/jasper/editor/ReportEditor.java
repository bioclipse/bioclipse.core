package net.bioclipse.jasper.editor;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Map;

import net.bioclipse.core.api.BioclipseException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.jasperassistant.designer.viewer.DefaultHyperlinkHandler;
import com.jasperassistant.designer.viewer.IReportViewer;
import com.jasperassistant.designer.viewer.ReportViewer;
import com.jasperassistant.designer.viewer.StatusBar;
import com.jasperassistant.designer.viewer.actions.ExportAsCsvAction;
import com.jasperassistant.designer.viewer.actions.ExportAsHtmlAction;
import com.jasperassistant.designer.viewer.actions.ExportAsJasperReportsAction;
import com.jasperassistant.designer.viewer.actions.ExportAsMultiXlsAction;
import com.jasperassistant.designer.viewer.actions.ExportAsPdfAction;
import com.jasperassistant.designer.viewer.actions.ExportAsRtfAction;
import com.jasperassistant.designer.viewer.actions.ExportAsSingleXlsAction;
import com.jasperassistant.designer.viewer.actions.ExportAsXmlAction;
import com.jasperassistant.designer.viewer.actions.ExportAsXmlWithImagesAction;
import com.jasperassistant.designer.viewer.actions.ExportMenuAction;
import com.jasperassistant.designer.viewer.actions.FirstPageAction;
import com.jasperassistant.designer.viewer.actions.LastPageAction;
import com.jasperassistant.designer.viewer.actions.NextPageAction;
import com.jasperassistant.designer.viewer.actions.PageNumberContributionItem;
import com.jasperassistant.designer.viewer.actions.PreviousPageAction;
import com.jasperassistant.designer.viewer.actions.PrintAction;
import com.jasperassistant.designer.viewer.actions.ReloadAction;
import com.jasperassistant.designer.viewer.actions.ZoomActualSizeAction;
import com.jasperassistant.designer.viewer.actions.ZoomComboContributionItem;
import com.jasperassistant.designer.viewer.actions.ZoomFitPageAction;
import com.jasperassistant.designer.viewer.actions.ZoomFitPageWidthAction;
import com.jasperassistant.designer.viewer.actions.ZoomInAction;
import com.jasperassistant.designer.viewer.actions.ZoomOutAction;

/**
 *  An editor wrappping a Jasper ReportViewer.
 *  
 * @author ola
 *
 */
public class ReportEditor extends EditorPart implements ISelectionProvider{

	private ReportViewer reportViewer = new ReportViewer(SWT.BORDER);

	private static final Logger logger = Logger.getLogger(ReportEditor.class);
	
	@Override
	public void createPartControl(Composite parent) {
		
		initMenu();
		initToolBar();


		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		container.setLayout(layout);

		Control reportViewerControl = reportViewer.createControl(container);
		reportViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		StatusBar statusBar = new StatusBar();
		statusBar.setReportViewer(reportViewer);
		Control statusBarControl = statusBar.createControl(container);
		statusBarControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reportViewer.addHyperlinkListener(new DefaultHyperlinkHandler());


		
		

		/*

//		  // prepare report and data
//        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/reports/userList.jrxml");
//        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(results);
//        
//        // generate pdf file
//        JasperDesign jasperDesign = JRXmlLoader.load(is);
//        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, 
//                                                       ds);
		
		
		

		String path;
		try {
//			path = FileUtil.getFilePath("demo/FirstJasper.jrprint", Activator.PLUGIN_ID);
			path = FileUtil.getFilePath("demo/PieChartReport.jrprint", Activator.PLUGIN_ID);
//			path = FileUtil.getFilePath("demo/HyperlinkReport.jrprint", Activator.PLUGIN_ID);
			reportViewer.loadDocument(path, false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
*/

		getSite().setSelectionProvider(this);

	}


	
	
	
	
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

        setSite( site );
        setInput( input );

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
	}

	
	
	private void initMenu() {
		
        MenuManager mm = new MenuManager();
        mm.add( new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        getSite().registerContextMenu("net.bioclipse.jasper.report.context",
                mm, this);

		MenuManager export = new MenuManager("Export");
		export.add(new ExportAsPdfAction(reportViewer));
        export.add(new ExportAsRtfAction(reportViewer));
        export.add(new ExportAsJasperReportsAction(reportViewer));
		export.add(new ExportAsHtmlAction(reportViewer));
		export.add(new ExportAsSingleXlsAction(reportViewer));
		export.add(new ExportAsMultiXlsAction(reportViewer));
		export.add(new ExportAsCsvAction(reportViewer));
		export.add(new ExportAsXmlAction(reportViewer));
		export.add(new ExportAsXmlWithImagesAction(reportViewer));

		MenuManager file = new MenuManager("File");
		file.add(new ReloadAction(reportViewer));
		file.add(new Separator());
		file.add(export);
		file.add(new Separator());
		file.add(new PrintAction(reportViewer));
		mm.add(file);

		MenuManager view = new MenuManager("View");
		view.add(new ZoomOutAction(reportViewer));
		view.add(new ZoomInAction(reportViewer));
		view.add(new Separator());
		view.add(new ZoomActualSizeAction(reportViewer));
		view.add(new ZoomFitPageAction(reportViewer));
		view.add(new ZoomFitPageWidthAction(reportViewer));
		mm.add(view);

		MenuManager nav = new MenuManager("Navigate");
		nav.add(new FirstPageAction(reportViewer));
		nav.add(new PreviousPageAction(reportViewer));
		nav.add(new NextPageAction(reportViewer));
		nav.add(new LastPageAction(reportViewer));
		mm.add(nav);

	}



	private void initToolBar() {
		
		IToolBarManager tbManager = getEditorSite().getActionBars()
		.getToolBarManager();
		
		ExportMenuAction exportMenu = new ExportMenuAction(reportViewer);
		IAction pdfAction = null;
		exportMenu.getMenuManager().add(
				pdfAction = new ExportAsPdfAction(reportViewer));
        exportMenu.getMenuManager().add(
                new ExportAsRtfAction(reportViewer));
        exportMenu.getMenuManager().add(
				new ExportAsJasperReportsAction(reportViewer));
		exportMenu.getMenuManager().add(new ExportAsHtmlAction(reportViewer));
		exportMenu.getMenuManager().add(
				new ExportAsSingleXlsAction(reportViewer));
		exportMenu.getMenuManager().add(
				new ExportAsMultiXlsAction(reportViewer));
		exportMenu.getMenuManager().add(new ExportAsCsvAction(reportViewer));
		exportMenu.getMenuManager().add(new ExportAsXmlAction(reportViewer));
		exportMenu.getMenuManager().add(
				new ExportAsXmlWithImagesAction(reportViewer));
		exportMenu.setDefaultAction(pdfAction);

		tbManager.add(exportMenu);
		tbManager.add(new PrintAction(reportViewer));
		tbManager.add(new ReloadAction(reportViewer));
		tbManager.add(new Separator());
		tbManager.add(new FirstPageAction(reportViewer));
		tbManager.add(new PreviousPageAction(reportViewer));
		if (SWT.getPlatform().equals("win32")) //$NON-NLS-1$
			tbManager.add(new PageNumberContributionItem(reportViewer));
		tbManager.add(new NextPageAction(reportViewer));
		tbManager.add(new LastPageAction(reportViewer));
		tbManager.add(new Separator());
		tbManager.add(new ZoomActualSizeAction(reportViewer));
		tbManager.add(new ZoomFitPageAction(reportViewer));
		tbManager.add(new ZoomFitPageWidthAction(reportViewer));
		tbManager.add(new Separator());
		tbManager.add(new ZoomOutAction(reportViewer));
		tbManager.add(new ZoomComboContributionItem(reportViewer));
		tbManager.add(new ZoomInAction(reportViewer));
	}

	/**
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		container.setLayout(layout);

		Control reportViewerControl = reportViewer.createControl(container);
		reportViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		StatusBar statusBar = new StatusBar();
		statusBar.setReportViewer(reportViewer);
		Control statusBarControl = statusBar.createControl(container);
		statusBarControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reportViewer.addHyperlinkListener(new DefaultHyperlinkHandler());

		return container;
	}

	/**
	 * Returns the report viewer used for viewing reports.
	 * 
	 * @return the report viewer
	 */
	public IReportViewer getReportViewer() {
		return reportViewer;
	}


	
	
	
	
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener arg0) {
	}
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(new Object());
	}
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
	}
	@Override
	public void setSelection(ISelection arg0) {
	}
	
	
	@SuppressWarnings("unchecked")
	public void openReport(String reportAbsolutePath, Map parameters, 
			Collection beanCollection  ) throws BioclipseException {
		
		logger.debug("Opening jasper report: " + reportAbsolutePath);
		logger.debug("Number of parameters: " + parameters != null ? parameters.size() : "");
		logger.debug("Number of beans: " + beanCollection.size());
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(reportAbsolutePath));

			//Create a new JR data source and populate with our collection
			JRDataSource reportSource = 
				new JRBeanCollectionDataSource(beanCollection );
			JasperPrint jp = JasperFillManager.fillReport( fis, parameters, 
					reportSource);
			reportViewer.setDocument(jp);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BioclipseException(e.getMessage(), e);
		} 

	}

}
