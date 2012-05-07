package net.bioclipse.core.domain;

import java.util.List;

public interface IDataset {

	public static final String CSV_SEPARATOR = "\t";

	public String getFileExtension();
	public String getFileContents();

	public List<String> getResponseValues();
	public List<String> getColHeaders();
	
}
