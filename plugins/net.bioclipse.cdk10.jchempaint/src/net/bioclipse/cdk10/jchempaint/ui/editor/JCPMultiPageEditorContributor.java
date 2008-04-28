package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.controller.PopupController2D;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class JCPMultiPageEditorContributor extends MultiPageEditorActionBarContributor {
	private ArrayList actionList;
	private IEditorPart activeEditorPart;
	public JCPAction lastaction= null;
	/**
	 * Creates a multi-page contributor.
	 */
	public JCPMultiPageEditorContributor() {
		super();
		createActions();
	}
	
	
	/**
	 * Returns the action registed with the given text editor.
	 * @return IAction or null if editor is null.
	 */

	private void createActions() {
		actionList = ToolBarMaker.createToolbar(this);
//		sampleAction = new Action() {
//			public void run() {
//				MessageDialog.openInformation(null, "Bc_jcp_swt Plug-in", "Sample Action Executed");
//			}
//		};
//		sampleAction.setText("Sample Action");
//		sampleAction.setToolTipText("Sample Action tool tip");
//		sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(IDE.SharedImages.IMG_OBJS_TASK_TSK));
	}
	public void contributeToMenu(IMenuManager manager) {
		IMenuManager menu = new MenuManager("JChemPaint");
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		MenuBarMaker.createMenuBar(this, menu);
	}
	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		for (int i=0; i< actionList.size(); i++) {
			Object entry = actionList.get(i);
			if (entry instanceof IAction) {
				manager.add((IAction) entry);
			}
			else if (entry instanceof Separator) {
				manager.add((IContributionItem) entry);
			}
		}
	}
//	@Override
	public void setActivePage(IEditorPart activeEditor) {
//		if (activeEditorPart == activeEditor)
//			return;
//		activeEditorPart = activeEditor;		
	}


//	@Override
	public void setActiveEditor(IEditorPart part) {
		if (!(activeEditorPart == part)) {
			this.activeEditorPart = part;
//			super.setActiveEditor(part);
			if (((MDLMolfileEditor)activeEditorPart).getJcpModel() != null) {
				registerModel(((MDLMolfileEditor)activeEditorPart).getJcpModel());
				// FIXME egonw: likely crucial call
//				((MDLMolfileEditor)activeEditorPart).setContributor(this);
			}
		}
	}


	public IEditorPart getActiveEditorPart() {
		return activeEditorPart;
	}
	
	
	public void registerModel(JChemPaintModel model)
	{
		if (model != null) {
			DrawingPanel drawingPanel = ((MDLMolfileEditor)activeEditorPart).getDrawingPanel();
			
			/*new code -  functional group*/
			String filename = "org/openscience/cdk/applications/jchempaint/resources/text/funcgroups.txt";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
	        
	        HashMap funcgroups = new HashMap();
	        SmilesParser sp=new SmilesParser();
			StringBuffer sb=new StringBuffer();
			if (ins != null) {
				InputStreamReader isr = new InputStreamReader(ins);
				try{
					while(true){
						int i=isr.read();
						if(i==-1){
							break;
						}else if(((char)i)=='\n' || ((char)i)=='\r'){
							if(!sb.toString().equals("")){
								StringTokenizer st=new StringTokenizer(sb.toString());
								String key=(String)st.nextElement();
								String value=(String)st.nextElement();
								funcgroups.put(key, sp.parseSmiles(value));
								funcgroups.put(key.toUpperCase(), sp.parseSmiles(value));
								sb=new StringBuffer();
							}
						}else{
							sb.append((char)i);
						}
					}
					if(!sb.toString().equals("")){
						StringTokenizer st=new StringTokenizer(sb.toString());
						String key=(String)st.nextElement();
						String value=(String)st.nextElement();
						funcgroups.put(key, sp.parseSmiles(value));
						funcgroups.put(key.toUpperCase(), sp.parseSmiles(value));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			PopupController2D inputAdapter = new BCJCPPopupController((ChemModel) model.getChemModel(), model.getRendererModel(),model.getControllerModel(), null, null, ((MDLMolfileEditor)activeEditorPart).getJcpComposite(),funcgroups);
			JCPBioclipseUndoRedoHandler undoRedoHandler=new JCPBioclipseUndoRedoHandler();
			undoRedoHandler.setDrawingPanel(((MDLMolfileEditor)this.getActiveEditorPart()).getDrawingPanel());
			undoRedoHandler.setJcpm(model);
			undoRedoHandler.setUndoContext(((MDLMolfileEditor)this.getActiveEditorPart()).getUndoContext());
			inputAdapter.setUndoRedoHandler(undoRedoHandler);
			setupPopupMenus(inputAdapter);
			Renderer2DModel rendererModel = model.getRendererModel();
			model.getControllerModel().setBondPointerLength(rendererModel.getBondLength());
			model.getControllerModel().setRingPointerLength(rendererModel.getBondLength());
	
	//		model.getRendererModel().addCDKChangeListener((ICDKChangeListener) activeEditorPart);
			inputAdapter.addCDKChangeListener(model);
			//drawingPanel.setJChemPaintModel(model);
			drawingPanel.addMouseListener(inputAdapter);
			drawingPanel.addMouseMotionListener(inputAdapter);
			//Somehow this registration does not work. If it would, element symbols could be changed via keyboard
			drawingPanel.addKeyListener(inputAdapter);
		}
	}
	
	public void updateModel(IChemModel chemModel){
		((PopupController2D)((MDLMolfileEditor)activeEditorPart).getDrawingPanel().getKeyListeners()[0]).setChemModel(chemModel);
		((MDLMolfileEditor)activeEditorPart).getDrawingPanel().updateRingSetInRenderer();
	}
	
	public void setupPopupMenus(PopupController2D inputAdapter)
	{
		if (inputAdapter.getPopupMenu(Atom.class) == null)
		{
			inputAdapter.setPopupMenu(Atom.class, new JChemPaintPopupMenu(this, "atom"));
		}
		if (inputAdapter.getPopupMenu(PseudoAtom.class) == null)
		{
			inputAdapter.setPopupMenu(PseudoAtom.class, new JChemPaintPopupMenu(this, "pseudo"));
		}
		if (inputAdapter.getPopupMenu(Bond.class) == null)
		{
			inputAdapter.setPopupMenu(Bond.class, new JChemPaintPopupMenu(this, "bond"));
		}
		if (inputAdapter.getPopupMenu(ChemModel.class) == null)
		{
			inputAdapter.setPopupMenu(ChemModel.class, new JChemPaintPopupMenu(this, "chemmodel"));
		}
		if (inputAdapter.getPopupMenu(Reaction.class) == null)
		{
			inputAdapter.setPopupMenu(Reaction.class, new JChemPaintPopupMenu(this, "reaction"));
		}
	}
}
