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
package nl.technolution.batty.xstorage;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.BmsDataJson;

/**
 * Test parsing response with machine data
 */
public class BmsDataTest {

    /**
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * 
     */
    @Test
    public void parseResultTest() throws JsonParseException, JsonMappingException, IOException {
        String testResult = "{\"data\":[9,56,9,46,0,213,0,203,0,192]}";
        BmsDataJson dataJson = new ObjectMapper().readValue(testResult, BmsDataJson.class);

        BmsData data = BmsData.fromData(dataJson.getData());
        assertEquals(3540d, data.getHighestCellVoltage(), 0.0001d);
        assertEquals(3525d, data.getLowestCellVoltage(), 0.0001d);
        assertEquals(21.3d, data.getSystemMaxTemparature(), 0.0001d);
        assertEquals(20.3d, data.getSystemAvgTemparature(), 0.0001d);
        assertEquals(19.2d, data.getSystemMinTemparature(), 0.0001d);

    }

}
