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

import java.util.ArrayList;
import java.util.List;

import org.penpusher.client.controller.ApplicationController;
import org.penpusher.client.data.Category;
import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.data.Folder;
import org.penpusher.client.data.Form;
import org.penpusher.client.event.CancelGenerateDocumentEvent;
import org.penpusher.client.event.CancelGenerateDocumentEventHandler;
import org.penpusher.client.event.CategoryChangedEvent;
import org.penpusher.client.event.CategoryChangedEventHandler;
import org.penpusher.client.event.ExportDocumentEvent;
import org.penpusher.client.event.ExportDocumentEventHandler;
import org.penpusher.client.event.GenerateDocumentCompletedEvent;
import org.penpusher.client.event.GenerateDocumentCompletedEventHandler;
import org.penpusher.client.event.GenerateDocumentEvent;
import org.penpusher.client.event.GenerateDocumentEventHandler;
import org.penpusher.client.event.ListSelectionChangedEvent;
import org.penpusher.client.event.ListSelectionChangedEventHandler;
import org.penpusher.client.event.NewDocumentEvent;
import org.penpusher.client.event.NewDocumentEventHandler;
import org.penpusher.client.event.RemoveDocumentCompletedEvent;
import org.penpusher.client.event.RemoveDocumentCompletedEventHandler;
import org.penpusher.client.event.RemoveDocumentEvent;
import org.penpusher.client.event.RemoveDocumentEventHandler;
import org.penpusher.client.event.SignOutEvent;
import org.penpusher.client.event.SignOutEventHandler;
import org.penpusher.client.model.ApplicationModel;
import org.penpusher.client.model.DocumentModel;
import org.penpusher.client.model.FormPanelModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author camel.aissani(at)gmail.com
 */
public class MainPanel extends Composite {
    @UiTemplate("MainPanel.ui.xml")
    interface Binder extends UiBinder<Widget, MainPanel> {}

    private static final Binder binder = GWT.create(Binder.class);

    // UI components
    @UiField
    LeftMenuPanel menuPane;
    @UiField
    DeckPanel deck;

    private DocumentList table;
    private FormPanel formPanel;
    private ChoiceFormPanel choicePanel;

    // Controller
    private final ApplicationController controller;

    // Model
    private final ApplicationModel model;

    private boolean formPanelDisplayed = false;

    public MainPanel(final ApplicationController controller, final ApplicationModel model) {
        this.controller = controller;
        this.model = model;
        initComponents();
        initListeners();
    }

    @UiFactory
    TopBar createTopBar() {
        return new TopBar(model.getEventBus());
    }

    private void initComponents() {
        initWidget(binder.createAndBindUi(this));
        menuPane.setEventBus(model.getEventBus());
        table = DocumentList.create(model.getEventBus(), model.getDocumentModel());
        formPanel = new FormPanel(model.getEventBus());
        choicePanel = new ChoiceFormPanel(model.getEventBus());
    }

