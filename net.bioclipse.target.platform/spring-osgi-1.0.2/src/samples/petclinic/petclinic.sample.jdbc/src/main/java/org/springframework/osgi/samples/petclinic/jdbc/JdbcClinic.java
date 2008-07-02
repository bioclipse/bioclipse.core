/*
 * Copyright 2006-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.osgi.samples.petclinic.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.osgi.samples.petclinic.Clinic;
import org.springframework.osgi.samples.petclinic.Pet;
import org.springframework.osgi.samples.petclinic.PetType;

/**
 * @author Costin Leau
 * 
 */
public class JdbcClinic extends JdbcDaoSupport implements Clinic {

	private MappingSqlQuery petTypesQuery;

	private MappingSqlQuery petQuery;

	public void init() throws Exception {
		petTypesQuery = new PetTypesQuery(getDataSource());
		petQuery = new PetsQuery(getDataSource());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.osgi.samples.petclinic.Clinic#getDatabaseName()
	 */
	public String getDatabaseName() throws DataAccessException {
		return (String) getJdbcTemplate().execute(new ConnectionCallback() {

			public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
				return connection.getMetaData().getDatabaseProductName();
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.osgi.samples.petclinic.Clinic#getPetTypes()
	 */
	public Collection getPetTypes() throws DataAccessException {
		//return this.petTypesQuery.execute();
		return Arrays.asList(new String[] { "not implemented yet" });
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.osgi.samples.petclinic.Clinic#getVets()
	 */
	public Collection getPets() throws DataAccessException {
		return this.petQuery.execute();
	}

	protected class PetTypesQuery extends MappingSqlQuery {

		/**
		 * Create a new instance of PetTypesQuery.
		 * @param ds the DataSource to use for the query
		 */
		protected PetTypesQuery(DataSource ds) {
			super(ds, "SELECT id,name FROM types ORDER BY name");
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			PetType type = new PetType();
			type.setId(new Integer(rs.getInt("id")));
			type.setName(rs.getString("name"));
			return type.toString();
		}
	}

	/**
	 * Abstract base class for all <code>Pet</code> Query Objects.
	 */
	protected class PetsQuery extends MappingSqlQuery {

		/**
		 * Create a new instance of PetsQuery.
		 * @param ds the DataSource to use for the query
		 * @param sql SQL string to use for the query
		 */
		protected PetsQuery(DataSource ds) {
			super(ds, "SELECT id,name,birth_date FROM pets ORDER BY name");
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Pet pet = new Pet();
			pet.setId(new Integer(rs.getInt("id")));
			pet.setName(rs.getString("name"));
			pet.setBirthDate(rs.getDate("birth_date"));
			return pet.toString();
		}
	}

}
