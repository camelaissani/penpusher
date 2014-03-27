package org.penpusher.server.engine.opendocument;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.penpusher.client.data.FormElement;
import org.penpusher.server.StringUtils;
import org.penpusher.server.engine.OfficeDocument;

public class OpenDocument implements OfficeDocument {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(OpenDocument.class.getName());
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    private String sourceFile;
    private String meta;
    private String content;

    public String getSourceFile() {
        return sourceFile;
    }

    private void setSourceFile(final String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getMeta() {
        return meta;
    }

    private void setMeta(final String meta) {
        this.meta = meta;
    }

    public String getContent() {
        return content;
    }

    private void setContent(final String content) {
        this.content = content;
    }

    public static OfficeDocument createFromFile(final String sourceFile) {
        LOGGER.info("Create an office document from source file: " + sourceFile);
        final OpenDocument document = new OpenDocument();
        ZipFile zip = null;
        try {
            zip = new ZipFile(sourceFile);
            final ZipEntry metaEntry = zip.getEntry(OpenDocumentFile.META_FILE.toString());
            final ZipEntry contentEntry = zip.getEntry(OpenDocumentFile.CONTENT_FILE.toString());
            document.setSourceFile(sourceFile);
            document.setMeta(getEntryContent(zip, metaEntry));
            document.setContent(getEntryContent(zip, contentEntry));
            zip.close();

            LOGGER.info("Office document created");
        } catch (final IOException e) {
            LOGGER.error("Impossible to create office document. There is a problem with source file: " + sourceFile, e);
            return null;
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (final IOException e) {
                    LOGGER.error("Impossible to close zip input.", e);
                }
            }
        }
        return document;
    }

    private static String getEntryContent(final ZipFile zip, final ZipEntry entry) throws IOException {
        final InputStream is = zip.getInputStream(entry);
        final Writer writer = new StringWriter();
        int count;
        final byte[] buffer = new byte[1024];
        while ((count = is.read(buffer)) != -1) {
            writer.write(new String(buffer, 0, count, "UTF-8"));
        }
        is.close();
        return writer.toString();
    }

    private void updateMeta() throws ParseException {
        // compute current date
        final String currentDate = DATEFORMAT.format(Calendar.getInstance().getTime());
        meta = StringUtils.replaceXmlElement(OpenDocumentFile.CREATION_DATE_TAG.toString(), currentDate, meta);
        meta = StringUtils.replaceXmlElement(OpenDocumentFile.MODIFICATION_DATE_TAG.toString(), currentDate, meta);
        // TODO: set login name
        meta = StringUtils.replaceXmlElement(OpenDocumentFile.CREATOR_TAG.toString().toString(), "penpusher", meta);
        meta =
                StringUtils.replaceXmlElement(OpenDocumentFile.DESCRIPTION_TAG.toString(), "Created with penpusher",
                        meta);
        LOGGER.debug(meta);
    }

    @Override
    public void replace(final List<FormElement> elements) {
        try {
            updateMeta();
        } catch (final ParseException e) {
            LOGGER.error("Can not update meta.xml file", e);
        }

        for (final FormElement element : elements) {
            content = StringUtils.replace(element.getKey(), element.getSelectedValue(), content);
        }
    }
}
