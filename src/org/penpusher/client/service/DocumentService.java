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
package org.penpusher.client.service;

import java.util.List;

import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.data.Folder;
import org.penpusher.client.data.Form;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@RemoteServiceRelativePath("document")
public interface DocumentService extends RemoteService {

    String addDocument(String filename, String description, Form form, Folder folder);

    void removeDocument(String filename, Folder folder);

    List<DocumentDescriptor> listDocuments(Folder folder);
}
