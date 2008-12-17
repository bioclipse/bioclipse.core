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
import net.bioclipse.core.domain.IBioObject;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
public class BioListPropertySource implements IPropertySource2{
    protected static final String PROPERTY_SIZE = "Size";
    private final Object PropertiesTable[][] =
    {
        { PROPERTY_SIZE,
            new TextPropertyDescriptor(PROPERTY_SIZE,"Size")},
    };
    private IBioObject item;
    private ArrayList<IPropertyDescriptor> properties;
    private HashMap<String, Object> valueMap;
    /**
     * Constructor
     */
    public BioListPropertySource(IBioObject item) {
        properties=new ArrayList<IPropertyDescriptor>();
        valueMap=new HashMap<String, Object>();
        this.item=item;
        //Build the arraylist of propertydescriptors
        for (int i=0;i<PropertiesTable.length;i++) {
            // Add each property supported.
            PropertyDescriptor descriptor;
            descriptor = (PropertyDescriptor)PropertiesTable[i][1];
            descriptor.setCategory("General");
            properties.add(descriptor);
        }
        //Build the hashmap of property->value pair
        valueMap.put(PROPERTY_SIZE,"123456");
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
            propertyDescriptors[i]=(IPropertyDescriptor) properties.get(i);
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
    public HashMap<String, Object> getValueMap() {
        return valueMap;
    }
    public void setValueMap(HashMap<String, Object> valueMap) {
        this.valueMap = valueMap;
    }
    public IBioObject getItem() {
        return item;
    }
    public void setItem(IBioObject item) {
        this.item = item;
    }
}
