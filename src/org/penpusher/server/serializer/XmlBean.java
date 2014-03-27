/*
 * Penpusher allows users to create corporate documents from templates.
 * Copyright (C) 2010  Camel AISSANI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.penpusher.server.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.penpusher.client.data.Category;
import org.penpusher.client.data.CategoryList;
import org.penpusher.client.data.Folder;
import org.penpusher.client.data.Form;
import org.penpusher.client.data.FormElement;
import org.penpusher.server.logging.PPLogger;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class XmlBean {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(XmlBean.class.getName());

    public static synchronized List<Category> loadCategories(final File file) {
        PPLogger.info(LOGGER, "Instantiate bean form XML file", file.getAbsolutePath());

        final XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("categoryList", CategoryList.class);
        xstream.alias("category", Category.class);
        xstream.alias("folder", Folder.class);
        List<Category> categories = null;
        BufferedInputStream xml = null;
        try {
            xml = new BufferedInputStream(new FileInputStream(file));
            final CategoryList categoryList = (CategoryList) xstream.fromXML(xml);
            if (categoryList == null) {
                PPLogger.error(LOGGER, "Can not instantiate Categories bean from xml");
            }
            else {
                categories = categoryList.getCategories();
                PPLogger.info(LOGGER, "Bean Categories instantiation successful.");
            }
        } catch (final FileNotFoundException e) {
            PPLogger.error(LOGGER, e, "Can not instantiate Categories bean from xml", file.getAbsolutePath());
        } finally {
            if (null != xml) {
                try {
                    xml.close();
                } catch (final IOException e) {
                }
            }
        }
        return categories;
    }

    public static synchronized Form loadXmlForm(final File file) {
        final String filePath = file.getAbsolutePath();
        PPLogger.info(LOGGER, "Begin of decoding bean form XML file", filePath);
        Form form = null;
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            // get a new instance of parser
            final SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            final FormHandler handler = new FormHandler();
            sp.parse(filePath, handler);
            form = handler.getForm();

        } catch (final SAXException se) {
            se.printStackTrace();
        } catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (final IOException ie) {
            ie.printStackTrace();
        }
        PPLogger.info(LOGGER, "End of decoding bean form XML file", filePath);
        return form;
    }

    public static synchronized Form loadForm(final File file) {
        PPLogger.info(LOGGER, "Instantiate bean form XML file", file.getAbsolutePath());
        final XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("form", Form.class);
        xstream.alias("formElement", FormElement.class);
        Form form = null;
        BufferedInputStream xml = null;
        try {
            xml = new BufferedInputStream(new FileInputStream(file));
            form = (Form) xstream.fromXML(xml);
            PPLogger.info(LOGGER, "Bean instantiation successful form name is", form.getName());
        } catch (final FileNotFoundException e) {
            PPLogger.error(LOGGER, e, "Can not instantiate bean from xml", file.getAbsolutePath());
        } finally {
            if (null != xml) {
                try {
                    xml.close();
                } catch (final IOException e) {
                }
            }
        }
        return form;
    }

    public static synchronized void saveForm(final Form form, final String filePath) {
        PPLogger.info(LOGGER, "Serialize bean", form.getName(), "to XML file");
        final XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("form", Form.class);
        xstream.alias("formElement", FormElement.class);
        BufferedWriter xml = null;
        try {
            xml = new BufferedWriter(new FileWriter(filePath));
            xstream.toXML(form, xml);
            PPLogger.info(LOGGER, "Bean Serialization successful", filePath);

        } catch (final IOException e) {
            PPLogger.error(LOGGER, e, "Can not create xml for", form.getName());
        } finally {
            if (null != xml) {
                try {
                    xml.close();
                } catch (final IOException e) {
                }
            }
        }
    }
}