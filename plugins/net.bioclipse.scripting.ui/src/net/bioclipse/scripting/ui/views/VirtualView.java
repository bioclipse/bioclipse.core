/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.scripting.ui.views;
import java.util.ArrayList;
import java.util.List;
import net.bioclipse.core.domain.IBioObject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
/**
 * View containing virtual resources.
 * 
 * @author ola
 *
 */
public class VirtualView extends ViewPart {
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action action1;
    private Action action2;
    private Action doubleClickAction;
    static class VirtualObject implements IAdaptable {
        private IBioObject bioObject;
        private VirtualParent parent;
        private String name;
        public VirtualObject(String name) {
            this.name = name;
        }
        public VirtualObject(IBioObject bioObject) {
            this.bioObject = bioObject;
        }
        public void setParent(VirtualParent parent) {
            this.parent = parent;
        }
        public VirtualParent getParent() {
            return parent;
        }
        public String toString() {
            return name;
        }
        public Object getAdapter(Class key) {
            return null;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    static class VirtualParent extends VirtualObject {
        private List<VirtualObject> children;
        public VirtualParent(String name) {
            super(name);
            children = new ArrayList<VirtualObject>();
        }
        public void addChild(VirtualObject child) {
            children.add(child);
            child.setParent(this);
        }
        public void removeChild(VirtualObject child) {
            children.remove(child);
            child.setParent(null);
        }
        public VirtualObject [] getChildren() {
            return (VirtualObject [])children.toArray(new VirtualObject[children.size()]);
        }
        public boolean hasChildren() {
            return children.size()>0;
        }
    }
    class ViewContentProvider implements IStructuredContentProvider, 
                                                ITreeContentProvider {
        private VirtualParent invisibleRoot;
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
                if (invisibleRoot==null) initialize();
                return getChildren(invisibleRoot);
            }
            return getChildren(parent);
        }
        public Object getParent(Object child) {
            if (child instanceof VirtualObject) {
                return ((VirtualObject)child).getParent();
            }
            return null;
        }
        public Object [] getChildren(Object parent) {
            if (parent instanceof VirtualParent) {
                return ((VirtualParent)parent).getChildren();
            }
            return new Object[0];
        }
        public boolean hasChildren(Object parent) {
            if (parent instanceof VirtualParent)
                return ((VirtualParent)parent).hasChildren();
            return false;
        }
/*
 * We will set up a dummy model to initialize tree heararchy.
 * In a real code, you will connect to a real model and
 * expose its hierarchy.
 */
        private void initialize() {
            VirtualObject to1 = new VirtualObject("Leaf 1");
            VirtualObject to2 = new VirtualObject("Leaf 2");
            VirtualObject to3 = new VirtualObject("Leaf 3");
            VirtualParent p1 = new VirtualParent("Parent 1");
            p1.addChild(to1);
            p1.addChild(to2);
            p1.addChild(to3);
            VirtualObject to4 = new VirtualObject("Leaf 4");
            VirtualParent p2 = new VirtualParent ("Parent 2");
            p2.addChild(to4);
            VirtualParent root = new VirtualParent("Root");
            root.addChild(p1);
            root.addChild(p2);
            invisibleRoot = new VirtualParent("");
            invisibleRoot.addChild(root);
        }
    }
    static class ViewLabelProvider extends LabelProvider {
        public String getText(Object obj) {
            return obj.toString();
        }
        public Image getImage(Object obj) {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if (obj instanceof VirtualParent)
               imageKey = ISharedImages.IMG_OBJ_FOLDER;
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }
    class NameSorter extends ViewerSorter {
    }
    /**
     * The constructor.
     */
    public VirtualView() {
    }
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                VirtualView.this.fillContextMenu(manager);
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
        manager.add(action1);
        manager.add(new Separator());
        manager.add(action2);
    }
    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }
    private void makeActions() {
        action1 = new Action() {
            public void run() {
                showMessage("Action 1 executed");
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
            getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        action2 = new Action() {
            public void run() {
                showMessage("Action 2 executed");
            }
        };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                showMessage("Double-click detected on "+obj.toString());
            }
        };
    }
    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }
    private void showMessage(String message) {
        MessageDialog.openInformation(
            viewer.getControl().getShell(),
            "Virtual Navigator",
            message);
    }
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }
}