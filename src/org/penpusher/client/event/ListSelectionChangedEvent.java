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
package org.penpusher.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class ListSelectionChangedEvent extends GwtEvent<ListSelectionChangedEventHandler> {
    public static Type<ListSelectionChangedEventHandler> TYPE = new Type<ListSelectionChangedEventHandler>();

    // data
    private final int row;
    private final Object selection;
    private boolean changeSelection = true;

    public ListSelectionChangedEvent(final int row, final Object selection) {
        this.row = row;
        this.selection = selection;
    }

    @Override
    public Type<ListSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ListSelectionChangedEventHandler handler) {
        handler.onListSelectionChanged(this);
    }

    public boolean isChangeSelection() {
        return changeSelection;
    }

    public int getRow() {
        return row;
    }

    public Object getSelection() {
        return selection;
    }

    public void setChangeSelection(final boolean changeSelection) {
        this.changeSelection = changeSelection;
    }
}