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
package net.bioclipse.core.domain.props;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class BasicPropertySource implements IPropertySource{

	private Object item;
	private ArrayList<IPropertyDescriptor> properties;
	private HashMap<String, String> valueMap;
	
	public BasicPropertySource(Object item) {
		this.item=item;
	}

	public boolean isPropertyResettable(Object id) {
		return false;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {

		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[properties.size()];
		for (int i=0; i< properties.size();i++){
			propertyDescriptors[i]=properties.get(i);
		}
		
		// Return it.
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		if (valueMap.containsKey(id))
			return valueMap.get(id);

		return null;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public ArrayList<IPropertyDescriptor> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<IPropertyDescriptor> properties) {
		this.properties = properties;
	}

	public HashMap<String, String> getValueMap() {
		return valueMap;
	}

	public void setValueMap(HashMap<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}


}
