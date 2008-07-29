/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting.ui.views;

import java.util.ArrayList;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.recording.HistoryEvent;
import net.bioclipse.recording.IHistory;
import net.bioclipse.recording.IHistoryListener;
import net.bioclipse.recording.IRecord;
import net.bioclipse.recording.JsScriptGenerator;
import net.bioclipse.ui.Activator;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class ActionHistoryView extends ViewPart implements IHistoryListener {

    private IHistory history;
    private List actionList;

    private static final Logger logger =
        Logger.getLogger(ActionHistoryView.class);


    public ActionHistoryView() {
        history = Activator.getDefault().getHistoryObject();
        history.addHistoryListener(this);
    }
/**
 * Clean up on exit
 */
    @Override
    public void dispose() {
        history.removeHistoryListener(this);
        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        actionList = new List(parent, SWT.MULTI);
        receiveHistoryEvent(null);

        addContextMenu();
    }

    private void addContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                mgr.add(new Action("Generate Javascript") {
                    @Override
                    public void run() {

                        java.util.List<IRecord> records =
                            new ArrayList<IRecord>();

                        for( int i : actionList.getSelectionIndices() ) {
                            records.add(history.getRecords().get(i));
                        }

                        String[] script
                            = new JsScriptGenerator()
                                  .generateScript(
                                          records.toArray(new IRecord[0]) );

                        //Set up string content
                        String content="";
                        StringBuilder sb = new StringBuilder();
                        for(String s : script) {
                            sb.append(s);
                            sb.append("\n");
                        }
                        content = sb.toString();

                        //Open editor with script as content


                        IFileStore fileStore= queryFileStore();
                        IEditorInput input= createEditorInput(fileStore);
                        String editorId= getEditorId(fileStore);
                        IWorkbenchPage page= getViewSite().getPage();
                        try {
                            IEditorPart editor=page.openEditor(input, editorId);
                            if (editor instanceof TextEditor) {
                                TextEditor ted = (TextEditor) editor;
                                IDocumentProvider pr=ted.getDocumentProvider();
                                IDocument doc=pr.getDocument(input);
//                                String currentContent=doc.get();
                                doc.set(content);
                            }

                        } catch (PartInitException e) {
                            LogUtils.debugTrace(logger, e);
                        }
                    }
                });
            }
        });

        Menu menu = mgr.createContextMenu(actionList);
        actionList.setMenu(menu);
    }

    @Override
    public void setFocus() {
    }

    @SuppressWarnings("restriction")
    private IEditorInput createEditorInput(IFileStore fileStore) {
        return new NonExistingFileEditorInput(fileStore, "New script");
    }

    @SuppressWarnings("restriction")
    private IFileStore queryFileStore() {
        IPath stateLocation= EditorsPlugin.getDefault().getStateLocation();
        IPath path= stateLocation.append("/_" + new Object().hashCode()); //$NON-NLS-1$
        return EFS.getLocalFileSystem().getStore(path);
    }

    private String getEditorId(IFileStore fileStore) {
        IWorkbench workbench= PlatformUI.getWorkbench();
        IEditorRegistry editorRegistry= workbench.getEditorRegistry();
        IEditorDescriptor descriptor= editorRegistry.getDefaultEditor(fileStore.getName());
        if (descriptor != null)
            return descriptor.getId();
        return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
    }



    public void receiveHistoryEvent(HistoryEvent e) {

        java.util.List<IRecord> newRecords
            = history.getRecords().subList( actionList.getItemCount(),
                                            history.getRecordCount() );

        for( IRecord r : newRecords ) {
            actionList.add( r.toString() );
        }
    }
}
