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

import java.util.Date;

import org.penpusher.client.data.FormElement;
import org.penpusher.client.model.FormPanelModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class FormEntry extends Composite {
    @UiTemplate("FormEntry.ui.xml")
    interface Binder extends UiBinder<Widget, FormEntry> {}

    interface FormEntryStyle extends CssResource {
        String label();

        String datebox();

        String textbox();

        String textarea();

        String listbox();

        String valid();

        String notvalid();
    }

    private static final Binder binder = GWT.create(Binder.class);

    private DateTimeFormat outputDateFormat = null;

    private String label;
    private Widget widget;
    @UiField
    FormEntryStyle style;
    @UiField
    VerticalPanel entry;
    @UiField
    Label entryLabel;

    public static FormEntry create(final FormElement element, final FormPanelModel model) {
        final FormEntry instance = new FormEntry();
        instance.label = element.getLabel();
        instance.widget = instance.build(element, model);
        instance.initComponents();
        instance.initListeners();
        return instance;
    }

    private FormEntry() {
        initWidget(binder.createAndBindUi(this));
    }

    public void setValid(final boolean valid) {
        if (!valid) {
            entry.setStyleName(style.notvalid());
            final StringBuilder builder = new StringBuilder(">> ");
            builder.append(label).append(" <<");
            entryLabel.setText(builder.toString());
        }
        else {
            entry.setStyleName(style.valid());
            entryLabel.setText(label);
        }
    }

    public void setWidgetStyleName(final String style) {
        widget.setStyleName(style);
    }

    private void initListeners() {}

    private void initComponents() {
        entryLabel.setText(label);
        entry.add(widget);
    }

    private Widget build(final FormElement element, final FormPanelModel model) {
        Widget widget = null;
        switch (element.getWidget()) {
            case TEXTFIELD:
                final TextBox txtBox = new TextBox();
                txtBox.setStyleName(style.textbox());
                // txtBox.setText(element.getDefaultValue());
                txtBox.addValueChangeHandler(new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange(final ValueChangeEvent<String> event) {
                        model.setUpdated(true);
                        element.setSelectedValue(txtBox.getText());
                    }
                });
                widget = txtBox;
            break;
            case TEXTAREA:
                final TextArea txtArea = new TextArea();
                txtArea.setStyleName(style.textarea());
                // txtArea.setText(element.getDefaultValue());
                txtArea.addValueChangeHandler(new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange(final ValueChangeEvent<String> event) {
                        model.setUpdated(true);
                        element.setSelectedValue(txtArea.getText());
                    }
                });
                widget = txtArea;
            break;
            case COMBOBOX:
                final ListBox listBox = new ListBox();
                listBox.setStyleName(style.listbox());
                for (final String value : element.getValues()) {
                    listBox.addItem(value);
                }
                element.setSelectedValue(listBox.getItemText(0));
                listBox.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(final ChangeEvent event) {
                        model.setUpdated(true);
                        final int index = listBox.getSelectedIndex();
                        element.setSelectedValue(listBox.getItemText(index));
                    }
                });
                widget = listBox;
            break;
            case DATE:
                final DateBox dateBox = createDateBox(element);
                dateBox.setStyleName(style.datebox());
                dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {

                    @Override
                    public void onValueChange(final ValueChangeEvent<Date> event) {
                        model.setUpdated(true);
                        final Date date = event.getValue();
                        if (date != null) {
                            element.setSelectedValue(outputDateFormat.format(date));
                        }
                        else {
                            element.setSelectedValue(null);
                        }
                    }
                });
                // dateBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
                // @Override
                // public void onValueChange(final ValueChangeEvent<String> event) {
                // model.setUpdated(true);
                // final Date date = dateBox.getValue();
                // if (date != null) {
                // element.setSelectedValue(dateFormat.format(date));
                // }
                // else {
                // element.setSelectedValue(null);
                // }
                // }
                // });
                widget = dateBox;
            break;
        }
        if (widget != null && element.getDefaultValue() != null) {
            widget.setTitle("Sample: " + element.getDefaultValue());
        }
        return widget;
    }

    private DateBox createDateBox(final FormElement element) {
        DateTimeFormat dateFormat = null;
        final String pattern = element.getFormat();
        if (pattern == null || pattern.length() == 0) {
            dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
        }
        else {
            dateFormat = DateTimeFormat.getFormat(pattern);
        }
        final String outputPattern = element.getOutputFormat();
        if (outputPattern == null || outputPattern.length() == 0) {
            outputDateFormat = dateFormat;
        }
        else {
            outputDateFormat = DateTimeFormat.getFormat(outputPattern);
        }
        // final Date defaultDate = getDateFormat().parse(element.getDefaultValue());
        final DateBox.DefaultFormat defaultFormat = new DateBox.DefaultFormat(dateFormat) {
            @Override
            public Date parse(final DateBox dateBox, final String dateText, final boolean reportError) {
                Date date = null;
                try {
                    if (dateText.length() > 0) {
                        date = getDateTimeFormat().parse(dateText);
                    }
                } catch (final IllegalArgumentException exception) {
                    try {
                        final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
                        date = format.parse(dateText);
                    } catch (final IllegalArgumentException e) {
                        return super.parse(dateBox, dateText, reportError);
                    }
                }
                return date;
            }
        };

        return new DateBox(new DatePicker(), null, defaultFormat);
    }
}