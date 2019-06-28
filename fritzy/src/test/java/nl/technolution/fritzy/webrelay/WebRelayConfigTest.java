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
package nl.technolution.fritzy.webrelay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import nl.technolution.fritzy.app.FritzyConfig;

/**
 * 
 */
public class WebRelayConfigTest {

    /**
     * Test if object can be written as String
     * 
     * @throws JsonProcessingException
     */
    @Test
    public void testConfig() throws JsonProcessingException {
        FritzyConfig obj = new FritzyConfig("Fritzy", "localhost", 80, "/dev/tty", false, false, 0d, 0d, 0);
        System.out.println(
                new ObjectMapper().writeValueAsString(obj));
    }
}
