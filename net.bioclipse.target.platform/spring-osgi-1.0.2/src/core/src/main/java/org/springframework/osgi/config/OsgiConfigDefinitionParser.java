package org.springframework.osgi.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.osgi.compendium.internal.OsgiConfig;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Osgi namespace config tag parser.
 * 
 * @author Hal Hildebrand Date: Nov 2, 2006 Time: 8:06:32 AM
 */
class OsgiConfigDefinitionParser extends AbstractSingleBeanDefinitionParser {

	public static final String PERSISTENT_ID = "persistent-id";

	public static final String CONFIG_LISTENER = "config-listener";

	public static final String FACTORY = "isFactory";

	public static final String REF = "ref";

	public static final String UPDATE_METHOD = "update-method";

	public static final String DELETED_METHOD = "deleted-method";

	public static final String PERSISTENT_ID_FIELD = "pid";

	public static final String LISTENERS_FIELD = "listeners";

	public static final String FACTORY_FIELD = "factory";

	protected Class getBeanClass(Element element) {
		return OsgiConfig.class;
	}

	protected boolean shouldGenerateId() {
		return true;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		ParserUtils.parseCustomAttributes(element, builder, new ParserUtils.AttributeCallback() {
			public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
				String name = attribute.getLocalName();
				String value = attribute.getValue();

				if (PERSISTENT_ID.equals(name)) {
					builder.addPropertyValue(PERSISTENT_ID_FIELD, value);
					return false;
				}
				else if (FACTORY.equals(name)) {
					builder.addPropertyValue(FACTORY_FIELD, value);
					return false;
				}
				return true;
			}
		});

		List configListeners = new ArrayList();
		List nestedElements = DomUtils.getChildElementsByTagName(element, CONFIG_LISTENER);

		for (Iterator listeners = nestedElements.iterator(); listeners.hasNext();) {
			Element listener = (Element) listeners.next();

			if (!listener.hasAttribute(REF)) {
				parserContext.getReaderContext().error(REF + "' attribute is not specified", element);
			}

			if (!listener.hasAttribute(UPDATE_METHOD)) {
				parserContext.getReaderContext().error(UPDATE_METHOD + "' attribute is not specified", element);
			}

			OsgiConfig.ConfigListener l = new OsgiConfig.ConfigListener();
			l.setReference(listener.getAttribute(REF));
			l.setUpdateMethod(listener.getAttribute(UPDATE_METHOD));

			if (listener.hasAttribute(DELETED_METHOD)) {
				l.setDeletedMethod(listener.getAttribute(DELETED_METHOD));
			}
			configListeners.add(l);
		}
		builder.addPropertyValue(LISTENERS_FIELD, configListeners);
	}
}
