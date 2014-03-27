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
package org.penpusher.server.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.penpusher.client.data.Category;
import org.penpusher.client.data.Form;
import org.penpusher.client.service.FormService;
import org.penpusher.server.WebApplication;
import org.penpusher.server.logging.PPLogger;
import org.penpusher.server.serializer.XmlBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class FormServiceImpl extends RemoteServiceServlet implements FormService {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(FormServiceImpl.class.getName());

    @Override
    public List<Form> loadForms() {
        final WebApplication app = WebApplication.getInstance();
        final String path = app.getFolderContext().getTemplatesFolder();

        PPLogger.info(LOGGER, "Scanning path", path);
        final File currentDir = new File(path);
        int count = 0;
        final List<Form> forms = new ArrayList<Form>();
        if (currentDir.exists()) {
            final File files[] = currentDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return (name.endsWith(".xml") && !("categories.xml".equals(name)));
                }
            });

            for (final File file : files) {
                PPLogger.info(LOGGER, "Computing file:", file.getName());
                final Form form = XmlBean.loadXmlForm(file);
                if (null != form) {
                    forms.add(form);
                    count++;
                }
            }
        }
        else {
            PPLogger.warn(LOGGER, path, "does not exist.");
        }

        PPLogger.info(LOGGER, String.valueOf(count), "template files loaded.");

        return forms;
    }

    @Override
    public List<Category> loadCategories() {

        final WebApplication app = WebApplication.getInstance();
        final String templatePath = app.getFolderContext().getTemplatesFolder();

        PPLogger.info(LOGGER, "Loading file categories.xml");
        final String path = FileUtils.buildPath(templatePath, "categories.xml");
        final File file = new File(path.toString());
        List<Category> categories = XmlBean.loadCategories(file);
        if (categories == null) {
            PPLogger.error(LOGGER, "Categories can not be initialized");
            categories = Collections.emptyList();
        }

        PPLogger.info(LOGGER, String.valueOf(categories.size()), "categories loaded.");

        return categories;
    }
}