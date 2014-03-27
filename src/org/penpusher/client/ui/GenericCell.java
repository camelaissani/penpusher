package org.penpusher.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;

/**
 * A {@link Cell} used to render text.
 */
public class GenericCell<T> extends AbstractCell<T> {
    private AbstractSafeHtmlRenderer<T> renderer = new AbstractSafeHtmlRenderer<T>() {
        @Override
        public SafeHtml render(T object) {
            return toSafeHtml(object);
        }
    };

    @Override
    public void render(Context context, T value, SafeHtmlBuilder sb){
        renderer.render(value, sb);
    }
}