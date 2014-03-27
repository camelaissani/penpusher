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

import java.util.List;

import org.penpusher.client.data.Form;
import org.penpusher.client.event.ListSelectionChangedEvent;
import org.penpusher.client.event.ListSelectionChangedEventHandler;
import org.penpusher.client.model.ListModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class ChoiceFormPanel extends Composite {
    @UiTemplate("ChoiceFormPanel.ui.xml")
    interface Binder extends UiBinder<Widget, ChoiceFormPanel> {}

    private static final Binder binder = GWT.create(Binder.class);

    private final ListModel<Form> formModel = new ListModel<Form>();

    // UI components
    @UiField
    PPList<Form> forms;

    public ChoiceFormPanel(final SimpleEventBus eventBus) {
        initComponents(eventBus);
        initListeners();
    }

    private void initComponents(final SimpleEventBus eventBus) {
        initWidget(binder.createAndBindUi(this));
        forms.setEventBus(eventBus);
        formModel.setTitle("Select a form");
    }

    private void initListeners() {}

    public HandlerRegistration addSelectionHandler(final ListSelectionChangedEventHandler handler) {
        return addHandler(handler, ListSelectionChangedEvent.TYPE);
    }

    public void setModel(final List<Form> formList) {
        forms.clearSelection();
        formModel.setItems(formList);
        forms.setModel(formModel, false);
    }

    public Form getSelectedForm() {
        return forms.getSelectedObject();
    }
}