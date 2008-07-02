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
package org.springframework.osgi;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

/**
 * Util classes for test cases.
 * 
 * @author Costin Leau
 * 
 */
public abstract class TestUtils {

	public static Object getFieldValue(final Object object, final String fieldName) {
		final Object[] fld = new Object[1];
		ReflectionUtils.doWithFields(object.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				field.setAccessible(true);
				fld[0] = field.get(object);
			}

		}, new FieldFilter() {

			public boolean matches(Field field) {
				return fld[0] == null && fieldName.equals(field.getName());
			}

		});

		return fld[0];
	}
}
