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
package net.bioclipse.cdk10.jchempaint.outline;

import java.util.ArrayList;

public class Container {

	String name;
	ArrayList<CDKChemObject> children;
	ArrayList<Container> subfolders;

	public Container(String name) {
		this.name=name;
		children=new ArrayList<CDKChemObject>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<CDKChemObject> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<CDKChemObject> children) {
		this.children = children;
	}
	public ArrayList<Container> getSubfolders() {
		return subfolders;
	}
	public void setSubfolders(ArrayList<Container> subfolders) {
		this.subfolders = subfolders;
	}

	//Convenience methods for children
	public void addChild(CDKChemObject co){
		children.add(co);
	}
	public void removeChild(CDKChemObject co){
		children.remove(co);
	}
	
	//Convenience methods for subfolders
	public void addSubFolder(Container co){
		subfolders.add(co);
	}
	public void removeSubFolder(Container co){
		subfolders.remove(co);
	}


	public void clear(){
		children.clear();
		subfolders.clear();
	}
	
}
