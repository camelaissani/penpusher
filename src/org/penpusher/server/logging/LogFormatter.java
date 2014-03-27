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
package org.penpusher.server.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 
 * @author camel.aissani(at)gmail.com
 */
public class LogFormatter extends Formatter {
	private final static String FIELD_SEPARATOR = "| ";
	private final static String METHOD_SEPARATOR = "::";

	private final Date logDate = new Date();
	private String lineSeparator = 
		(String) java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));
	
	/**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
    	final StringBuilder buffer = new StringBuilder();
    	final DateFormat df = SimpleDateFormat.getInstance();
    	logDate.setTime(record.getMillis());
    	buffer.append(df.format(logDate)).append(FIELD_SEPARATOR);
    	buffer.append(record.getLevel().getLocalizedName()).append(FIELD_SEPARATOR);
    	if (record.getSourceClassName() != null) {	
    		buffer.append(record.getSourceClassName());
    	} else {
    		buffer.append(record.getLoggerName());
    	}
    	if (record.getSourceMethodName() != null) {	
    		buffer.append(METHOD_SEPARATOR);
    		buffer.append(record.getSourceMethodName());
    	}
    	buffer.append(FIELD_SEPARATOR);
    	buffer.append(formatMessage(record));
    	buffer.append(lineSeparator);
		if (record.getThrown() != null) {
		    //try {
		        final StringWriter sw = new StringWriter();
		        final PrintWriter pw = new PrintWriter(sw);
		        record.getThrown().printStackTrace(pw);
		        pw.close();
		        buffer.append(sw.toString());
		    //} catch (Exception ex) { }
		}
		return buffer.toString();
    }
}
