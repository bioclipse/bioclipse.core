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
package net.bioclipse.cdk10.sdfeditor.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.bioclipse.core.domain.IMolecule;

import org.eclipse.jface.viewers.IStructuredSelection;

public class StructureEntitySelection implements IStructuredSelection {

    Set<StructureTableEntry> selectionSet;
    
    public StructureEntitySelection(Set<StructureTableEntry> set) {
        selectionSet=set;
    }

    public StructureEntitySelection(StructureTableEntry entry) {
        selectionSet=new HashSet<StructureTableEntry>();
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
        List lst=new ArrayList<StructureTableEntry>();
        lst.addAll(selectionSet);
        return lst;
    }

    public boolean isEmpty() {
        if (selectionSet==null) return true;
        if (selectionSet.size()<=0) return true;
        return false;
    }

}
