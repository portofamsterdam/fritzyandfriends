/*
 (C) COPYRIGHT TECHNOLUTION BV, GOUDA NL
| =======          I                   ==          I    =
|    I             I                    I          I
|    I   ===   === I ===  I ===   ===   I  I    I ====  I   ===  I ===
|    I  /   \ I    I/   I I/   I I   I  I  I    I  I    I  I   I I/   I
|    I  ===== I    I    I I    I I   I  I  I    I  I    I  I   I I    I
|    I  \     I    I    I I    I I   I  I  I   /I  \    I  I   I I    I
|    I   ===   === I    I I    I  ===  ===  === I   ==  I   ===  I    I
|                 +---------------------------------------------------+
+----+            |  +++++++++++++++++++++++++++++++++++++++++++++++++|
     |            |             ++++++++++++++++++++++++++++++++++++++|
     +------------+                          +++++++++++++++++++++++++|
                                                        ++++++++++++++|
                                                                 +++++|
 */
package nl.technolution.protocols.efi.util;

import java.time.Instant;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Xml utils
 */
public final class XmlUtils {

    private XmlUtils() {
        // hide
    }

    /**
     * Convert XML calendar to {@link Instant}
     * 
     * @param calendar to convert
     * @return instant instance
     */
    public static Instant fromXmlCalendar(XMLGregorianCalendar calendar) {
        return Instant.ofEpochMilli(calendar.toGregorianCalendar().getTimeInMillis());
    }
}
