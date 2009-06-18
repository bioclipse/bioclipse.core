/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.core.tests.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit tests for checking the copyright and license header. It assumes the
 * SVN structure where plug-ins are in <code>bioclipse2/plugins/</code> and
 * features are in <code>bioclipse2/features/</code>.
 * 
 * @author egonw
 */
public abstract class AbstractFeatureTest {
    
    /**
     * Method that must be overwritten by classes that extend this class.
     * 
     * @return the identifier of the feature
     */
    public String getFeatureName() {
        throw new RuntimeException(
            "The getFeatureName() method must be overwritten."
        );
    }

    /**
     * Returns a {@link File} pointing to the configuration file of the
     * feature.
     *
     * @return {@link File} of the <code>feature.xml</code>.
     */
    private File getFeatureXML() {
        File featureXML = new File(
            ".." + File.separator + ".." + File.separator +"features" +
            File.separator + getFeatureName() + File.separator + "feature.xml"
        );
        return featureXML;
    }
    
    /**
     * Returns a {@link File} pointing to the configuration file of the
     * plug-in.
     *
     * @param  plugin identifier of the plug-in
     * @return {@link File} of the <code>plugin.xml</code>.
     */
    private File getPluginXML(String plugin) {
        File pluginXML = new File(
            ".." + File.separator + ".." + File.separator +"plugins" +
            File.separator + plugin + File.separator + "plugin.xml"
        );
        return pluginXML;
    }
    
    /**
     * Tests if the <code>feature.xml</code> for the tested feature can be
     * found.
     */
    @Test public void searchFeatureXML() {
        Assert.assertNotNull(
            "The branding plugin is null", getFeatureName()
        );
        Assert.assertNotSame(
            "The branding plugin name is zero length", 0, getFeatureName()
        );
        File featureXML = getFeatureXML();
        Assert.assertTrue(
            "Could not find the feature.xml: " + getFeatureName(),
            featureXML.exists()
        );
    }

    /**
     * Returns a {@link List} of <code>.java</code> source files found in the
     * given folder. Recurses into subfolders.
     *
     * @param  folder {@link File} of the folder to look for source files.
     * @return        a {@link List} of source files.
     */
    private List<String> getSourceFiles(File folder) {
        List<String> sourceFiles = new ArrayList<String>();
        if (folder.isDirectory()) {
            for (File entry : folder.listFiles()) {
                if (entry.isDirectory()) {
                    sourceFiles.addAll(getSourceFiles(entry));
                } else if (entry.getName().endsWith(".java")){
                    sourceFiles.add(entry.getAbsolutePath());
                }
            }
        }
        return sourceFiles;
    }
    
    /**
     * Tests if the source files of the plug-ins in this feature all have
     * copyright notices in the headers.
     *
     * @throws IOException when a source file could not be processed.
     */
    @Test public void testCopyrightPresent() throws IOException {
        int errorCount = 0;
        for (String plugin : getBioclipsePlugins()) {
            File pluginFolder = new File(
                ".." + File.separator + ".." + File.separator +"plugins" +
                File.separator + plugin
            );
            Assert.assertTrue(
                "Cannot find plugin folder: " + pluginFolder.getAbsolutePath(),
                pluginFolder.exists()
            );
            List<String> sourceFiles = getSourceFiles(pluginFolder);
            for (String sourceFile : sourceFiles) {
                String header = getHeader(sourceFile);
                errorCount += assertCopyrightPresent(sourceFile, header);
            }
        }
        Assert.assertEquals(
            "Missing copyright statements found: " +
            errorCount,
            0, errorCount
        );
    }

