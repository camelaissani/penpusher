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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.penpusher.server.AuthenticationContext;
import org.penpusher.server.WebApplication;
import org.penpusher.server.logging.PPLogger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class AuthenticationFilter implements Filter {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    private final List<String> uncheckedURIs = new ArrayList<String>(2);

    @Override
    public void init(final FilterConfig config) throws ServletException {
        final Enumeration<?> parameters = config.getInitParameterNames();
        while (parameters.hasMoreElements()) {
            final String parameter = config.getInitParameter(parameters.nextElement().toString());
            if (parameter != null && parameter.length() > 0) {
                uncheckedURIs.add(parameter);
            }
        }
    }

    @Override
    public void destroy() {
        uncheckedURIs.clear();
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String uri = httpRequest.getRequestURI();
        final String queryString = httpRequest.getQueryString();
        
        if (!uncheckedURIs.contains(uri)) {
        	PPLogger.debug(LOGGER, "Call securized URI:",uri, "QS:", queryString);

            final WebApplication application = WebApplication.getInstance();
            final AuthenticationContext authContext = application.getAuthenticationContext();
            final HttpSession session = httpRequest.getSession();

            if (!authContext.isValidSession(session.getAttribute(AuthenticationServlet.OPENID_ATTR_IDENTIFIER),
                    session.getId())) {
                // get query string requested from the HTTP request
                final StringBuilder requestedURI = new StringBuilder(uri);
                if (queryString != null && queryString.length() > 0) {
                    requestedURI.append('?').append(queryString);
                }
                session.setAttribute(AuthenticationServlet.ATTR_REQUESTED_URI, requestedURI.toString());
                PPLogger.debug(LOGGER, "Query requested:", requestedURI.toString());

                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.sendRedirect(AuthenticationServlet.URI_AUTHENTICATION);
            }
            else {
                chain.doFilter(request, response);
            }
        }
        else {
            PPLogger.debug(LOGGER, "Not securized URI:", uri, "QS:", queryString);
            chain.doFilter(request, response);
        }
    }

}
