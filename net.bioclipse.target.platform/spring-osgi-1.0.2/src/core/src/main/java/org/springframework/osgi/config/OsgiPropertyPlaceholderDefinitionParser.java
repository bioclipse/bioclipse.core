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
package org.springframework.osgi.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.osgi.compendium.internal.OsgiPropertyPlaceholder;
import org.springframework.osgi.config.ParserUtils.AttributeCallback;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * osgi:property-placeholder parser.
 * 
 * @author Costin Leau
 * 
 */
class OsgiPropertyPlaceholderDefinitionParser extends AbstractSingleBeanDefinitionParser {

	public static final String REF = "defaults-ref";

	public static final String PROPERTIES_FIELD = "properties";

	public static final String NESTED_PROPERTIES = "default-properties";

	protected Class getBeanClass(Element element) {
		return OsgiPropertyPlaceholder.class;
	}

	protected boolean shouldGenerateId() {
		return true;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// do standard parsing
		ParserUtils.parseCustomAttributes(element, builder, new AttributeCallback() {

			public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
				String name = attribute.getLocalName();
				String value = attribute.getValue();
				// transform ref into bean reference
				if (REF.equals(name)) {
					builder.addPropertyReference(PROPERTIES_FIELD, value);
					return false;
				}
				return true;
			}
		});

		// parse subelement (default-properties)
		Element nestedElement = DomUtils.getChildElementByTagName(element, NESTED_PROPERTIES);

		if (nestedElement != null) {
			if (element.hasAttribute(REF))
				parserContext.getReaderContext().error(
					"nested properties cannot be declared if '" + REF + "' attribute is specified", element);

			builder.addPropertyValue(PROPERTIES_FIELD, parserContext.getDelegate().parsePropsElement(nestedElement));
		}
	}
}