    /**
     * Tests if the copyright notice does not have Bioclipse. The Bioclipse
     * project is not a legal entity and therefore not own copyright.
     *
     * @throws IOException if a source file cannot be processed.
     */
    @Ignore
    @Test public void testCopyrightBioclipse() throws IOException {
        int errorCount = 0;
        for (String plugin : getBioclipsePlugins()) {
            File pluginFolder = new File(
                ".." + File.separator + ".." + File.separator +"plugins" +
                File.separator + plugin
            );
            Assert.assertTrue(
                "Cannot find plugin folder: " + pluginFolder.getAbsolutePath(),
                pluginFolder.exists()
            );
            List<String> sourceFiles = getSourceFiles(pluginFolder);
            for (String sourceFile : sourceFiles) {
                String header = getHeader(sourceFile);
                errorCount += assertCopyrightAuthorEmail(sourceFile, header);
            }
        }
        Assert.assertEquals(
            "Several copyright statements use Bioclipse as legal entity: " +
            errorCount,
            0, errorCount
        );
    }

    /**
     * Returns the header of a source file. The header is here defined as all
     * lines preceding the line which defines the start of the class definition.
     * This line is recognized as the first line in which the class name is
     * mentioned, which is extracted from the source file absolute path
     * given as parameter.
     *
     * @param sourceFile Source file of which the header is returned.
     * @return header of the given source file.
     * @throws IOException if the source file could not be read.
     */
    private String getHeader( String sourceFile ) throws IOException {
        String className = sourceFile.substring(
            sourceFile.lastIndexOf(File.separator)+1,
            sourceFile.lastIndexOf('.')
        );
        StringBuffer header = new StringBuffer();
        BufferedReader reader = new BufferedReader(
            new FileReader(new File(sourceFile))
        );
        String line = reader.readLine();
        while (line != null && !line.contains(className)) {
            header.append(line);
            line = reader.readLine();
        }
        reader.close();
        return header.toString();
    }

    /**
     * Method that tests of the copyright line refers to mr. Bioclipse.
     *
     * @param sourceFile source file to check.
     * @param header     header of the source file.
     * @return           number of violations.
     */
    private int assertCopyrightAuthorEmail(String sourceFile, String header) {
        for (String line : header.split("\n")) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains("copyright")) {
                if (lowerLine.contains("bioclipse")) {
                    System.out.println("Source file contains old copyright " +
                    		"statement: " + sourceFile);
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Method that tests if the copyright line is present.
     *
     * @param sourceFile source file to check.
     * @param header     header of the source file.
     * @return           number of violations.
     */
    private int assertCopyrightPresent(String sourceFile, String header) {
        boolean hasCopyright = false;
        for (String line : header.split("\n")) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains("copyright")) {
                hasCopyright = true;
            }
        }
        if (!hasCopyright) {
            System.out.println("Source file does not have a copyright " +
               "statement: " + sourceFile);
            return 1;
        }
        return 0;
    }

    /**
     * Returns a {@link List} of Bioclipse plugins.
     *
     * @return a list of identifiers of bioclipse plugins.
     */
    private List<String> getBioclipsePlugins() {
        List<String> bioclipsePlugins = new ArrayList<String>();
        List<String> plugins = getPlugins();
        for (String plugin : plugins) {
            File pluginXML = getPluginXML(plugin);
            if (pluginXML.exists()) {
                bioclipsePlugins.add(plugin);
            }
        }
        return bioclipsePlugins;
    }

    /**
     * Returns a {@link List} of plugins defined as being part of the
     * tested feature.
     *
     * @return a list of identifiers of feature plugins.
     */
    public List<String> getPlugins() {
        List<String> plugins = new ArrayList<String>();
        try {
            Builder parser = new Builder();
            Document doc = parser.build(new FileInputStream(getFeatureXML()));
            Element root = doc.getRootElement();
            Elements pluginElems = root.getChildElements("plugin");
            for (int i=0; i<pluginElems.size(); i++) {
                Element plugin = pluginElems.get(i);
                plugins.add(plugin.getAttributeValue("id"));
            }
        } catch (ParsingException ex) {
            System.err.println("Cafe con Leche is malformed today. How embarrassing!");
        } catch (IOException ex) {
            System.err.println("Could not connect to Cafe con Leche. The site may be down.");
        }
        return plugins;
    }
    
}
