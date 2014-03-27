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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class ToolBar extends Composite {

    @UiTemplate("ToolBar.ui.xml")
    interface Binder extends UiBinder<Widget, ToolBar> {}

    interface ToolBarStyle extends CssResource {
        String link();

        String lastlink();
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    ToolBarStyle style;
    @UiField
    Label title;
    @UiField
    HTMLPanel links;

    private SimpleEventBus eventBus;

    public ToolBar() {
        initComponents();
    }

    public void setEventBus(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
    }

    private void initComponents() {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void setTitle(final String text) {
        title.setText(text);
    }

    public void addAction(final Action... actions) {
        final int count = actions.length;
        int index = 0;

        for (final Action action : actions) {
            final Anchor link = new Anchor(action.getLabel());
            link.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent event) {
                    if (eventBus != null && action.getEvent() != null) {
                        eventBus.fireEvent(action.getEvent());
                    }
                }
            });
            if (index == count - 1) {
                link.setStyleName(style.lastlink());
            }
            else {
                link.setStyleName(style.link());
            }
            links.add(link, "links");
            index++;
        }
    }
}
