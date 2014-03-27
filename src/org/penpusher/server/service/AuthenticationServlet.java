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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ax.FetchRequest;
import org.penpusher.server.AuthenticationContext;
import org.penpusher.server.WebApplication;
import org.penpusher.server.logging.PPLogger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class AuthenticationServlet extends HttpServlet {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationServlet.class.getName());

    public final static String ATTR_REQUESTED_URI = "org.penpusher.requested.requesteduri";
    public final static String OPENID_ATTR_DISCOVERED = "org.penpusher.openid.discovered";
    public final static String OPENID_ATTR_IDENTIFIER = "org.penpusher.openid.identifier";
    public final static String OPENID_FIRSTNAME = "firstName";
    public final static String OPENID_LASTNAME = "lastName";
    public final static String OPENID_EMAIL = "email";
    public static final String URI_AUTHENTICATION = "/penpusher/authentication";
    public static final String URI_INDEX = "/penpusher.html";
    public static final String URI_CHECK_AUTHENTICATION = "penpusher/checkAuthentication";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
            IOException {
        try {
            final WebApplication application = WebApplication.getInstance();
            final AuthenticationContext authContext = application.getAuthenticationContext();
            final ConsumerManager manager = authContext.getOpenIDConsumerManager();

            final String openIDEndPoint = authContext.getOpenIDEndPoint();

            // configure the return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            final String returnToUrl = FileUtils.buildUrl(request, URI_CHECK_AUTHENTICATION);

            // perform discovery on the user-supplied identifier
            final List<?> discoveries = manager.discover(openIDEndPoint);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            final DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            request.getSession().setAttribute(OPENID_ATTR_DISCOVERED, discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            final AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            // Attribute Exchange example: fetching the 'email' attribute
            final FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute(OPENID_EMAIL, authContext.getEmailSchema(), true);
            fetch.addAttribute(OPENID_FIRSTNAME, authContext.getFirstNameSchema(), true);
            fetch.addAttribute(OPENID_LASTNAME, authContext.getLastNameSchema(), true);

            // attach the extension to the authentication request
            authReq.addExtension(fetch);

            if (!discovered.isVersion2()) {
                response.sendRedirect(authReq.getDestinationUrl(true));
            }
            else {
                // TODO Create a jsp form to make a post
                response.sendRedirect(authReq.getDestinationUrl(true));
            }
        } catch (final OpenIDException e) {
            PPLogger.error(LOGGER, e, "Authentication error");
        }
    }
}