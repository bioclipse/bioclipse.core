/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/

package net.bioclipse.ui.editors.keyword;

/**
 * Keywords for Jmol.
 * 
 * @author ola
 */
public abstract class Keywords {

	String[][] stringlist;
	
	public String[][] getStringlist() {
		return stringlist;
	}

	public void setStringlist(String[][] stringlist) {
		this.stringlist = stringlist;
	}

	/**
	 * Concatenate the String[][] above into one array. Can make use of concatenateStringArrays,
	 */
	public String[] getAllKeywords(){

		int totallength=0;
		for (int i=0; i< stringlist.length;i++){
			totallength=totallength+stringlist[i].length;
		}

		if (totallength<=0) return null;
		String[] allKeywords=new String[totallength];

		int cnt=0;
		for (int i=0; i< stringlist.length;i++){
			for (int j=0; j<stringlist[i].length;j++){
				allKeywords[cnt]=stringlist[i][j];
				cnt++;
			}
		}
		
		return allKeywords;
	}

	
	
}
