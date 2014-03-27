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
package org.penpusher.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.penpusher.client.data.PPSession;
import org.penpusher.server.logging.PPLogger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */

public class AuthenticationContext {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationContext.class.getName());

    private Map<String, PPSession> sessions;

    private ConsumerManager manager;
    private String openIDEndPoint;
    private String emailSchema;
    private String firstNameSchema;
    private String lastNameSchema;
    private List<String> authorizedPeople;

    // Call after xstream deserialization
    private Object readResolve() {
        sessions = Collections.synchronizedMap(new HashMap<String, PPSession>());
        try {
            manager = new ConsumerManager();
        } catch (final ConsumerException e) {
            PPLogger.error(LOGGER, e, "Can not create an instance of ConsumerManager");
        }
        return this;
    }

    public String getOpenIDEndPoint() {
        return openIDEndPoint;
    }

    public ConsumerManager getOpenIDConsumerManager() {
        return manager;
    }

    public String getEmailSchema() {
        return emailSchema;
    }

    public String getFirstNameSchema() {
        return firstNameSchema;
    }

    public String getLastNameSchema() {
        return lastNameSchema;
    }

    public boolean isValidEmail(final String email) {
        return authorizedPeople.contains(email);
    }

    public boolean isValidSession(final Object openID, final String sessionId) {
        if (openID == null || sessionId == null) {
            return false;
        }
        final PPSession refSession = sessions.get(openID);
        return refSession != null && sessionId.equals(refSession.getSessionId());
    }

    public void addSession(final String openID, final PPSession session) {
        if (openID == null || session == null) {
            return;
        }
        removeSession(openID);
        sessions.put(openID, session);
    }

    public void removeSession(final Object openID) {
        if (openID == null) {
            return;
        }
        sessions.remove(openID);
    }

    public PPSession getSession(final Object openID) {
        return sessions.get(openID);
    }
}