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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.StorageRegistration;

/**
 * Test Efi functions
 */
public class EfiTest {

    /**
     * Test next quarter
     */
    @Test
    public void testDates() {
        Instant nowInNextQuarter = Instant.now().plus(15, ChronoUnit.MINUTES);
        Instant quarter = Efi.getNextQuarter();
        assertEquals(0L, quarter.toEpochMilli() % 900000L);

        assertEquals(LocalDateTime.ofInstant(quarter, ZoneId.systemDefault()).get(ChronoField.HOUR_OF_DAY),
                LocalDateTime.ofInstant(nowInNextQuarter, ZoneId.systemDefault()).get(ChronoField.HOUR_OF_DAY));
        assertEquals(LocalDateTime.ofInstant(quarter, ZoneId.systemDefault()).get(ChronoField.DAY_OF_MONTH),
                LocalDateTime.ofInstant(nowInNextQuarter, ZoneId.systemDefault()).get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void buildEfiClass() {
        DeviceId deviceId = new DeviceId("testdevice");
        long before = new Date().getTime();
        StorageRegistration registration = Efi.build(StorageRegistration.class, deviceId);
        long after = new Date().getTime();

        assertEquals(deviceId.getDeviceId(), registration.getHeader().getEfiResourceId());
        Date createdTs = registration.getHeader().getTimestamp().toGregorianCalendar().getTime();
        assertTrue(createdTs.getTime() >= before && createdTs.getTime() <= after);

        assertEquals(deviceId, Efi.getDeviceId(registration));
    }

}
