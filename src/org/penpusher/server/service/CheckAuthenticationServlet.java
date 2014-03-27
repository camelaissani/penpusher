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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.penpusher.client.data.PPSession;
import org.penpusher.server.AuthenticationContext;
import org.penpusher.server.WebApplication;
import org.penpusher.server.logging.PPLogger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class CheckAuthenticationServlet extends HttpServlet {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(CheckAuthenticationServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
            IOException {
        try {
            final WebApplication application = WebApplication.getInstance();
            final AuthenticationContext authContext = application.getAuthenticationContext();
            final ConsumerManager manager = authContext.getOpenIDConsumerManager();

            // extract the parameters from the authentication response
            final ParameterList openIdProviderResponse = new ParameterList(request.getParameterMap());

            // retrieve the previously stored discovery information and original query string
            final HttpSession session = request.getSession();
            final Object originalRequestedURI = session.getAttribute(AuthenticationServlet.ATTR_REQUESTED_URI);
            final DiscoveryInformation discovered =
                    (DiscoveryInformation) session.getAttribute(AuthenticationServlet.OPENID_ATTR_DISCOVERED);

            // extract the receiving URL from the HTTP request
            final StringBuffer receivingURL = request.getRequestURL();
            final String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                receivingURL.append("?").append(queryString);
            }

            // verify the response via ConsumerManager
            final VerificationResult verification =
                    manager.verify(receivingURL.toString(), openIdProviderResponse, discovered);

            // examine the verification result and extract the verified identifier
            boolean authenticated = false;
            final Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                // success, use the verified identifier to identify the user
                final AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    // Retrives data from openID provider response
                    final FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
                    final String first = fetchResponse(fetchResp, AuthenticationServlet.OPENID_FIRSTNAME);
                    final String last = fetchResponse(fetchResp, AuthenticationServlet.OPENID_LASTNAME);
                    final String email = fetchResponse(fetchResp, AuthenticationServlet.OPENID_EMAIL);
                    if (authContext.isValidEmail(email)) {
                        PPLogger.info(LOGGER, "User", first, last, "is authenticated");

                        // Stores the session and user's informations in current application
                        session.setAttribute(AuthenticationServlet.OPENID_ATTR_IDENTIFIER, verified.getIdentifier());
                        final PPSession ppSession = new PPSession();
                        ppSession.setSessionId(session.getId());
                        ppSession.setFirstName(first);
                        ppSession.setLastName(last);
                        ppSession.setEmail(email);
                        authContext.addSession(verified.getIdentifier(), ppSession);
                        authenticated = true;
                    }
                    else {
                        PPLogger.info(LOGGER, "User", first, last, "is not authorized");
                    }
                }
            }
            if (!authenticated) {
                PPLogger.info(LOGGER, "Authentication failed");
                // OpenID authentication failed
                session.invalidate();
            }
            String URL = FileUtils.buildUrl(request, AuthenticationServlet.URI_INDEX);
            if (originalRequestedURI != null) {
                URL = FileUtils.buildUrl(request, originalRequestedURI.toString());
            }
            PPLogger.info(LOGGER, "After authentication redirect to", URL);
            response.sendRedirect(URL);

        } catch (final OpenIDException e) {
            PPLogger.error(LOGGER, e, "OpenID authentication failed");
        }
    }

    private String fetchResponse(final FetchResponse fetchResp, final String attribute) {
        final List<?> values = fetchResp.getAttributeValues(attribute);
        String value = null;
        if (values.size() > 0) {
            value = values.get(0).toString();
        }
        return value;
    }
}