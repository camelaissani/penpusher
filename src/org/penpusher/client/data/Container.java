package org.penpusher.client.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Container implements Serializable {

    private static final long serialVersionUID = 1789157749152701184L;

    public static enum ContainerType {
        ROW, COLUMN
    };

    private ContainerType type;
    private String label;
    private int repeat = 0;
    private List<Container> containers;
    private List<FormElement> elements;

    public ContainerType getType() {
        return type;
    }

    public void setType(final ContainerType type) {
        this.type = type;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(final int repeat) {
        this.repeat = repeat;
    }

    public void add(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Container is null");
        }
        if (containers == null) {
            containers = new ArrayList<Container>();
        }
        containers.add(container);
    }

    public List<Container> getContainers() {
        if (containers == null) {
            return Collections.emptyList();
        }
        return containers;
    }

    public void addFormElement(final FormElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null");
        }
        if (elements == null) {
            elements = new ArrayList<FormElement>();
        }
        elements.add(element);
    }

    public List<FormElement> getFormElement() {
        if (elements == null) {
            return Collections.emptyList();
        }
        return elements;
    }

    public List<FormElement> getAllFormElement() {
        final List<FormElement> elements = new ArrayList<FormElement>();
        elements.addAll(getFormElement());
        if (containers != null) {
            for (final Container container : containers) {
                elements.addAll(container.getAllFormElement());
            }
        }
        return elements;
    }

    public Container copy() {
        final Container container = new Container();
        container.setType(type);
        container.setLabel(label);
        container.setRepeat(repeat);
        if (containers != null) {
            container.containers = new ArrayList<Container>(containers.size());
            for (final Container c : containers) {
                container.containers.add(c.copy());
            }
        }
        if (elements != null) {
            container.elements = new ArrayList<FormElement>(elements.size());
            for (final FormElement e : elements) {
                container.elements.add(e.copy());
            }
        }
        return container;
    }

}
