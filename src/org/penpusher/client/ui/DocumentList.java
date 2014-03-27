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

import org.penpusher.client.data.DocumentDescriptor;
import org.penpusher.client.event.DocumentChangedEvent;
import org.penpusher.client.event.DocumentChangedEventHandler;
import org.penpusher.client.event.ExportDocumentEvent;
import org.penpusher.client.event.NewDocumentEvent;
import org.penpusher.client.event.RemoveDocumentEvent;
import org.penpusher.client.model.DocumentModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class DocumentList extends Composite {
	@UiTemplate("DocumentList.ui.xml")
	interface Binder extends UiBinder<Widget, DocumentList> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final int PAGE_SIZE = 20;

	@UiField(provided = true)
	CellTable<DocumentDescriptor> table = new CellTable<DocumentDescriptor>(
			PAGE_SIZE);
	@UiField
	SimplePager pager;
	@UiField
	ToolBar toolbar;

	private final SimpleEventBus eventBus;
	private final DocumentModel model;
	private final DateTimeFormat dtFormat = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_TIME_SHORT);

	public static DocumentList create(final SimpleEventBus eventBus,
			final DocumentModel model) {
		return new DocumentList(eventBus, model);
	}

	private DocumentList(final SimpleEventBus eventBus,
			final DocumentModel model) {
		this.eventBus = eventBus;
		this.model = model;
		initComponent();
		initListener();
	}

	private void initComponent() {
		initWidget(binder.createAndBindUi(this));

		toolbar.setTitle("Documents");

		pager.setDisplay(table);
		pager.setVisible(false);

		final SingleSelectionModel<DocumentDescriptor> selectionModel = new SingleSelectionModel<DocumentDescriptor>();
		table.setSelectionModel(selectionModel);

		// Create file column.
		final TextColumn<DocumentDescriptor> fileColumn = new TextColumn<DocumentDescriptor>() {
			@Override
			public String getValue(final DocumentDescriptor document) {
				return document.getName();
			}
		};

		// Create date column.
		final TextColumn<DocumentDescriptor> dateColumn = new TextColumn<DocumentDescriptor>() {
			@Override
			public String getValue(final DocumentDescriptor document) {
				return dtFormat.format(document.getCreationDate());
			}
		};

		// Add the columns.
		table.addColumn(dateColumn, "Date");
		table.addColumn(fileColumn, "File");

		// Set the width of each column.
		table.setColumnWidth(dateColumn, 15.0, Unit.PCT);
		table.setColumnWidth(fileColumn, 85.0, Unit.PCT);
	}

	private void initListener() {
		toolbar.setEventBus(eventBus);
		toolbar.addAction(Action.create("+ New", new NewDocumentEvent()),
				Action.create("⇐ Export", new ExportDocumentEvent()),
				// Action.create("* Modify", new ModifyDocumentEvent()),
				Action.create("x Remove", new RemoveDocumentEvent()));
		eventBus.addHandler(DocumentChangedEvent.TYPE,
				new DocumentChangedEventHandler() {

					@Override
					public void onDocumentChanged(
							final DocumentChangedEvent event) {
						final List<DocumentDescriptor> documents = event
								.getDocuments();
						final int rowCount = documents.size();
						ListDataProvider<DocumentDescriptor> dataProvider = new ListDataProvider<DocumentDescriptor>(documents);

					    // Connect the table to the data provider.
					    dataProvider.addDataDisplay(table);

					    table.setRowCount(rowCount, true);

						// Push the data into the widget.
						table.setRowData(0, documents);
						if (rowCount > 0) {
							table.getSelectionModel().setSelected(
									documents.get(0), true);
							model.setSelectedIndex(0);
						} else {
							model.setSelectedIndex(-1);
						}
						pager.setVisible(rowCount > PAGE_SIZE);
					}
				});

		table.getSelectionModel().addSelectionChangeHandler(
				new SelectionChangeEvent.Handler() {

					@Override
					public void onSelectionChange(
							final SelectionChangeEvent event) {
						int index = 0;
						final List<DocumentDescriptor> documents = model
								.getDocuments();
						if (documents != null) {
							for (final DocumentDescriptor document : documents) {
								if (table.getSelectionModel().isSelected(
										document)) {
									model.setSelectedIndex(index);
									return;
								}
								index++;
							}
						}
						model.setSelectedIndex(-1);
					}
				});
	}
}
