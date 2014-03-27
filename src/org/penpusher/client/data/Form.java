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
import java.util.ArrayList;
import java.util.List;

/**
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class Form implements Serializable {
    private String id;
    private String name;
    private String template;
    private List<Container> containers;

    public Form() {}

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(final String templateFile) {
        this.template = templateFile;
    }

    public List<FormElement> getAllFormElement() {
        final List<FormElement> elements = new ArrayList<FormElement>();
        for (final Container container : containers) {
            elements.addAll(container.getAllFormElement());
        }
        return elements;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(final List<Container> containers) {
        this.containers = containers;
    }

    public Form copy() {
        final Form copy = new Form();
        copy.id = id;
        copy.name = name;
        copy.template = template;
        if (containers != null && containers.size() > 0) {
            copy.containers = new ArrayList<Container>(containers.size());
            for (final Container container : containers) {
                copy.containers.add(container.copy());
            }
        }
        return copy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Form other = (Form) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }
}