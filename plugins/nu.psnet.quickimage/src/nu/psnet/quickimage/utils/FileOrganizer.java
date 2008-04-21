/*
 * Software released under Common Public License (CPL) v1.0
 */
package nu.psnet.quickimage.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * @author Per Salomonsson
 * 
 */
public class FileOrganizer {
	public String path;
	public String filename;
	private int index = 0;
	private File[] files;

	public void setPath(String path, String filename) {
		this.path = path;
		this.filename = filename;
		initList();
	}

	public File getNextFile() {
		index = (index + 1) % files.length;

		return files[index];
	}

	public File getPreviousFile() {
		if (index > 0)
			index--;

		return files[index];
	}

	public boolean hasNext() {
		return (files.length - 1 != index);
	}

	public boolean hasPrevious() {
		return (index != 0);
	}

	public File getCurrentFile() {
		return files[index];
	}

	public String getCurrentPath() {
		return path;
	}

	private void initList() {
		File f = new File(path);
		files = f.listFiles(new ImageFileFilter());

		if (files != null) {
			String[] sfiles = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				sfiles[i] = files[i].getAbsolutePath();
			}

			// TODO kolla upp hur sortering fungerar i eclipse package explorer
			Arrays.sort(sfiles, String.CASE_INSENSITIVE_ORDER);

			for (int i = 0; i < sfiles.length; i++) {
				// TODO fixa en exakt jämförelse och inte endswidth
				if (sfiles[i].endsWith(filename)) {
					index = i;
				}

				files[i] = new File(sfiles[i]);
			}
		}
	}
}

class ImageFileFilter implements FileFilter {
	String[] patterns = new String[] { ".gif", ".jpeg", ".jpg", ".png", ".ico",
			".bmp" };

	public boolean accept(File f) {
		for (int i = 0; i < patterns.length; i++) {
			if (f.getName().toLowerCase().endsWith(patterns[i]))
				return true;
		}

		return false;
	}
}
