package org.springframework.osgi.samples.petclinic;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for
 * objects needing this property.
 * 
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
public class BaseEntity implements Serializable {

	private Integer id;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public boolean isNew() {
		return (this.id == null);
	}
}
