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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.penpusher.client.data.Container;
import org.penpusher.client.data.Container.ContainerType;
import org.penpusher.client.data.FormElement;
import org.penpusher.client.data.WidgetEnum;
import org.penpusher.client.event.CancelGenerateDocumentEvent;
import org.penpusher.client.event.GenerateDocumentEvent;
import org.penpusher.client.event.GenerateDocumentEventHandler;
import org.penpusher.client.model.FormPanelModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class FormPanel extends Composite {
    @UiTemplate("FormPanel.ui.xml")
    interface Binder extends UiBinder<Widget, FormPanel> {}

    interface FormStyle extends CssResource {
        String captionPanel();

        String description();
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    FormStyle style;

    private FormEntry fileNameEntry;
    private FormEntry fileDescriptionEntry;

    @UiField
    ToolBar toolbar;
    @UiField
    HorizontalPanel errorPanel;
    @UiField
    Label errorLabel;
    @UiField
    FlexTable fileTable;
    @UiField
    VerticalPanel entriesTable;

    private final Map<String, FormEntry> mapOfEntries = new HashMap<String, FormEntry>();

    // Model
    private final SimpleEventBus eventBus;
    private HandlerRegistration generateRegistration;

    public FormPanel(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
        initComponents();
        initListeners();
    }

    private void initListeners() {
        toolbar.setEventBus(eventBus);
        toolbar.addAction(Action.create("! Generate", new GenerateDocumentEvent()),
                Action.create("X Cancel", new CancelGenerateDocumentEvent()));
    }

    private void initComponents() {
        initWidget(binder.createAndBindUi(this));
    }

    public void setModel(final FormPanelModel model) {
        toolbar.setTitle(model.getForm().getName());
        removeErrorPanel();

        // file entries
        final FormElement filenameElement = new FormElement();
        filenameElement.setLabel("File name");
        filenameElement.setWidget(WidgetEnum.TEXTFIELD);
        model.setFilenameElement(filenameElement);
        fileNameEntry = FormEntry.create(filenameElement, model);
        fileTable.setWidget(0, 0, fileNameEntry);
        // fileTable.getCellFormatter().setStyleName(0, 0, style.field());

        // final FormElement descriptionElement = new FormElement();
        // descriptionElement.setLabel("File description");
        // descriptionElement.setWidget(WidgetEnum.TEXTAREA);
        // model.setDescriptionElement(descriptionElement);
        // fileDescriptionEntry = FormEntry.create(descriptionElement, model);
        // fileDescriptionEntry.setWidgetStyleName(style.description());
        // fileTable.setWidget(0, 1, fileDescriptionEntry);
        // // fileTable.getCellFormatter().setStyleName(0, 1, style.field());

        // Form entries
        mapOfEntries.clear();
        entriesTable.clear();
        final List<Container> containers = model.getForm().getContainers();
        final List<Widget> cells = buildForm(model, containers);
        if (cells != null) {
            for (final Widget cell : cells) {
                entriesTable.add(cell);
            }
        }

        if (generateRegistration != null) {
            generateRegistration.removeHandler();
        }
        generateRegistration = eventBus.addHandler(GenerateDocumentEvent.TYPE, new GenerateDocumentEventHandler() {

            @Override
            public void onGenerateDocument(final GenerateDocumentEvent event) {
                boolean formValid = true;
                boolean formNameValid = true;

                if (model != null) {
                    final List<FormElement> elements = model.getForm().getAllFormElement();
                    FormEntry entry;
                    for (final FormElement element : elements) {
                        entry = mapOfEntries.get(element.getKey());
                        if (entry != null) {
                            final boolean valid = model.isFormElementValid(element);
                            entry.setValid(valid);
                            if (!valid) {
                                formValid = false;
                            }
                        }
                    }
                }

                final boolean valid = model.isFormNameValid();
                fileNameEntry.setValid(valid);
                if (!valid) {
                    formNameValid = false;
                }

                if (!formValid) {
                    addErrorPanel("Form not complete");
                }
                else if (!formNameValid) {
                    addErrorPanel("Form name is empty or already exist");
                }
                else {
                    removeErrorPanel();
                }
            }
        });
    }

    private List<Widget> buildForm(final FormPanelModel model, final List<Container> containers) {
        final List<Widget> cells = new ArrayList<Widget>();
        for (final Container container : containers) {
            CellPanel cell;
            if (container.getType() == ContainerType.ROW) {
                cell = new HorizontalPanel();
            }
            else {
                cell = new VerticalPanel();
            }
            for (final FormElement element : container.getFormElement()) {
                final FormEntry entry = FormEntry.create(element, model);
                if (entry != null) {
                    mapOfEntries.put(element.getKey(), entry);
                    cell.add(entry);
                    // entriesTable.getCellFormatter().setStyleName(row, col, style.field());
                    // panel.setStyleName(style.field());
                }
            }
            final List<Widget> child = buildForm(model, container.getContainers());
            if (child != null) {
                for (final Widget cp : child) {
                    cell.add(cp);
                }
            }
            if (container.getLabel() != null) {
                final CaptionPanel captionPanel = new CaptionPanel(container.getLabel());
                captionPanel.setStyleName(style.captionPanel());
                captionPanel.add(cell);
                cells.add(captionPanel);
            }
            else {
                cells.add(cell);
            }
        }
        return cells;
    }

    private void addErrorPanel(final String text) {
        errorLabel.setText(text);
        errorPanel.setVisible(true);
    }

    private void removeErrorPanel() {
        errorLabel.setText(null);
        errorPanel.setVisible(false);
    }

}