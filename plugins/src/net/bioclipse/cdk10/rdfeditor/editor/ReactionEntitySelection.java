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
package net.bioclipse.cdk10.rdfeditor.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;

public class ReactionEntitySelection implements IStructuredSelection {

    Set<ReactionTableEntry> selectionSet;
    
    public ReactionEntitySelection(Set<ReactionTableEntry> set) {
        selectionSet=set;
    }

    public ReactionEntitySelection(ReactionTableEntry entry) {
        selectionSet=new HashSet<ReactionTableEntry>();
        selectionSet.add(entry);
    }

    public Object getFirstElement() {
        return selectionSet.toArray()[0];
    }

    public Iterator iterator() {
        return selectionSet.iterator();
    }

    public int size() {
        return selectionSet.size();
    }

    public Object[] toArray() {
        return selectionSet.toArray();
    }

    public List toList() {
        List lst=new ArrayList<ReactionTableEntry>();
        lst.addAll(selectionSet);
        return lst;
    }

    public boolean isEmpty() {
        if (selectionSet==null) return true;
        if (selectionSet.size()<=0) return true;
        return false;
    }

}
