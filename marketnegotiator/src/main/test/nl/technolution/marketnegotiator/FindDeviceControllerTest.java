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
package nl.technolution.marketnegotiator;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import nl.technolution.appliance.IDeviceControler;
import nl.technolution.core.resources.TypeFinder;

/**
 * Test if a device controller can be found
 */
public class FindDeviceControllerTest {

    @Test
    public void findDeviceControllerTest() {
        List<Class<? extends IDeviceControler>> classes = TypeFinder.findImplementingClasses("nl.technolution",
                IDeviceControler.class);
        assertFalse(classes.isEmpty());
    }

}
