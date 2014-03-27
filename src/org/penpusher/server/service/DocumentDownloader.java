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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.penpusher.server.FolderContext;
import org.penpusher.server.WebApplication;
import org.penpusher.server.logging.PPLogger;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
@SuppressWarnings("serial")
public class DocumentDownloader extends HttpServlet {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(DocumentDownloader.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {
        final String path = req.getParameter("path").toString();
        final String filename = req.getParameter("filename").toString();
        PPLogger.info(LOGGER, "Download file :", filename);

        ServletOutputStream out = null;
        InputStream in = null;
        try {
            final WebApplication application = WebApplication.getInstance();
            final FolderContext context = application.getFolderContext();
            final String filepath = FileUtils.buildPath(context.getDocumentsFolder(), path, filename);

            PPLogger.info(LOGGER, "Retrieving file from :", filepath);

            final File f = new File(filepath);
            resp.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(f));
            resp.setContentLength((int) f.length());
            resp.setCharacterEncoding("UTF-8");
            final StringBuffer headerBuffer = new StringBuffer("attachment; filename=\"");
            headerBuffer.append(URLEncoder.encode(filename, "UTF-8")).append("\"");
            resp.setHeader("Content-Disposition", headerBuffer.toString());

            final byte[] bbuf = new byte[1024];
            int length = 0;
            in = new DataInputStream(new FileInputStream(f));
            out = resp.getOutputStream();
            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                out.write(bbuf, 0, length);
            }
            out.flush();

        } catch (final IOException e) {
            PPLogger.error(LOGGER, e, "Impossible to initiate the document download");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
                PPLogger.info(LOGGER, "Download complete");
            } catch (final IOException e) {
            }
        }
    }
}