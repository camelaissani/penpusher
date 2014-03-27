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

import java.util.List;

import org.penpusher.client.event.ListSelectionChangedEvent;
import org.penpusher.client.model.ListModel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class PPList<T> extends Composite {
    @UiTemplate("PPList.ui.xml")
    interface Binder extends UiBinder<Widget, PPList<?>> {}

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    Label title;
    @UiField(provided = true)
    CellList<T> list;

    private SimpleEventBus eventBus;
    private ListModel<T> model;
    private SingleSelectionModel<T> selectionModel;


    public PPList() {
        initComponent();
        initListeners();
    }

    public void setEventBus(final SimpleEventBus eventBus) {
        this.eventBus = eventBus;
    }

    private void initComponent() {
        Cell<T> textCell = new GenericCell<T>();
        list = new CellList<T>(textCell);

        initWidget(binder.createAndBindUi(this));
        
        list.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        
        // Add a selection model to handle user selection.
        selectionModel = new SingleSelectionModel<T>();
        list.setSelectionModel(selectionModel);
    }
    
    private void initListeners() {
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler(){

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                final T value = selectionModel.getSelectedObject();
                if (value != null) {
                    final int row = model.getRowIndex(value);
                    final ListSelectionChangedEvent e = fireListSelectionChangedEvent(row);
                    if (e != null && e.isChangeSelection()) {
                        // TODO stop selection  isChangeSelection == false
                        //selectRow(row);
                    }
                }    
            }
            
        });
    }

    public void setModel(final ListModel<T> model) {
        setModel(model, true);
    }

    public void setModel(final ListModel<T> model, final boolean selectFirstRow) {
        this.model = model;

        final List<T> items = model.getItems();
        title.setText(model.getTitle());
        list.setRowCount(items.size());
        list.setRowData(items);
        if (selectFirstRow) {
            fireListSelectionChangedEvent(0);
            selectRow(0);
        }
    }

    public ListModel<T> getModel() {
        return model;
    }

    private ListSelectionChangedEvent fireListSelectionChangedEvent(final int row) {
        if (model == null) {
            return null;
        }

        final T item = model.getItem(row);
        if (item == null) {
            return null;
        }

        ListSelectionChangedEvent event;
        if (row >= 0) {
            event = new ListSelectionChangedEvent(row, item);
        }
        else {
            event = new ListSelectionChangedEvent(-1, null);
        }

        eventBus.fireEvent(event);
        return event;
    }

    /**
     * Selects the given row (relative to the current page).
     * 
     * @param row
     *            the row to be selected
     */
    public void selectRow(final int row) {
        T value = model.getItem(row);
        if (value != null) {
            selectionModel.setSelected(value, true);
        }
    }

    public void clearSelection() {
        T value = selectionModel.getSelectedObject();
        if (value != null) {
            selectionModel.setSelected(value, false);
        }
    }

    public T getSelectedObject() {
        return selectionModel.getSelectedObject();
    }
}