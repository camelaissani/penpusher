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
import java.util.Collections;
import java.util.List;

import org.penpusher.client.data.DocumentDescriptor;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class DocumentModel implements Serializable {
    private int selectedIndex = -1;
    private List<DocumentDescriptor> documents;

    public void setDocuments(final List<DocumentDescriptor> documents) {
        this.documents = documents;
    }

    public List<DocumentDescriptor> getDocuments() {
        if (documents == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(documents);
    }

    public DocumentDescriptor getDocument(final int index) {
        if (documents != null && index > -1 && index < documents.size()) {
            return documents.get(index);
        }
        return null;
    }

    public void setSelectedIndex(final int index) {
        selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public DocumentDescriptor getSelectedDocument() {
        final int index = getSelectedIndex();
        if (documents != null && index > -1 && index < documents.size()) {
            return documents.get(index);
        }
        return null;
    }

}
