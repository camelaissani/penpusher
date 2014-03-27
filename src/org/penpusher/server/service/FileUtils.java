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
package org.penpusher.server.service;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author camel.aissani(at)gmail.com
 * 
 */
public final class FileUtils {
    private FileUtils() {}

    public static synchronized String buildFilePath(final String filepath, final String filename, final String extension) {
        final StringBuilder builder = new StringBuilder(filepath);
        if (!filepath.endsWith(File.separator)) {
            builder.append(File.separator);
        }
        builder.append(filename).append(extension);
        return builder.toString();
    }

    public static synchronized String buildPath(final String... pathes) {
        final StringBuilder builder = new StringBuilder();
        final int length = pathes.length;
        int index = 0;
        for (final String path : pathes) {
            if (index != 0 && path.startsWith(File.separator)) {
                builder.append(path.substring(1));
            }
            else {
                builder.append(path);
            }
            if (index != length - 1 && !path.endsWith(File.separator)) {
                builder.append(File.separator);
            }
            index++;
        }
        return builder.toString();
    }

    public static String getExtension(final String filename) {
        if (filename == null || filename.length() == 0) {
            throw new IllegalArgumentException("file name must be not null");
        }
        final int index = filename.lastIndexOf('.');
        final String ext = filename.substring(index);
        if (ext == null || ext.length() == 0) {
            throw new IllegalArgumentException("file name has not extension");
        }
        return ext;
    }

    public static String buildUrl(final HttpServletRequest request, final String URI) {
        final StringBuilder builder = new StringBuilder();
        if (request.isSecure()) {
            builder.append("https://");
        }
        else {
            builder.append("http://");
        }
        builder.append(request.getServerName());
        final int port = request.getServerPort();
        if (port > 0 && port != 80) {
            builder.append(':').append(port);
        }
        if (!URI.startsWith("/")) {
            builder.append('/');
        }
        builder.append(URI);
        return builder.toString();
    }
}
