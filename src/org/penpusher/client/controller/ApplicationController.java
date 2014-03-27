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
package org.penpusher.client.controller;

import java.util.List;

import org.penpusher.client.data.Category;
import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.data.Folder;
import org.penpusher.client.data.Form;
import org.penpusher.client.data.PPSession;
import org.penpusher.client.event.CategoryChangedEvent;
import org.penpusher.client.event.DocumentChangedEvent;
import org.penpusher.client.event.FormChangedEvent;
import org.penpusher.client.event.GenerateDocumentCompletedEvent;
import org.penpusher.client.event.RemoveDocumentCompletedEvent;
import org.penpusher.client.event.SessionEvent;
import org.penpusher.client.event.SignOutCompletedEvent;
import org.penpusher.client.model.ApplicationModel;
import org.penpusher.client.model.CategoryModel;
import org.penpusher.client.model.DocumentModel;
import org.penpusher.client.model.FormModel;
import org.penpusher.client.service.ApplicationService;
import org.penpusher.client.service.ApplicationServiceAsync;
import org.penpusher.client.service.DocumentService;
import org.penpusher.client.service.DocumentServiceAsync;
import org.penpusher.client.service.FormService;
import org.penpusher.client.service.FormServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class ApplicationController {
    /**
     * Remote Application service proxy to talk to the server-side.
     */
    private final ApplicationServiceAsync applicationService = GWT.create(ApplicationService.class);

    /**
     * Remote Form service proxy to talk to the server-side.
     */
    private final FormServiceAsync formService = GWT.create(FormService.class);

    /**
     * Remote OfficeEngine service proxy to talk to the server-side.
     */
    private final DocumentServiceAsync officeEngineService = GWT.create(DocumentService.class);

    final private SimpleEventBus eventBus;

    public ApplicationController(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void getSession(final ApplicationModel model) {
        applicationService.getSession(new AsyncCallback<PPSession>() {
            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(final PPSession result) {
                model.setSession(result);
                eventBus.fireEvent(new SessionEvent(result));
            }
        });
    }

    public void signOut() {
        applicationService.signOut(new AsyncCallback<Void>() {

            @Override
            public void onSuccess(final Void result) {
                eventBus.fireEvent(new SignOutCompletedEvent());
            }

            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }

    public void loadCategories(final CategoryModel model) {
        formService.loadCategories(new AsyncCallback<List<Category>>() {
            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(final List<Category> result) {
                model.setCategories(result);
                eventBus.fireEvent(new CategoryChangedEvent(result));
            }
        });
    }

    public void loadForms(final FormModel model) {
        formService.loadForms(new AsyncCallback<List<Form>>() {
            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(final List<Form> result) {
                model.setForms(result);
                eventBus.fireEvent(new FormChangedEvent(result));
            }
        });
    }

    public void getDocuments(final Folder folder, final DocumentModel model) {
        officeEngineService.listDocuments(folder, new AsyncCallback<List<DocumentDescriptor>>() {

            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(final List<DocumentDescriptor> result) {
                model.setDocuments(result);
                eventBus.fireEvent(new DocumentChangedEvent(result));
            }
        });

    }

    public void addDocument(final String filename, final String description, final Form form, final Folder folder) {
        officeEngineService.addDocument(filename, description, form, folder, new AsyncCallback<String>() {

            @Override
            public void onSuccess(final String filename) {
                eventBus.fireEvent(new GenerateDocumentCompletedEvent());

            }

            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }

    public void removeDocument(final String filename, final Folder folder) {
        officeEngineService.removeDocument(filename, folder, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(final Void result) {
                eventBus.fireEvent(new RemoveDocumentCompletedEvent());
            }

            @Override
            public void onFailure(final Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }

    public void exportDocument(final String filename, final Folder folder) {
        // Download the document
        final StringBuffer url = new StringBuffer(GWT.getModuleBaseURL());
        url.append("documentDownloader");
        url.append("?path=");
        url.append(folder.getPath());
        url.append("&filename=");
        url.append(filename);
        Window.open(url.toString(), "_self", "");
    }
}