/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.ui.contentlabelproviders;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
/** 
 * A class implementing ILabelProvider and returning the name for text if
 * the element is any resource. This can be used to build TreeViewers for browsing for any sort of files.
 *
 */
public class FolderLabelProvider implements ILabelProvider {
        public FolderLabelProvider() {
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
                return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
        public Image getImage(Object element) {
                return null;
        }
        public String getText(Object element) {
                if (element instanceof IResource) {
                        return ((IResource)element).getName();
                }else{
                        return "Unknown element";
                }
        }
}
