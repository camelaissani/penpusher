package org.penpusher.server.engine.opendocument;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.penpusher.server.engine.OfficeDocument;
import org.penpusher.server.engine.OfficeEngine;

public class OpenDocumentEngine implements OfficeEngine {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(OpenDocumentEngine.class.getName());

    @Override
    public OfficeDocument openDocument(final String sourceFile, final boolean hidden) throws Exception {
        return OpenDocument.createFromFile(sourceFile);
    }

    @Override
    public void saveDocument(final OfficeDocument document, final String destFile) throws Exception {
        LOGGER.info("Save the office document in file: " + destFile);
        final OpenDocument openDocument = (OpenDocument) document;
        ZipFile oldDocument = null;
        ZipOutputStream newDocument = null;
        try {
            oldDocument = new ZipFile(openDocument.getSourceFile());
            FileOutputStream out = new FileOutputStream(destFile);
            newDocument = new ZipOutputStream(out);
            
            // copy contents from existing document
            final Enumeration<? extends ZipEntry> entries = oldDocument.entries();
            final byte[] buffer = new byte[1024];
            ZipEntry e;
            while (entries.hasMoreElements()) {
                e = entries.nextElement();
                final String entryName = e.getName();
                LOGGER.debug("copy: " + entryName);
                if (e.isDirectory()) {
                    newDocument.putNextEntry(e);
                    newDocument.closeEntry();
                }
                else {
                    if (OpenDocumentFile.META_FILE.toString().equals(entryName)) {
                        final byte[] meta = openDocument.getMeta().getBytes("UTF-8");
                        final ZipEntry zipEntry = createZipEntry(entryName, meta);
                        newDocument.putNextEntry(zipEntry);
                        newDocument.write(meta);
                    }
                    else if (OpenDocumentFile.CONTENT_FILE.toString().equals(entryName)) {
                        final byte[] content = openDocument.getContent().getBytes("UTF-8");
                        final ZipEntry zipEntry = createZipEntry(entryName, content);
                        newDocument.putNextEntry(zipEntry);
                        newDocument.write(content);
                    }
                    else {
                        newDocument.putNextEntry(e);
                        int count;
                        final InputStream is = oldDocument.getInputStream(e);
                        while ((count = is.read(buffer)) != -1) {
                            newDocument.write(buffer, 0, count);
                        }
                    }
                    newDocument.closeEntry();
                }
            }
            LOGGER.info("Office document saved");
        } catch (final AccessControlException e) {
        	LOGGER.error("Impossible to save office document. There is a problem with destination file: " + destFile, e);
        } catch (final IOException e) {
            LOGGER.error("Impossible to save office document. There is a problem with destination file: " + destFile, e);
        } finally {
            try {
                if (oldDocument != null) {
                    oldDocument.close();
                }
                if (newDocument != null) {
                    newDocument.close();
                }
            } catch (final IOException e) {
                LOGGER.error("Impossible to close documents", e);
            }
        }
    }

    private static ZipEntry createZipEntry(final String name, final byte[] content) {
        final ZipEntry zipEntry = new ZipEntry(name);
        final CRC32 crc = new CRC32();
        crc.reset();
        crc.update(content);
        zipEntry.setCrc(crc.getValue());
        zipEntry.setSize(content.length);
        return zipEntry;
    }

    @Override
    public void closeDocument(final OfficeDocument document) throws Exception {
        // Clear memory if needed
    }
}
