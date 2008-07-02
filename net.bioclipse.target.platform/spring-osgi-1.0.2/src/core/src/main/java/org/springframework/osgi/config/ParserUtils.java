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
 *
 */
package org.springframework.osgi.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Parsing utility class.
 * 
 * @author Andy Piper
 * @author Costin Leau
 */
abstract class ParserUtils {

	/**
	 * Standard attribute callback. Deals with ID, DEPENDS-ON and LAZY-INIT
	 * attribute.
	 * 
	 * @author Costin Leau
	 * 
	 */
	static class StandardAttributeCallback implements AttributeCallback {
		public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
			String name = attribute.getLocalName();

			if (BeanDefinitionParserDelegate.ID_ATTRIBUTE.equals(name)) {
				return false;
			}

			if (BeanDefinitionParserDelegate.DEPENDS_ON_ATTRIBUTE.equals(name)) {
				builder.getBeanDefinition().setDependsOn(
					(StringUtils.tokenizeToStringArray(attribute.getValue(),
						BeanDefinitionParserDelegate.BEAN_NAME_DELIMITERS)));
				return false;
			}
			if (BeanDefinitionParserDelegate.LAZY_INIT_ATTRIBUTE.equals(name)) {
				builder.setLazyInit(Boolean.getBoolean(attribute.getValue()));
				return false;
			}
			return true;
		}
	}

	/**
	 * Convention callback that transforms "&lt;property-name&gt;-ref"
	 * attributes into a bean definition that sets the give
	 * &lt;property-name&gt; to a bean reference pointing to the attribute
	 * value.
	 * 
	 * <p/> Thus attribute "comparator-ref='bla'" will have property
	 * 'comparator' pointing to bean named 'bla'.
	 * 
	 * @see BeanDefinitionBuilder#addPropertyReference(String, String)
	 * 
	 * @author Costin Leau
	 * 
	 */
	static class PropertyRefAttributeCallback implements AttributeCallback {
		private static final String PROPERTY_REF = "-ref";

		public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
			String name = attribute.getLocalName();
			if (name.endsWith(PROPERTY_REF)) {
				String propertyName = name.substring(0, name.length() - PROPERTY_REF.length());
				builder.addPropertyReference(propertyName, attribute.getValue());
				return false;
			}
			return true;
		}
	}

	/**
	 * Callback relying on 'Spring' conventions. Normally this is the last
	 * callback in the stack trying to convert the property name and then
	 * setting it on the builder.
	 * 
	 * @see Conventions#attributeNameToPropertyName(String)
	 * @see BeanDefinitionBuilder#addPropertyValue(String, Object)
	 * 
	 * @author Costin Leau
	 */
	static class ConventionCallback implements AttributeCallback {
		public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
			String name = attribute.getLocalName();
			String propertyName = Conventions.attributeNameToPropertyName(name);
			builder.addPropertyValue(propertyName, attribute.getValue());
			return true;
		}
	}

	private static final AttributeCallback STANDARD_ATTRS_CALLBACK = new StandardAttributeCallback();

	private static final AttributeCallback PROPERTY_REF_ATTRS_CALLBACK = new PropertyRefAttributeCallback();

	private static final AttributeCallback PROPERTY_CONV_ATTRS_CALLBACK = new ConventionCallback();

	private static final String OSGI_NS = "http://www.springframework.org/schema/osgi";

	private static final String DEFAULT_TIMEOUT = "default-timeout";

	private static final String DEFAULT_CARDINALITY = "default-cardinality";

	/**
	 * Wrapper callback used for parsing attributes (one at a time) that have
	 * are non standard (ID, LAZY-INIT, DEPENDS-ON).
	 * 
	 * @author Costin Leau
	 * 
	 */
	static interface AttributeCallback {

		/**
		 * Process the given attribute using the contextual element and bean
		 * builder. Normally, the callback will interact with the bean
		 * definition and set some properties. <p/> If the callback has
		 * intercepted an attribute, it can stop the invocation of the rest of
		 * the callbacks on the stack by returning false.
		 * 
		 * @param parent parent element
		 * @param attribute current intercepted attribute
		 * @param builder builder holding the current bean definition
		 * @return true if the rest of the callbacks should be called or false
		 * otherwise.
		 */
		boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder);
	}

	/**
	 * Generic attribute callback. Will parse the given callback array, w/o any
	 * standard callback.
	 * 
	 * @param element XML element
	 * @param builder current bean definition builder
	 * @param callbacks array of callbacks (can be null/empty)
	 */
	public static void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks) {
		NamedNodeMap attributes = element.getAttributes();

		for (int x = 0; x < attributes.getLength(); x++) {
			Attr attr = (Attr) attributes.item(x);

			boolean shouldContinue = true;
			if (!ObjectUtils.isEmpty(callbacks))
				for (int i = 0; i < callbacks.length && shouldContinue; i++) {
					AttributeCallback callback = callbacks[i];
					shouldContinue = callback.process(element, attr, builder);
				}
		}
	}

	/**
	 * Dedicated parsing method that uses the following stack:
	 * <ol>
	 * <li>user given {@link AttributeCallback}s</li>
	 * <li>{@link StandardAttributeCallback}</li>
	 * <li>{@link PropertyRefAttributeCallback}</li>
	 * <li>{@link ConventionCallback}</li>
	 * </ol>
	 * 
	 * 
	 * @param element XML element
	 * @param builder current bean definition builder
	 * @param callbacks array of callbacks (can be null/empty)
	 */
	public static void parseCustomAttributes(Element element, BeanDefinitionBuilder builder,
			AttributeCallback[] callbacks) {
		List list = new ArrayList(8);

		if (!ObjectUtils.isEmpty(callbacks))
			CollectionUtils.mergeArrayIntoCollection(callbacks, list);
		// add standard callback
		list.add(STANDARD_ATTRS_CALLBACK);
		// add property ref
		list.add(PROPERTY_REF_ATTRS_CALLBACK);
		// add convention
		list.add(PROPERTY_CONV_ATTRS_CALLBACK);

		AttributeCallback[] cbacks = (AttributeCallback[]) list.toArray(new AttributeCallback[list.size()]);
		parseAttributes(element, builder, cbacks);
	}

	/**
	 * Derivative for
	 * {@link #parseCustomAttributes(Element, BeanDefinitionBuilder, org.springframework.osgi.internal.config.ParserUtils.AttributeCallback[])}
	 * accepting only one {@link AttributeCallback}.
	 * 
	 * @param element XML element
	 * @param builder current bean definition builder
	 * @param callback attribute callback, can be null
	 */
	public static void parseCustomAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback callback) {
		AttributeCallback[] callbacks = (callback == null ? new AttributeCallback[0]
				: new AttributeCallback[] { callback });
		parseCustomAttributes(element, builder, callbacks);
	}

	/**
	 * Initialize OSGi defaults.
	 * 
	 * @param document XML document
	 * @return initialized {@link OsgiDefaultsDefinition} instance
	 */
	public static OsgiDefaultsDefinition initOsgiDefaults(Document document) {
		Assert.notNull(document);
		return initOsgiDefaults(document.getDocumentElement());
	}

	/**
	 * Initialize OSGi defaults.
	 * 
	 * @param root root document element
	 * @return initialized {@link OsgiDefaultsDefinition} instance
	 */
	public static OsgiDefaultsDefinition initOsgiDefaults(Element root) {
		Assert.notNull(root);

		OsgiDefaultsDefinition defaults = new OsgiDefaultsDefinition();
		String timeout = root.getAttributeNS(OSGI_NS, DEFAULT_TIMEOUT);

		if (StringUtils.hasText(timeout))
			defaults.setTimeout(timeout);

		String cardinality = root.getAttributeNS(OSGI_NS, DEFAULT_CARDINALITY);

		if (StringUtils.hasText(cardinality))
			defaults.setCardinality(cardinality);

		return defaults;
	}

	public static AttributeCallback[] mergeCallbacks(AttributeCallback[] callbacksA, AttributeCallback[] callbacksB) {
		if (ObjectUtils.isEmpty(callbacksA))
			if (ObjectUtils.isEmpty(callbacksB))
				return new AttributeCallback[0];
			else
				return callbacksB;
		if (ObjectUtils.isEmpty(callbacksB))
			return callbacksA;

		AttributeCallback[] newCallbacks = new AttributeCallback[callbacksA.length + callbacksB.length];
		System.arraycopy(callbacksA, 0, newCallbacks, 0, callbacksA.length);
		System.arraycopy(callbacksB, 0, newCallbacks, callbacksA.length, callbacksB.length);
		return newCallbacks;
	}

}
