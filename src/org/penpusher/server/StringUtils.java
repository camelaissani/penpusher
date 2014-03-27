package org.penpusher.server;

import java.text.ParseException;

public final class StringUtils {

    public static String replace(final String key, final String value, final String text) {
        final int index = text.indexOf(key);
        if (index == -1) {
            return text == null ? "" : text;
        }
        if (key.equals(text)) {
            return value == null ? "" : value;
        }
        final int textLength = text.length();
        final int keyLength = key.length();
        final int lastIndex = index + keyLength;
        final StringBuilder builder = new StringBuilder(text.substring(0, index));
        builder.append(value);
        if (lastIndex < textLength) {
            builder.append(text.substring(lastIndex, textLength));
        }
        return replace(key, value, builder.toString());
    }

    public static String replaceXmlElement(final String tag, final String value, final String xml)
            throws ParseException {
        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("tag can not be null or empty");
        }
        if (!tag.startsWith("<") || !tag.endsWith(">")) {
            throw new IllegalArgumentException("tag must be surround with '<' and '>'");
        }
        final String endTag = tag.replace("<", "</");
        return replaceXmlElement(tag, endTag, value, 0, xml);
    }

    private static String replaceXmlElement(final String beginTag, final String endTag, final String value,
            final int fromIndex, final String xml) throws ParseException {
        final int beginIndex = xml.indexOf(beginTag, fromIndex);
        if (beginIndex < 0) {
            return xml;
        }
        final int beginTagLength = beginTag.length();
        final int endIndex = xml.indexOf(endTag, beginIndex + beginTagLength);
        if (endIndex < 0) {
            throw new ParseException("End tag " + beginTag + " not found", beginIndex);
        }
        final StringBuilder builder = new StringBuilder(xml.substring(0, beginIndex + beginTagLength));
        System.out.println(Thread.currentThread().getName() + " " + System.identityHashCode(builder));
        builder.append(value);
        builder.append(xml.substring(endIndex, xml.length()));
        final int endTagLength = beginTagLength + 1;
        return replaceXmlElement(beginTag, endTag, value, fromIndex + beginTagLength + endTagLength + value.length(),
                builder.toString());
    }
}
