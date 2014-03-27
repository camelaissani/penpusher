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
package org.penpusher.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.penpusher.server.logging.PPLogger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@XStreamAlias("penpusher")
public class WebApplication {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WebApplication.class.getName());

    private static final String CONFIG_FILE_PATH = "/etc/penpusher/penpusher.xml";

    private static WebApplication instance;

    private AuthenticationContext authenticationContext;
    private FolderContext folderContext;
    @XStreamImplicit(itemFieldName = "extension")
    private List<String> compatibleExtensions;

    public static synchronized WebApplication getInstance() {
        if (null == instance) {
            instance = create();
        }
        return instance;
    }

    private static WebApplication create() {
        PPLogger.info(LOGGER, "Instantiate WebApplication from file", CONFIG_FILE_PATH);
        WebApplication application = null;
        final XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.processAnnotations(WebApplication.class);
        BufferedInputStream xml = null;
        try {
            xml = new BufferedInputStream(new FileInputStream(CONFIG_FILE_PATH));
            application = (WebApplication) xstream.fromXML(xml);
            PPLogger.info(LOGGER, "WebApplication instantiation successful");
        } catch (final FileNotFoundException e) {
            PPLogger.error(LOGGER, e, "Can not instantiate WebApplication from xml");
        } finally {
            if (null != xml) {
                try {
                    xml.close();
                } catch (final IOException e) {
                }
            }
        }
        return application;
    }

    private WebApplication() {}

    public AuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public FolderContext getFolderContext() {
        return folderContext;
    }

    public List<String> getCompatibleExtensions() {
        return compatibleExtensions;
    }
}