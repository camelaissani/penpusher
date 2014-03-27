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

import javax.servlet.http.HttpSession;

import org.penpusher.client.data.PPSession;
import org.penpusher.client.service.ApplicationService;
import org.penpusher.server.AuthenticationContext;
import org.penpusher.server.WebApplication;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class ApplicationServiceImpl extends RemoteServiceServlet implements ApplicationService {

    @Override
    public PPSession getSession() {
        final HttpSession session = getThreadLocalRequest().getSession();
        final WebApplication application = WebApplication.getInstance();
        final AuthenticationContext authContext = application.getAuthenticationContext();

        final PPSession ppSession =
                authContext.getSession(session.getAttribute(AuthenticationServlet.OPENID_ATTR_IDENTIFIER));
        return ppSession;
    }

    @Override
    public void signOut() {
        final HttpSession session = getThreadLocalRequest().getSession();
        final WebApplication application = WebApplication.getInstance();
        final AuthenticationContext authContext = application.getAuthenticationContext();

        authContext.removeSession(session.getAttribute(AuthenticationServlet.OPENID_ATTR_IDENTIFIER));
        session.invalidate();
    }
}