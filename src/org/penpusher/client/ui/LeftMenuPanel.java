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

import org.penpusher.client.data.Category;
import org.penpusher.client.data.Folder;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class LeftMenuPanel extends Composite {
    @UiTemplate("LeftMenuPanel.ui.xml")
    interface Binder extends UiBinder<Widget, LeftMenuPanel> {}

    private static final Binder binder = GWT.create(Binder.class);

    private final ListModel<Category> organisationModel = new ListModel<Category>();
    private final ListModel<Folder> folderModel = new ListModel<Folder>();

    // UI components
    @UiField
    PPList<Category> organisations;
    @UiField
    PPList<Folder> folders;

    public LeftMenuPanel() {
        initComponents();
        initListeners();
    }

    public void setEventBus(final SimpleEventBus eventBus) {
        organisations.setEventBus(eventBus);
        folders.setEventBus(eventBus);
    }

    private void initComponents() {
        initWidget(binder.createAndBindUi(this));
    }

    private void initListeners() {}

    public HandlerRegistration addSelectionHandler(final ListSelectionChangedEventHandler handler) {
        return addHandler(handler, ListSelectionChangedEvent.TYPE);
    }

    public void setOrganisationModel(final List<Category> categories) {
        organisations.clearSelection();
        organisationModel.setItems(categories);
        organisations.setModel(organisationModel);
    }

    public void setFolderModel(final List<Folder> fds) {
        folders.clearSelection();
        folderModel.setItems(fds);
        folders.setModel(folderModel);
    }

    public Category getSelectedOrganisation() {
        return organisations.getSelectedObject();
    }

    public Folder getSelectedFolder() {
        return folders.getSelectedObject();
    }
}