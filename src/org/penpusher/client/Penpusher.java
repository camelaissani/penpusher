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
package org.penpusher.client;

import org.penpusher.client.controller.ApplicationController;
import org.penpusher.client.event.SessionEvent;
import org.penpusher.client.event.SessionEventHandler;
import org.penpusher.client.event.SignOutCompletedEvent;
import org.penpusher.client.event.SignOutCompletedEventHandler;
import org.penpusher.client.model.ApplicationModel;
import org.penpusher.client.ui.MainPanel;
import org.penpusher.client.ui.SignOutPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author camel.aissani(at)gmail.com
 */
public class Penpusher implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final SimpleEventBus eventBus = new SimpleEventBus();
        final ApplicationModel model = ApplicationModel.create(eventBus);
        final ApplicationController ctrl = new ApplicationController(eventBus);

        // Instantiates view
        final RootLayoutPanel root = RootLayoutPanel.get();
        final MainPanel mainPane = new MainPanel(ctrl, model);
        root.add(mainPane);

        eventBus.addHandler(SessionEvent.TYPE, new SessionEventHandler() {
            @Override
            public void onSession(final SessionEvent event) {
                // Load forms and categories. Forms must be loaded first.
                ctrl.loadForms(model.getFormModel());
                ctrl.loadCategories(model.getCategoryModel());
            }
        });

        eventBus.addHandler(SignOutCompletedEvent.TYPE, new SignOutCompletedEventHandler() {
            @Override
            public void onSignOutCompleted(final SignOutCompletedEvent event) {
                root.remove(mainPane);
                root.add(new SignOutPanel());
            }
        });

        // Gets session information
        ctrl.getSession(model);
    }
}
