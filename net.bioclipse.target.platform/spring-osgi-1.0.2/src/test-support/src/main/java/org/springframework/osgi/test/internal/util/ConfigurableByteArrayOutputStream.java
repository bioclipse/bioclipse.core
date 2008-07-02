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
package org.springframework.osgi.test.internal.util;

import java.io.ByteArrayOutputStream;

/**
 * Simple class which allows a specific buffer to be used as the underlying
 * implementation. This makes it easy to manipulate directly the storage support
 * (such as resizing the byte array or reading from it).
 * 
 * @author Costin Leau
 * 
 */
public class ConfigurableByteArrayOutputStream extends ByteArrayOutputStream {

	public ConfigurableByteArrayOutputStream() {
		super();
	}

	public ConfigurableByteArrayOutputStream(int size) {
		super(size);
	}

	/**
	 * Extension added to the original class.
	 * 
	 * @param bufferToUse
	 */
	public ConfigurableByteArrayOutputStream(byte[] bufferToUse) {
		this.buf = bufferToUse;
	}
}
