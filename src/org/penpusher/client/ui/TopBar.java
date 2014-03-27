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
package org.penpusher.client.ui;

import org.penpusher.client.data.PPSession;
import org.penpusher.client.event.SessionEvent;
import org.penpusher.client.event.SessionEventHandler;
import org.penpusher.client.event.SignOutEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class TopBar extends Composite {
    @UiTemplate("TopBar.ui.xml")
    interface Binder extends UiBinder<Widget, TopBar> {}

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    Label user;
    @UiField
    ToolBar toolbar;

    // Model
    private final SimpleEventBus eventBus;

    public TopBar(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
        initComponents();
        initListeners();
    }

    private void initComponents() {
        initWidget(binder.createAndBindUi(this));
    }

    private void initListeners() {
        toolbar.setEventBus(eventBus);
        toolbar.addAction(Action.create("Sign out", new SignOutEvent()));

        eventBus.addHandler(SessionEvent.TYPE, new SessionEventHandler() {
            @Override
            public void onSession(final SessionEvent event) {
                final PPSession ppSession = event.getSession();
                final StringBuilder builder = new StringBuilder(ppSession.getFirstName());
                builder.append(' ').append(ppSession.getLastName());
                user.setText(builder.toString());
            }
        });

    }
}
