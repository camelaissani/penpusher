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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.penpusher.client.data.Form;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class FormModel implements Serializable {

    private List<Form> forms;

    public Form getFormById(final String id) {
        if (null == id) {
            return null;
        }

        final List<Form> forms = getForms();
        for (final Form form : forms) {
            if (id.equals(form.getId())) {
                return form;
            }
        }
        return null;
    }

    public List<Form> getForms() {
        if (forms == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(forms);
    }

    public void setForms(final List<Form> forms) {
        this.forms = forms;
    }
}