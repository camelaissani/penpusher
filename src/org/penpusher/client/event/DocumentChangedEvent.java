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
package org.penpusher.client.event;

import java.util.Collections;
import java.util.List;

import org.penpusher.client.data.DocumentDescriptor;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class DocumentChangedEvent extends GwtEvent<DocumentChangedEventHandler> {
    public static Type<DocumentChangedEventHandler> TYPE = new Type<DocumentChangedEventHandler>();

    // data
    private final List<DocumentDescriptor> documents;

    public DocumentChangedEvent(final List<DocumentDescriptor> documents) {
        this.documents = documents;
    }

    @Override
    public Type<DocumentChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final DocumentChangedEventHandler handler) {
        handler.onDocumentChanged(this);
    }

    public List<DocumentDescriptor> getDocuments() {
        if (documents == null) {
            return Collections.emptyList();
        }
        return documents;
    }
}
