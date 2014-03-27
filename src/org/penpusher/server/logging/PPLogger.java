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
package org.penpusher.server.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public final class PPLogger {

    private PPLogger() {}

    private static synchronized String buildMessages(final String... messages) {
        final StringBuilder builder = new StringBuilder();
        for (final String message : messages) {
            builder.append(message).append(" ");
        }
        return builder.toString();
    }

    public static synchronized void debug(final Logger logger, final String... messages) {
        if (logger.isDebugEnabled()) {
            logger.debug(buildMessages(messages));
        }
    }

    public static synchronized void info(final Logger logger, final String... messages) {
        if (logger.isInfoEnabled()) {
            logger.info(buildMessages(messages));
        }
    }

    public static synchronized void warn(final Logger logger, final String... messages) {
        if (logger.isEnabledFor(Level.WARN)) {
            logger.warn(buildMessages(messages));
        }
    }

    public static synchronized void error(final Logger logger, final String... messages) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(buildMessages(messages));
        }
    }

    public static synchronized void error(final Logger logger, final Throwable e, final String... messages) {
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(buildMessages(messages), e);
        }
    }
}