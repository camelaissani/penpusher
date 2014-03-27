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
package org.penpusher.client.model;

import java.io.Serializable;

import org.penpusher.client.data.PPSession;

import com.google.gwt.event.shared.SimpleEventBus;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class ApplicationModel implements Serializable {

    private final SimpleEventBus eventBus;

    private PPSession session;

    // model classes
    private final CategoryModel categoryModel;
    private final FormModel formModel;
    private final DocumentModel documentModel;
    private final FormPanelModel formPanelModel;

    private ApplicationModel(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
        categoryModel = new CategoryModel();
        formModel = new FormModel();
        documentModel = new DocumentModel();
        formPanelModel = new FormPanelModel();
    }

    public synchronized static ApplicationModel create(final SimpleEventBus eventBus) {
        return new ApplicationModel(eventBus);
    }

    public SimpleEventBus getEventBus() {
        return eventBus;
    }

    public void setSession(final PPSession session) {
        this.session = session;
    }

    public PPSession getSession() {
        return session;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    public DocumentModel getDocumentModel() {
        return documentModel;
    }

    public FormPanelModel getFormPanelModel() {
        return formPanelModel;
    }
}
