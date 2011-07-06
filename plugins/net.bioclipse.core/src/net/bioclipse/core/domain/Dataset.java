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

import java.util.List;

/**
 * A dataset contains a matrix of values (Float) with headers and 
 * an optional response variable.
 * 
 * @author ola
 *
 */
public class Dataset {

	public static final String CSV_SEPARATOR = "\t";

	protected List<String> colHeaders;
	protected List<String> rowHeaders;
	protected List<List<Float>> values;
	protected String responseProperty;
	protected List<String> responseValues;

	
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
	public List<List<Float>> getValues() {
		return values;
	}
	public void setValues(List<List<Float>> values) {
		this.values = values;
	}
	
	public Dataset() {
		super();
	}
	public Dataset(List<String> colHeaders, List<String> rowHeaders,
			List<List<Float>> values) {
		super();
		this.colHeaders = colHeaders;
		this.rowHeaders = rowHeaders;
		this.values = values;
	}

	public Dataset(List<String> colHeaders, List<String> rowHeaders,
			List<List<Float>> values, String responseProperty,
			List<String> responseValues) {
		this(colHeaders, rowHeaders, values);
		this.responseProperty=responseProperty;
		this.responseValues=responseValues;
	}

	public String asCSV() {
		return asCSV(CSV_SEPARATOR);
	}
	
	public String asCSV(String separator) {

		//Collect as Stringbuffer
		StringBuffer buf = new StringBuffer(50000);
		String rowstring = getCSVString(colHeaders, separator);
		if (responseProperty!=null)
			 rowstring = rowstring + responseProperty;

		buf.append(" " + separator + rowstring + "\n");

		int c=0;
		for (List<Float> row : values){
			rowstring = getCSVString(row, separator);
			buf.append(rowHeaders.get(c) + separator + rowstring);

			if (responseProperty!=null)
				buf.append(responseValues.get(c));

			buf.append("\n");
			c++;
			if (c%10==0)
				System.out.println("Processed " + c + "/" + values.size() + " rows");
		}
		return buf.toString();
	}

	private String getCSVString(List<?> entries, String separator) {
		
		StringBuffer b = new StringBuffer();
		for (Object s : entries){
			b.append(s + separator);
		}
		return b.toString();
	}
	
}
