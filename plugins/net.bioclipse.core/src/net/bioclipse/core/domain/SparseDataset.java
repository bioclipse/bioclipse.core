/* ******************************************************************************
 * Copyright (c) 2011  Ola Spjuth <ola@bioclipse.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.core.domain;

import java.awt.Point;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A dataset contains a matrix of values (Float) with headers and 
 * an optional response variable.
 * 
 * @author ola
 *
 */
public class SparseDataset implements IDataset{

	private static final String DEFAULT_SEPARATOR = " ";
	protected List<String> colHeaders;
	protected List<String> rowHeaders;
	protected String responseProperty;
	protected List<String> responseValues;
	protected LinkedHashMap<Point, Integer> values;

	
	public LinkedHashMap<Point, Integer> getValues() {
		return values;
	}
	public void setValues(LinkedHashMap<Point, Integer> values) {
		this.values = values;
	}
	public String getResponseProperty() {
		return responseProperty;
	}
	public void setResponseProperty(String responseProperty) {
		this.responseProperty = responseProperty;
	}
	public List<String> getResponseValues() {
		return responseValues;
	}
	public void setResponseValues(List<String> responseValues) {
		this.responseValues = responseValues;
	}
	public List<String> getColHeaders() {
		return colHeaders;
	}
	public void setColHeaders(List<String> colHeaders) {
		this.colHeaders = colHeaders;
	}
	public List<String> getRowHeaders() {
		return rowHeaders;
	}
	public void setRowHeaders(List<String> rowHeaders) {
		this.rowHeaders = rowHeaders;
	}

	public SparseDataset() {
		super();
	}

	public SparseDataset(List<String> colHeaders, List<String> rowHeaders,
			LinkedHashMap<Point, Integer> values) {
		super();
		this.colHeaders = colHeaders;
		this.rowHeaders = rowHeaders;
		this.values = values;
	}

	public SparseDataset(List<String> colHeaders, List<String> rowHeaders,
			String responseProperty, List<String> responseValues,
			LinkedHashMap<Point, Integer> values) {
		super();
		this.colHeaders = colHeaders;
		this.rowHeaders = rowHeaders;
		this.responseProperty = responseProperty;
		this.responseValues = responseValues;
		this.values = values;
	}

//    [,1]      [,2]       [,3]       [,4]
//[1,]  0.000000  0.000000 -0.7161187  0.0000000
//[2,]  0.000000  1.749898 -1.2605905 -1.5254161
//[3,] -2.012254  1.033870  0.0000000 -0.9921068
//[4,]  0.000000  0.000000  1.7928275  0.0000000
//[5,]  0.000000 -1.897589  1.7793862 -1.0933724
//
//> write.matrix.csr(tmp, file = "tmp.dat")
//	
//	ola$ cat tmp.dat
//	3:-0.7161187 
//	2:1.749898 3:-1.260590 4:-1.525416 
//	1:-2.012254 2:1.03387 4:-0.9921068 
//	3:1.792828 
//	2:-1.897589 3:1.779386 4:-1.093372 

	
	@Override
	public String getFileExtension() {
		return "csr";
	}

	@Override
	public String getFileContents() {
		return toSparseString(DEFAULT_SEPARATOR);
	}


	public String toSparseString(String separator) {

		StringBuffer buf = new StringBuffer(50000);

		int c=0;  //line number
		for (Point p : values.keySet()){
			if (p.x==c)
				buf.append(separator + p.y+":"+values.get(p));
			else if (p.x==(c+1)){
				//next line
				if (c!=0)  //not first line
					buf.append("\n");
				c++;
				buf.append(p.y+":"+values.get(p));
			}
		}
		buf.append("\n");  //last newline
		
		System.out.println(buf.toString());

		return buf.toString();
	}

	/**
	 * Convenience method to just return the response values.
	 * Remove leading and trailing '[' and ']'.
	 * 
	 * @return
	 */
	public String getResponseValuesRaw() {
		return responseValues.toString().substring(1, responseValues.toString().length()-1)+"\n";
	}

	
}
