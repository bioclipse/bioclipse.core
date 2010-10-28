/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.xml.business;

import java.util.List;

import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.api.managers.PublishedClass;
import net.bioclipse.core.api.managers.PublishedMethod;
import net.bioclipse.core.api.managers.TestClasses;
import net.bioclipse.core.api.managers.TestMethods;

@PublishedClass(
    value="Manager to analyze and validate XML documents."
)
@TestClasses("net.bioclipse.xml.test.AllXmlManagerTests," +
		     "net.bioclipse.xml.test.AbstractXmlManagerPluginTest")
public interface IXmlManager extends IBioclipseManager {

    @PublishedMethod(
        methodSummary="Checks if the XML document is well-formed.",
        params="String filename"
    )
    @TestMethods("testIsWellFormed")
    public boolean isWellFormed(String filename);

    @PublishedMethod(
        methodSummary="Validates the XML document against the " +
        "schemas defined in the document itself.",
        params="String filename"
    )
    @TestMethods("testValidate")
    public List<XMLError> validate(String filename);

    @PublishedMethod(
        methodSummary="Checks if the XML document is valid against the " +
        "schemas defined in the document itself.",
        params="String filename"
    )
    @TestMethods("testIsValid")
    public boolean isValid(String filename);

    @PublishedMethod(
        methodSummary="Lists all namespaces used in the XML document.",
        params="String filename"
    )
    @TestMethods("testListNamespaces")
    public List<String> listNamespaces(String filename);

    @PublishedMethod(
        methodSummary="Validates the XML document against the given " +
        	"Schematron 1.5 document.",
        params="String filename, String schematronFilename"
    )
    @TestMethods("testValidateAgainstSchematron")
    public List<XMLError> validateAgainstSchematron(
        String filename,
        String schematronFilename
    );

    @PublishedMethod(
        methodSummary="Validates the XML document against the given " +
        "RelaxNG document.",
        params="String filename, String relaxngFilename"
    )
    @TestMethods("testValidateAgainstRelaxNG")
    public List<XMLError> validateAgainstRelaxNG(
        String filename,
        String relaxngFilename
    );

    @PublishedMethod(
        methodSummary="Validates the XML document against the given " +
        "XML Schema document.",
        params="String filename, String xmlSchemaFilename"
    )
    @TestMethods("testValidateAgainstXMLSchema")
    public List<XMLError> validateAgainstXMLSchema(
        String filename,
        String xmlSchemaFilename
    );

}
