/* *****************************************************************************
 * Copyright (c) 2007-2009 Bioclipse Project
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
import java.net.MalformedURLException;
import java.net.URL;
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
    
    private String name, logoPath = "";
       
    /**
     * Standard Constructor
     */
    public AccountType() {
        properties = new ArrayList<Property>();
        this.name  = "";
        this.logoPath = "";
    }
    
    /**
     * Constructor
     * 
     * @param name the name of the account type 
     */
    public AccountType(String name) {
        properties = new ArrayList<Property>();
        this.name = name;
        this.logoPath = "";
    }
    
    /**
     * Constructor 
     * 
     * @param name the name of the account type 
     * @param path the path to an logo for the account type
     */
    public AccountType(String name, String path) {
        properties = new ArrayList<Property>();
        this.name = name;
        this.logoPath = path;
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
    public void addProperty( String name, boolean required, boolean secret) {
        properties.add( new Property(name, required, secret) );
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
     * A set-method to give the account type a logo.
     * 
     * @param logo The image that contains the logo
     */
    public void setLogoPath(String path) {
    	this.logoPath = path;
    }
    
    /**
     * This method returns an URL with the path to an logotype that is 
     * associated with the account-type, if there's non it returns null
     * 
     * @return The path as an URL
     */
    public URL getLogoPath() { 
    	URL url = null;
    	try {
    		url = new URL(logoPath);
    	} catch(MalformedURLException e) {
    		System.out.println("Bad URL:\n"+e);
    	}
    	return url;
    }
       
    /**
     * A method to check whether there's a logo associated with this account
     *  type.
     *  
     * @return True if there's a logo associated with this account
     */
    public boolean hasLogo() {
    		return (logoPath != "");
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
        boolean secret;

        /**
         * @param name the property's name
         * @param required whether the property is required
         */
        public Property(String name, boolean required) {
            super();
            this.name = name;
            this.required = required;
            this.secret = false;
        }
        
        /**
         * 
         * @param name the property's name
         * @param required whether the property is required
         * @param secret whether the property should be visible when entering 
         * 		it in a text-field, e.g. is a password 
         */
        public Property(String name, boolean required, boolean secret) {
            super();
            this.name = name;
            this.required = required;
            this.secret = secret;
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
            this.secret = p.secret;
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

        /** 
         * @return true if the property is supposed to be written in a protected 
         * 			text-field, e.g. if it's a password
         */
        public boolean isSecret() {
        	return secret;
        }
        
        /**
         * To set if the property shouldn't be clearly visibly in e.g. a 
         * text-field for some reason (e.g. if it's password).
         * 
         * @param secret secret status to be set
         */
        public void setSecret(boolean secret) {
        	this.secret = secret;
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
