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
import java.util.Collections;
import java.util.List;

/**
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class Folder implements Serializable {
    private String name;
    private String description;
    private String path;
    private List<String> forms;

    public Folder() {}

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getForms() {
        return Collections.unmodifiableList(forms);
    }

    public void addForm(final String formId) {
        forms.add(formId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getName();
    }
}
