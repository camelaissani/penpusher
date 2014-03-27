package org.penpusher.server.engine.opendocument;
public enum OpenDocumentFile {
    META_FILE("meta.xml"), CONTENT_FILE("content.xml"), CREATOR_TAG("<dc:creator>"), CREATION_DATE_TAG(
            "<meta:creation-date>"), MODIFICATION_DATE_TAG("<dc:date>"), DESCRIPTION_TAG("<dc:description>");

    private final String name;

    private OpenDocumentFile(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}