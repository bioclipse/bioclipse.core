package org.springframework.osgi.samples.petclinic;

import java.util.Date;

/**
 * Simple JavaBean business object representing a pet.
 *
 * @author  Ken Krebs
 * @author Juergen Hoeller
 */
public class Pet extends NamedEntity {

	private Date birthDate;

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}
}
