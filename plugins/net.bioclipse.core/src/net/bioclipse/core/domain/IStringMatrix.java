/*******************************************************************************
 * Copyright (c) 2009-2010  Egon Willighagen <egonw.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 *******************************************************************************/
package net.bioclipse.core.domain;

import java.util.List;

/**
 * Generalization if the IMatrixResource from the Statistics Feature.
 */
public interface IStringMatrix {

    public abstract void set(int row, int col, String value);
    public abstract void set(int row, String col, String value);

    public abstract String get(int row, int col);
    public abstract String get(int row, String col);

    public abstract int getColumnCount();
    public abstract int getColumnNumber(String col);

    public abstract int getRowCount();

    public abstract void setSize(int row, int col);

    public abstract boolean hasRowHeader();

    public abstract boolean hasColHeader();

    public abstract String getColumnName(int index);

    public abstract String getRowName(int index);

    public abstract List<String> getColumnNames();

    public abstract List<String> getRowNames();

    public abstract void setColumnName(int index, String name);

    public abstract void setRowName(int index, String name);

    public List<String> getColumn(int index);
    public List<String> getColumn(String col);
    
}