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
package org.penpusher.client.data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class FormElement implements Serializable {
    private String key;
    private String label;
    private List<String> values;
    private String selectedValue;
    private String defaultValue;
    private WidgetEnum widget;
    private String format;
    private String outputFormat;
    private Boolean optional = false;

    public FormElement() {}

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(final List<String> values) {
        this.values = values;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(final String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public WidgetEnum getWidget() {
        return widget;
    }

    public void setWidget(final WidgetEnum widget) {
        this.widget = widget;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(final boolean optional) {
        this.optional = optional;
    }

    public FormElement copy() {
        final FormElement copy = new FormElement();
        copy.key = key;
        copy.label = label;
        copy.values = values;
        copy.selectedValue = null;
        copy.defaultValue = defaultValue;
        copy.widget = widget;
        copy.format = format;
        copy.outputFormat = outputFormat;
        copy.optional = optional;
        return copy;
    }

    @Override
    public String toString() {
        return key;
    }

}