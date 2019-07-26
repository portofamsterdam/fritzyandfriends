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

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

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

    /**
     * Convert XML duration to Java 8 {@link Duration}
     * 
     * @param xmlDuration
     * @return
     */
    public static Duration fromXmlDuration(javax.xml.datatype.Duration xmlDuration) {
        return Duration.ofMillis(xmlDuration.getTimeInMillis(new Date()));
    }
}
