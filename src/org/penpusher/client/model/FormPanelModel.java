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
package org.penpusher.client.model;

import java.util.ArrayList;
import java.util.List;

import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.data.Form;
import org.penpusher.client.data.FormElement;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class FormPanelModel {

    private FormElement filenameElement;
    private FormElement descriptionElement;
    private Form form;
    private boolean updated;
    private final List<String> filenames = new ArrayList<String>();

    public String getFilename() {
        return filenameElement.getSelectedValue();
    }

    public void setFilenameElement(final FormElement filename) {
        this.filenameElement = filename;
    }

    public String getDescription() {
        return "";// descriptionElement.getSelectedValue();
    }

    public void setDescriptionElement(final FormElement description) {
        this.descriptionElement = description;
    }

    public void setForm(final Form form) {
        updated = false;
        this.form = form;
    }

    public void setDocuments(final List<DocumentDescriptor> documents) {
        filenames.clear();
        for (final DocumentDescriptor document : documents) {
            filenames.add(document.getName());
        }

    }

    public Form getForm() {
        return form;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(final boolean updated) {
        this.updated = updated;
    }

    public boolean isFormNameValid() {
        final String filename = getFilename();
        if (filename == null || filename.trim().length() == 0) {
            return false;
        }
        for (final String existingFilename : filenames) {
            final int index = existingFilename.lastIndexOf('.');
            if (filename.equalsIgnoreCase(existingFilename.substring(0, index))) {
                return false;
            }
        }
        return true;
    }

    public boolean isFormElementValid(final FormElement element) {
        if (element.isOptional()) {
            return true;
        }
        final String selection = element.getSelectedValue();
        return selection != null && selection.trim().length() != 0;
    }

    public boolean isFormValid() {
        if (!isFormNameValid()) {
            return false;
        }

        final List<FormElement> elements = form.getAllFormElement();
        for (final FormElement element : elements) {
            if (!isFormElementValid(element)) {
                return false;
            }
        }
        return true;
    }
}