    private void initListeners() {
        model.getEventBus().addHandler(SignOutEvent.TYPE, new SignOutEventHandler() {

            @Override
            public void onSignOut(final SignOutEvent event) {
                if (isFormComplete()) {
                    final ConfirmationDialog dlg =
                            new ConfirmationDialog("Generate document",
                                    "The Form has been modified. Do you want to lose changes?") {
                                @Override
                                public void onOk() {
                                    controller.signOut();
                                }
                            };
                    dlg.show();
                }
                else {
                    controller.signOut();
                }
            }
        });

        model.getEventBus().addHandler(CategoryChangedEvent.TYPE, new CategoryChangedEventHandler() {
            @Override
            public void onCategoryChanged(final CategoryChangedEvent event) {
                menuPane.setOrganisationModel(event.getCategories());
            }
        });

        model.getEventBus().addHandler(ListSelectionChangedEvent.TYPE, new ListSelectionChangedEventHandler() {

            @Override
            public void onListSelectionChanged(final ListSelectionChangedEvent event) {
                final Object selection = event.getSelection();
                if (selection instanceof Category) {
                    if (isFormComplete()) {
                        event.setChangeSelection(false);
                        final ConfirmationDialog dlg =
                                new ConfirmationDialog("Generate document",
                                        "The Form has been modified. Do you want to lose changes?") {
                                    @Override
                                    public void onOk() {
                                        setFormComplete(true);
                                        menuPane.organisations.selectRow(event.getRow());
                                        final Category category = (Category) selection;
                                        menuPane.setFolderModel(category.getFolders());
                                    }
                                };
                        dlg.show();
                    }
                    else {
                        final Category category = (Category) selection;
                        menuPane.setFolderModel(category.getFolders());
                    }
                }
                else if (selection instanceof Folder) {
                    if (isFormComplete()) {
                        event.setChangeSelection(false);
                        final ConfirmationDialog dlg =
                                new ConfirmationDialog("Generate document",
                                        "The Form has been modified. Do you want to lose changes?") {
                                    @Override
                                    public void onOk() {
                                        menuPane.folders.selectRow(event.getRow());
                                        showDocumentTable((Folder) selection);
                                    }
                                };
                        dlg.show();
                    }
                    else {
                        showDocumentTable((Folder) selection);
                    }
                }
                else if (selection instanceof Form) {
                    final Form form = (Form) selection;
                    final FormPanelModel fpModel = model.getFormPanelModel();
                    fpModel.setForm(form.copy());
                    fpModel.setDocuments(model.getDocumentModel().getDocuments());
                    formPanel.setModel(fpModel);
                    showFormPanel();
                }
            }
        });

        model.getEventBus().addHandler(NewDocumentEvent.TYPE, new NewDocumentEventHandler() {

            @Override
            public void onNewDocument(final NewDocumentEvent event) {
                final List<String> forms = menuPane.getSelectedFolder().getForms();
                Form form = null;
                final int formCount = forms.size();
                if (formCount == 1) {
                    form = model.getFormModel().getFormById(forms.get(0));
                    if (form != null) {
                        final FormPanelModel fpModel = model.getFormPanelModel();
                        fpModel.setForm(form.copy());
                        fpModel.setDocuments(model.getDocumentModel().getDocuments());
                        formPanel.setModel(fpModel);
                        showFormPanel();
                    }
                }
                else if (formCount > 0) {
                    showChoiceFormPanel(forms);
                }
            }

        });

        model.getEventBus().addHandler(GenerateDocumentEvent.TYPE, new GenerateDocumentEventHandler() {

            @Override
            public void onGenerateDocument(final GenerateDocumentEvent event) {
                final FormPanelModel fpModel = model.getFormPanelModel();
                final Form form = fpModel.getForm();
                if (fpModel.isFormValid()) {
                    controller.addDocument(fpModel.getFilename(), fpModel.getDescription(), form,
                            menuPane.getSelectedFolder());
                }
            }
        });

        model.getEventBus().addHandler(GenerateDocumentCompletedEvent.TYPE,
                new GenerateDocumentCompletedEventHandler() {

                    @Override
                    public void onGenerateDocumentCompleted(final GenerateDocumentCompletedEvent event) {
                        showDocumentTable(menuPane.getSelectedFolder());
                    }
                });

        model.getEventBus().addHandler(CancelGenerateDocumentEvent.TYPE, new CancelGenerateDocumentEventHandler() {
            @Override
            public void onCancelGenerateDocument(final CancelGenerateDocumentEvent event) {
                if (isFormComplete()) {
                    final ConfirmationDialog dlg =
                            new ConfirmationDialog("Generate document",
                                    "The Form has been modified. Do you want to lose changes?") {
                                @Override
                                public void onOk() {
                                    showDocumentTable();
                                }
                            };
                    dlg.show();
                }
                else {
                    showDocumentTable();
                }
            }
        });

        model.getEventBus().addHandler(ExportDocumentEvent.TYPE, new ExportDocumentEventHandler() {

            @Override
            public void onExportDocument(final ExportDocumentEvent event) {
                final DocumentDescriptor document = model.getDocumentModel().getSelectedDocument();
                if (document != null) {
                    controller.exportDocument(document.getName(), menuPane.getSelectedFolder());
                }
            }
        });

        model.getEventBus().addHandler(RemoveDocumentEvent.TYPE, new RemoveDocumentEventHandler() {

            @Override
            public void onRemoveDocument(final RemoveDocumentEvent event) {
                final DocumentDescriptor document = model.getDocumentModel().getSelectedDocument();
                if (document != null) {
                    final ConfirmationDialog dlg =
                            new ConfirmationDialog("Remove document", "Do you want to remove " + document.getName()
                                    + "?") {
                                @Override
                                public void onOk() {
                                    final Folder folder = menuPane.getSelectedFolder();
                                    controller.removeDocument(document.getName(), folder);
                                }
                            };
                    dlg.show();
                }
            }
        });

        model.getEventBus().addHandler(RemoveDocumentCompletedEvent.TYPE, new RemoveDocumentCompletedEventHandler() {

            @Override
            public void onRemoveDocumentCompleted(final RemoveDocumentCompletedEvent event) {
                showDocumentTable(menuPane.getSelectedFolder());
            }
        });
    }

    private void showDocumentTable(final Folder folder) {
        final DocumentModel documentModel = model.getDocumentModel();
        controller.getDocuments(folder, documentModel);
        showDocumentTable();
    }

    private void showDocumentTable() {
        setFormComplete(true);
        int index = deck.getWidgetIndex(table);
        if (index == -1) {
            deck.add(table);
            index = deck.getWidgetIndex(table);
        }
        deck.showWidget(index);
    }

    private void showChoiceFormPanel(final List<String> formIds) {
        int index = deck.getWidgetIndex(choicePanel);
        if (index == -1) {
            deck.add(choicePanel);
            index = deck.getWidgetIndex(choicePanel);
        }
        final List<Form> forms = new ArrayList<Form>();
        for (final String formId : formIds) {
            forms.add(model.getFormModel().getFormById(formId));
        }
        choicePanel.setModel(forms);
        deck.showWidget(index);
    }

    private void setFormComplete(final boolean complete) {
        formPanelDisplayed = !complete;
        model.getFormPanelModel().setUpdated(!complete);
    }

    private boolean isFormComplete() {
        return (formPanelDisplayed && model.getFormPanelModel().isUpdated());
    }

    private void showFormPanel() {
        formPanelDisplayed = true;
        int index = deck.getWidgetIndex(formPanel);
        if (index == -1) {
            deck.add(formPanel);
            index = deck.getWidgetIndex(formPanel);
        }
        deck.showWidget(index);
    }
}