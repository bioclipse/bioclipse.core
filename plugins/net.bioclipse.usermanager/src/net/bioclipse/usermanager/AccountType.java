/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.usermanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @author jonalv
 *
 */
public class AccountType implements Serializable {
    private static final long serialVersionUID = 2769292008460458052L;
    private List<Property> properties; 
    private String name;
    /**
     * Standard Constructor
     */
    public AccountType() {
        properties = new ArrayList<Property>();
        this.name  = "";
    }
    /**
     * Constructor
     * 
     * @param name the name of the account type 
     */
    public AccountType(String name) {
        properties = new ArrayList<Property>();
        this.name = name;
    }
    /**
     * Constructor creating a new deep copy of the 
     * given account type
     * 
     * @param accountType account type to copy
     */
    public AccountType(AccountType accountType) {
        name = accountType.name;
        properties = new ArrayList<Property>();
        for(Property p : accountType.properties) {
            properties.add(new Property(p));
        }
    }
    /**
     * Adds a new property
     * 
     * @param name the name of the property
     * @param required whether the property is required
     */
    public void addProperty( String name, boolean required) {
        properties.add( new Property(name, required) );
    }
    /**
     * Returns whether the account type has a property 
     * with the given name
     * 
     * @param name name of property
     * @return whether the account type has such a property  
     */
    public boolean hasProperty( String name ) {
        for ( Property property : properties )
            if ( name.equals( property.name ) )
                return true;
        return false;
    }
    /**
     * Returns a property with a given name.
     * 
     * @param name the name of the property
     * @return the property
     */
    public Property getProperty( String name ) {
        for ( Property property : properties )
            if ( name.equals( property.name ) )
                return property;
        return null;
    }
    /**
     * @return all required properties
     */
    public List<Property> getRequiredProperties() {
        ArrayList<Property> requiredProperties = new ArrayList<Property>();
        for ( Property property : properties )
            if ( property.isRequired() )
                requiredProperties.add( property );
        return requiredProperties;
    }
    /**
     * @return all properties
     */
    public Collection<Property> getProperties() {
        return properties;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    public String toString() {
        return name;
    }
    /**
     * The property class
     * 
     * @author jonathan
     *
     */
    public static class Property implements Serializable {
        private static final long serialVersionUID = -5736389052147326378L;
        String  name;
        boolean required;
        /**
         * @param name the property's name
         * @param required whether the property is required
         */
        public Property(String name, boolean required) {
            super();
            this.name = name;
            this.required = required;
        }
        /**
         * Constructor that creates a deep copy of the 
         * given property
         * 
         * @param p property to copy
         */
        public Property(Property p) {
            super();
            this.name     = p.name;
            this.required = p.required;
        }
        /**
         * @return name
         */
        public String getName() {
            return name;
        }
        /**
         * @param name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * @return whether the property is required 
         */
        public boolean isRequired() {
            return required;
        }
        /**
         * @param required required status to set
         */
        public void setRequired(boolean required) {
            this.required = required;
        }
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((name == null) ? 0 : name.hashCode());
            result = PRIME * result + (required ? 1231 : 1237);
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Property other = (Property) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (required != other.required)
                return false;
            return true;
        }
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + 
                         ( (properties == null) ? 0
                                                : properties.hashCode() );
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccountType other = (AccountType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }
}
