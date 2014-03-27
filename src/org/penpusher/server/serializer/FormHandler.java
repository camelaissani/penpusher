package org.penpusher.server.serializer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.penpusher.client.data.Container;
import org.penpusher.client.data.Container.ContainerType;
import org.penpusher.client.data.Form;
import org.penpusher.client.data.FormElement;
import org.penpusher.client.data.WidgetEnum;
import org.penpusher.server.logging.PPLogger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FormHandler extends DefaultHandler {
    private static final Logger LOGGER = Logger.getLogger(FormHandler.class.getName());

    private Form form;
    // Context
    private List<Container> table;
    private final List<Container> currentContainers = new ArrayList<Container>();
    private FormElement element;
    private List<String> values;
    private String tempVal;

    public Form getForm() {
        return form;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("Form")) {
            form = new Form();
            form.setId(attributes.getValue("id"));
            form.setName(attributes.getValue("label"));
            form.setTemplate(attributes.getValue("template"));
        }
        else if (qName.equalsIgnoreCase("table")) {
            table = new ArrayList<Container>();
        }
        else if (qName.equalsIgnoreCase("row")) {
            final Container container = new Container();
            container.setType(ContainerType.ROW);
            container.setLabel(attributes.getValue("label"));
            final String repeat = attributes.getValue("repeat");
            if (repeat != null && repeat.length() > 0) {
                try {
                    container.setRepeat(Integer.parseInt(repeat));
                } catch (final NumberFormatException e) {
                    PPLogger.error(LOGGER, e, "repeat attribute is not an integer");
                }
            }
            addContainer(container);
        }
        else if (qName.equalsIgnoreCase("column")) {
            final Container container = new Container();
            container.setType(ContainerType.COLUMN);
            container.setLabel(attributes.getValue("label"));
            addContainer(container);
        }
        else if (qName.equalsIgnoreCase("textfield")) {
            element = createFormElement(attributes, WidgetEnum.TEXTFIELD);
            addFormElement(element);
        }
        else if (qName.equalsIgnoreCase("textarea")) {
            element = createFormElement(attributes, WidgetEnum.TEXTAREA);
            addFormElement(element);
        }
        else if (qName.equalsIgnoreCase("combobox")) {
            element = createFormElement(attributes, WidgetEnum.COMBOBOX);
            addFormElement(element);
        }
        else if (qName.equalsIgnoreCase("date")) {
            element = createFormElement(attributes, WidgetEnum.DATE);
            addFormElement(element);
        }
        else if (qName.equalsIgnoreCase("values")) {
            values = new ArrayList<String>();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equalsIgnoreCase("form")) {
            if (form != null) {
                form.setContainers(table);
            }
        }
        else if (qName.equalsIgnoreCase("row") || qName.equalsIgnoreCase("column")) {
            final Container container = getCurrentContainer();
            final int lastIndex = currentContainers.size() - 1;
            if (lastIndex >= 0) {
                currentContainers.remove(lastIndex);
            }

            final int repeat = container.getRepeat();
            for (int i = 1; i < repeat; ++i) {
                final Container childContainer = new Container();
                childContainer.setType(container.getType());
                container.setLabel(container.getLabel());

                final int countFormElement = container.getFormElement().size();
                if (countFormElement > 0) {
                    for (final FormElement element : container.getFormElement()) {
                        final FormElement copy = element.copy();
                        copy.setLabel(null);
                        copy.setKey(buildKey(element.getKey(), i));
                        childContainer.addFormElement(copy);
                    }
                }

                // TODO make that containers can be repeated

                addContainer(childContainer);
                currentContainers.remove(lastIndex);
            }

        }
        else if (qName.equalsIgnoreCase("values")) {
            if (element != null) {
                element.setValues(values);
            }
            values = null;
        }
        else if (qName.equalsIgnoreCase("value")) {
            if (values != null) {
                values.add(tempVal);
            }
            else if (element != null) {
                element.setSelectedValue(tempVal);
            }
            tempVal = null;
        }
        else if (qName.equalsIgnoreCase("default")) {
            if (element != null) {
                element.setDefaultValue(tempVal);
            }
            tempVal = null;
        }
        else if (qName.equalsIgnoreCase("format")) {
            if (element != null) {
                element.setFormat(tempVal);
            }
            tempVal = null;
        }
        else if (qName.equalsIgnoreCase("outputformat")) {
            if (element != null) {
                element.setOutputFormat(tempVal);
            }
            tempVal = null;
        }
    }

    private String buildKey(final String key, final int index) {
        final String baseKey = key.substring(0, key.length() - 1);
        final StringBuilder builder = new StringBuilder(baseKey);
        builder.append("_").append(index).append("}");
        return builder.toString();
    }

    private FormElement createFormElement(final Attributes attributes, final WidgetEnum widget) {
        final FormElement element = new FormElement();
        element.setKey(attributes.getValue("key"));
        element.setLabel(attributes.getValue("label"));
        final String optional = attributes.getValue("optional");
        if (optional != null && optional.length() > 0) {
            element.setOptional(Boolean.parseBoolean(optional));
        }
        element.setWidget(widget);
        return element;
    }

    private Container getCurrentContainer() {
        final int lastIndex = currentContainers.size() - 1;
        if (lastIndex >= 0) {
            return currentContainers.get(lastIndex);
        }
        return null;
    }

    private void addContainer(final Container container) {
        if (currentContainers.size() == 0) {
            table.add(container);
            currentContainers.add(container);
        }
        else {
            final Container parent = getCurrentContainer();
            if (parent != null) {
                parent.add(container);
            }
            else {
                PPLogger.error(LOGGER, "Can not add child container in null parent container");
            }
            currentContainers.add(container);
        }
    }

    private void addFormElement(final FormElement element) {
        final Container container = getCurrentContainer();
        if (container != null) {
            container.addFormElement(element);
        }
        else {
            PPLogger.error(LOGGER, "Can not add form element in null container");
        }
    }
}
