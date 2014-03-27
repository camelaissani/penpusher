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

import org.penpusher.client.data.PPSession;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public interface ApplicationServiceAsync {

    void getSession(AsyncCallback<PPSession> callback);

    void signOut(AsyncCallback<Void> callback);
}
