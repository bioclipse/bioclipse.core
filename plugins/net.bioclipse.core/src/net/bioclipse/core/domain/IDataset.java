package net.bioclipse.core.domain;

public interface IDataset {

	public static final String CSV_SEPARATOR = "\t";

	public String getFileExtension();
	public String getFileContents();

	
}
