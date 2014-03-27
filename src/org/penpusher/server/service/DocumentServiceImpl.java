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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.data.Folder;
import org.penpusher.client.data.Form;
import org.penpusher.client.service.DocumentService;
import org.penpusher.server.FolderContext;
import org.penpusher.server.WebAppException;
import org.penpusher.server.WebApplication;
import org.penpusher.server.engine.OfficeDocument;
import org.penpusher.server.engine.OfficeEngine;
import org.penpusher.server.engine.OfficeEngineFactory;
import org.penpusher.server.logging.PPLogger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class DocumentServiceImpl extends RemoteServiceServlet implements DocumentService {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(DocumentServiceImpl.class.getName());

    @Override
    public String addDocument(final String filename, final String description, final Form form, final Folder folder) {
        if (filename == null || filename.trim().length() == 0) {
            PPLogger.error(LOGGER, "Impossible to generate document file name is not set");
            throw new IllegalArgumentException("filename must be set");
        }
        if (form == null) {
            PPLogger.error(LOGGER, "Impossible to generate document form is null");
            throw new IllegalArgumentException("form must be set");
        }
        if (folder == null) {
            PPLogger.error(LOGGER, "Impossible to generate document folder is null");
            throw new IllegalArgumentException("folder must be set");
        }

        try {
            final WebApplication app = WebApplication.getInstance();
            final FolderContext context = app.getFolderContext();

            // Template name and folder
            final String templateFilePath = FileUtils.buildPath(context.getTemplatesFolder(), form.getTemplate());

            // Document name and folder
            final String exportFilePath =
                    FileUtils.buildFilePath(FileUtils.buildPath(context.getDocumentsFolder(), folder.getPath()),
                            filename, FileUtils.getExtension(form.getTemplate()));

            PPLogger.info(LOGGER, "Generating document with form:", form.getName());

            final OfficeEngine engine = OfficeEngineFactory.createOpenDocumentEngine();

            // Open document
            PPLogger.info(LOGGER, "Opening template document:", templateFilePath);

            final OfficeDocument document = engine.openDocument(templateFilePath, true);
            if (null == document) {
                PPLogger.error(LOGGER, "Fail to open template document");
                throw new WebAppException("Fail to open template document");
            }
            // Replace keys by values
            PPLogger.info(LOGGER, "Replacing document keys by form values");

            document.replace(form.getAllFormElement());

            // Save the generated document
            PPLogger.info(LOGGER, "Saving document:", exportFilePath);

            engine.saveDocument(document, exportFilePath);

            // Close document
            PPLogger.info(LOGGER, "Closing document:", exportFilePath);

            engine.closeDocument(document);

            PPLogger.info(LOGGER, "Document generated with form:", form.getName());
        } catch (final Exception e) {
            PPLogger.error(LOGGER, e, "Impossible to generate document from form", form.getName());
        }
        return FileUtils.buildFilePath(folder.getPath(), filename, FileUtils.getExtension(form.getTemplate()));
    }

    @Override
    public void removeDocument(final String filename, final Folder folder) {
        if (filename == null || filename.trim().length() == 0) {
            PPLogger.error(LOGGER, "Impossible to remove document file name is not set");
            throw new IllegalArgumentException("filename must be set");
        }
        if (folder == null) {
            PPLogger.error(LOGGER, "Impossible to remove document folder is null");
            throw new IllegalArgumentException("folder must be set");
        }

        try {
            final WebApplication app = WebApplication.getInstance();
            final FolderContext context = app.getFolderContext();

            // Document name and folder
            final StringBuilder filePath =
                    new StringBuilder(FileUtils.buildPath(context.getDocumentsFolder(), folder.getPath()));
            filePath.append(File.separator).append(filename);

            final File documentToRemove = new File(filePath.toString());
            if (documentToRemove.delete()) {
                PPLogger.info(LOGGER, "Document removed:", documentToRemove.toString());
            }
            else {
                PPLogger.error(LOGGER, "Impossible to remove document", documentToRemove.toString());
            }
        } catch (final Exception e) {
            PPLogger.error(LOGGER, e, "Impossible to remove document", filename);
        }
    }

    @Override
    public List<DocumentDescriptor> listDocuments(final Folder folder) {
        if (folder == null) {
            PPLogger.error(LOGGER, "Impossible to get documents folder is null");
            throw new IllegalArgumentException("Folder can not be null.");
        }

        final String path = folder.getPath();
        if (path == null || path.isEmpty()) {
            PPLogger.error(LOGGER, "Impossible to get documents folder path is missing", folder.getName());
            throw new IllegalStateException("Folder path information is missing " + folder.getName());
        }

        final WebApplication app = WebApplication.getInstance();
        final FolderContext context = app.getFolderContext();
        final String documentsPath = FileUtils.buildPath(context.getDocumentsFolder(), path);

        PPLogger.info(LOGGER, "Load documents from", documentsPath);
        final List<DocumentDescriptor> documents = new ArrayList<DocumentDescriptor>();
        final File folderPath = new File(documentsPath);
        if (folderPath.exists()) {
            final File files[] = folderPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    final List<String> extensions = app.getCompatibleExtensions();
                    for (final String extension : extensions) {
                        if (name.toLowerCase().endsWith(extension)) {
                            PPLogger.info(LOGGER, "Found document", name);
                            return true;
                        }
                    }
                    return false;
                }
            });

            for (final File file : files) {
                PPLogger.info(LOGGER, "Return document", file.getName());

                final DocumentDescriptor document = new DocumentDescriptor();
                document.setName(file.getName());
                // document.setOwner(owner);
                document.setCreationDate(new Date(file.lastModified()));
                document.setDescription("The description");
                // document.setForm(form);
                documents.add(document);
            }
            Collections.sort(documents, new Comparator<DocumentDescriptor>() {

                @Override
                public int compare(final DocumentDescriptor o1, final DocumentDescriptor o2) {
                    return o2.getCreationDate().compareTo(o1.getCreationDate());
                }

            });
        }
        PPLogger.info(LOGGER, String.valueOf(documents.size()), "document(s) found.");

        return documents;
    }
}