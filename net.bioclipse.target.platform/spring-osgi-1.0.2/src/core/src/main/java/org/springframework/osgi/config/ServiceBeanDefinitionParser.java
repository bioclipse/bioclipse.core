/*
 * Copyright 2002-2005 the original author or authors.
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

package org.springframework.osgi.config;

import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.osgi.config.ParserUtils.AttributeCallback;
import org.springframework.osgi.service.exporter.support.OsgiServiceFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for service element found in the osgi namespace.
 * 
 * @author Costin Leau
 * @author Hal Hildebrand
 * @author Andy Piper
 */
class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	// bean properties
	private static final String TARGET_BEAN_NAME_PROP = "targetBeanName";

	private static final String TARGET_PROP = "target";

	private static final String LISTENERS_PROP = "listeners";

	private static final String INTERFACES_PROP = "interfaces";

	private static final String AUTOEXPORT_PROP = "autoExport";

	private static final String CCL_PROP = "contextClassLoader";

	// XML elements
	private static final String INTERFACES_ID = "interfaces";

	private static final String INTERFACE = "interface";

	private static final String PROPS_ID = "service-properties";

	private static final String LISTENER = "registration-listener";

	private static final String REF = "ref";

	private static final String AUTOEXPORT = "auto-export";

	private static final String CONTEXT_CLASSLOADER = "context-class-loader";


	protected Class getBeanClass(Element element) {
		return OsgiServiceFactoryBean.class;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// parse attributes
		ParserUtils.parseCustomAttributes(element, builder, new AttributeCallback() {

			public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder bldr) {
				String name = attribute.getLocalName();

				if (INTERFACE.equals(name)) {
					bldr.addPropertyValue(INTERFACES_PROP, attribute.getValue());
					return false;
				}
				else if (REF.equals(name)) {
					return false;
				}

				else if (AUTOEXPORT.equals(name)) {
					// convert constant to upper case to let Spring do the
					// conversion
					String label = attribute.getValue().toUpperCase().replace('-', '_');
					bldr.addPropertyValue(AUTOEXPORT_PROP, label);
					return false;
				}

				else if (CONTEXT_CLASSLOADER.equals(name)) {
					// convert constant to upper case to let Spring do the
					// conversion

					String value = attribute.getValue().toUpperCase().replace('-', '_');
					bldr.addPropertyValue(CCL_PROP, value);
					return false;
				}

				return true;
			}
		});

		// determine nested/referred beans
		Object target = null;
		if (element.hasAttribute(REF))
			target = new RuntimeBeanReference(element.getAttribute(REF));

		// element is considered parent
		NodeList nl = element.getChildNodes();

		ManagedList listeners = new ManagedList();

		// parse all sub elements
		// we iterate through them since we have to 'catch' the possible nested
		// bean which has an unknown name local name

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element subElement = (Element) node;
				String name = subElement.getLocalName();

				// osgi:interface
				if (parseInterfaces(element, subElement, parserContext, builder))
					;
				// osgi:service-properties
				else if (parseServiceProperties(element, subElement, parserContext, builder))
					;
				// osgi:registration-listener
				else if (LISTENER.equals(name)) {
					listeners.add(parseListener(parserContext, subElement, builder));
				}

				// nested bean reference/declaration
				else
					target = parseBeanReference(element, subElement, parserContext, builder);
			}
		}

		// if we have a named bean use target_bean_name
		if (target instanceof RuntimeBeanReference) {
			builder.addPropertyValue(TARGET_BEAN_NAME_PROP, ((RuntimeBeanReference) target).getBeanName());
		}
		else {
			// add target (can be either an object instance or a bean
			// definition)
			builder.addPropertyValue(TARGET_PROP, target);
		}

		// add listeners
		builder.addPropertyValue(LISTENERS_PROP, listeners);
	}

	// osgi:interfaces
	private boolean parseInterfaces(Element parent, Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String name = element.getLocalName();

		// osgi:interfaces
		if (INTERFACES_ID.equals(name)) {
			// check shortcut on the parent
			if (parent.hasAttribute(INTERFACE)) {
				parserContext.getReaderContext().error(
					"either 'interface' attribute or <intefaces> sub-element has be specified", parent);
			}
			Set interfaces = parserContext.getDelegate().parseSetElement(element, builder.getBeanDefinition());
			builder.addPropertyValue(INTERFACES_PROP, interfaces);
			return true;
		}

		return false;
	}

	// osgi:service-properties
	private boolean parseServiceProperties(Element parent, Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		String name = element.getLocalName();

		if (PROPS_ID.equals(name)) {
			if (DomUtils.getChildElementsByTagName(element, BeanDefinitionParserDelegate.ENTRY_ELEMENT).size() > 0) {
				Object props = parserContext.getDelegate().parseMapElement(element, builder.getRawBeanDefinition());
				builder.addPropertyValue(Conventions.attributeNameToPropertyName(PROPS_ID), props);
			}
			else {
				parserContext.getReaderContext().error("Invalid service property type", element);
			}
			return true;
		}
		return false;
	}

	// parse nested bean definition
	private Object parseBeanReference(Element parent, Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		// check shortcut on the parent
		if (parent.hasAttribute(REF))
			parserContext.getReaderContext().error(
				"nested bean definition/reference cannot be used when attribute 'ref' is specified", parent);
		return parserContext.getDelegate().parsePropertySubElement(element, builder.getBeanDefinition());
	}

	// osgi:listener
	private BeanDefinition parseListener(ParserContext context, Element element, BeanDefinitionBuilder builder) {

		// filter elements
		NodeList nl = element.getChildNodes();

		// wrapped object
		Object target = null;
		// target bean name (used for cycles)
		String targetName = null;

		// discover if we have listener with ref and nested bean declaration
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element nestedDefinition = (Element) node;
				// check shortcut on the parent
				if (element.hasAttribute(REF))
					context.getReaderContext().error(
						"nested bean declaration is not allowed if 'ref' attribute has been specified",
						nestedDefinition);

				target = context.getDelegate().parsePropertySubElement(nestedDefinition, builder.getBeanDefinition());
				// if this is a bean reference (nested <ref>), extract the name
				if (target instanceof RuntimeBeanReference) {
					targetName = ((RuntimeBeanReference) target).getBeanName();
				}
			}
		}

		// extract registration/unregistration attributes from
		// <osgi:registration-listener>
		MutablePropertyValues vals = new MutablePropertyValues();

		NamedNodeMap attrs = element.getAttributes();
		for (int x = 0; x < attrs.getLength(); x++) {
			Attr attribute = (Attr) attrs.item(x);
			String name = attribute.getLocalName();

			if (REF.equals(name))
				targetName = attribute.getValue();
			else
				vals.addPropertyValue(Conventions.attributeNameToPropertyName(name), attribute.getValue());
		}

		// create serviceListener wrapper
		RootBeanDefinition wrapperDef = new RootBeanDefinition(OsgiServiceRegistrationListenerAdapter.class);

		// set the target name (if we have one)
		if (targetName != null)
			vals.addPropertyValue(TARGET_BEAN_NAME_PROP, targetName);
		// else set the actual target
		else
			vals.addPropertyValue(TARGET_PROP, target);

		wrapperDef.setPropertyValues(vals);

		return wrapperDef;

	}

	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}
}