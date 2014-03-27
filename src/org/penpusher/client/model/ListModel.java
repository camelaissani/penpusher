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

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class ListModel<T> implements Serializable {

    private String title;
    private List<T> items;

    public ListModel() {}

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setItems(final List<T> items) {
        this.items = items;
    }

    public T getItem(final int index) {
        if (items != null) {
            if (index >= 0 || index < items.size()) {
                return items.get(index);
            }
        }
        return null;
    }
    
    public int getRowIndex(final T value) {
        if (items != null) {
            int index=0;
            for (T item : items) {
                if(value.equals(item)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    public List<T> getItems() {
        if (items == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(items);
    }
}
