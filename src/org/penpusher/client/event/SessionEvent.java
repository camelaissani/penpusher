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

import org.penpusher.client.data.PPSession;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class SessionEvent extends GwtEvent<SessionEventHandler> {
    public static Type<SessionEventHandler> TYPE = new Type<SessionEventHandler>();
    private final PPSession ppSession;

    public SessionEvent(final PPSession ppSession) {
        this.ppSession = ppSession;
    }

    @Override
    public Type<SessionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final SessionEventHandler handler) {
        handler.onSession(this);
    }

    public PPSession getSession() {
        return ppSession;
    }
}