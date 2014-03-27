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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public abstract class ConfirmationDialog {
    @UiTemplate("ConfirmationDialog.ui.xml")
    interface Binder extends UiBinder<Widget, ConfirmationDialog> {}

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    DialogBox mainPanel;
    @UiField
    Label message;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;

    private final String title;
    private final String text;

    public ConfirmationDialog(final String title, final String text) {
        this.title = title;
        this.text = text;
        initComponents();
        initListeners();
    }

    public void show() {
        mainPanel.center();
    }

    public abstract void onOk();

    public void onCancel() {}

    private void initListeners() {
        okButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                mainPanel.hide();
                onOk();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                mainPanel.hide();
                onCancel();
            }
        });

    }

    private void initComponents() {
        binder.createAndBindUi(this);
        mainPanel.setText(title);
        message.setText(text);
    }

}