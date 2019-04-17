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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import nl.technolution.batty.xstorage.types.EWarningType1;
import nl.technolution.batty.xstorage.types.EWarningType2;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineDataJson;
import nl.technolution.batty.xstorage.types.Mode;

/**
 * Test parsing response with machine data
 */
public class MachineDataTest {

    /**
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * 
     */
    @Test
    public void parseResultTest() throws JsonParseException, JsonMappingException, IOException {
        String testResult = "{\"data\":[" +
                "[50,48,49,57,45,48,52,45,48,53,32,48,56,58,51,57,58,51,50]," + // Time 2019-04-05 08:39:32
                "[82,66,55,55,72,52,54,48,50,52,0,0,0,0,0,0]," + // Serial: RB77H46024
                "[94,1,0,0]," + // temp 35 deg
                "[0,0,0,0]," + // Vpv1
                "[0,0,0,0]," +
                "[216,3,0,0]," + // VBatSys: 98.4
                "[88,213,255,255]," + // IBatSys: -10.92
                "[100,0,0,0]," +
                "[255,255,0,0]," + // MTF 65535
                "[255,0,0,0]," + // MTE 255
                "[0,0,0,0]," +
                "[216,8,0,0]," + // Vac: 2264d
                "[0,0,0,0]," +
                "[0,0,0,0]," +
                "[0,0,0,0]," +
                "[0,0,0,0]," +
                "[0,0,0,0]," +
                "[138,19,0,0]," + // FAC 50.02
                "[0,0,0,0]," +
                "[0,0,0,0]," +
                "[22,0,0,0]," + // E_Total 2.2
                "[1,0,0,0]," + // H_Total: 1
                "[4,0,0,0]," + // Errorcode 34
                "[0,16,0,1]," + // 16781312
                "[16,1,0,0]," +
                "[1,0,0,0]," +
                "[0,0,0,0]," +
                "[20,0,0,0]]}";
        MachineDataJson dataJson = new ObjectMapper().readValue(testResult, MachineDataJson.class);

        MachineData data = MachineData.fromData(dataJson.getData());
        assertEquals("2019-04-05 08:39:32", data.getTime());
        assertEquals("RB77H46024", data.getSerial());
        assertEquals(35.0d, data.getTemperature(), 0.0001d);
        assertEquals(0, data.getVpv1(), 0.0001d);
        assertEquals(0, data.getIpv1(), 0.0001d);
        assertEquals(98.4d, data.getvBatSys(), 0.0001d);
        assertEquals(-10.92d, data.getiBatSys(), 0.0001d);
        assertEquals(100, data.getSoc());
        assertEquals(65535, data.getmTF());
        assertEquals(255, data.getmTE());
        assertEquals(0, data.getIac(), 0.0001d);
        assertEquals(2264d, data.getVac(), 0.0001d);
        assertEquals(0, data.getgVFaultValue(), 0.0001d);
        assertEquals(0, data.getTmpFaultValue(), 0.0001d);
        assertEquals(0, data.getpV1FaultValue(), 0.0001d);
        assertEquals(0, data.getPac(), 0.0001d);
        assertEquals(0, data.geteToday(), 0.0001d);
        assertEquals(50.02, data.getFac(), 0.0001d);
        assertEquals(0, data.getgFFaultValue(), 0.0001d);
        assertEquals(0, data.getgFCIFaultValue(), 0.0001d);
        assertEquals(2.2, data.geteTotal(), 0.0001d);
        assertEquals(1, data.gethTotal());
        assertTrue(data.getWarning1().contains(EWarningType1.ErrorCode34));
        assertEquals(1, data.getWarning1().size());
        assertTrue(data.getWarning2().contains(EWarningType2.MSVERSIONFAIL));
        assertTrue(data.getWarning2().contains(EWarningType2.OVERPOWER));
        assertEquals(2, data.getWarning2().size());
        assertEquals(272, data.getbMS());
        assertEquals(Mode.getModeByInt(1), data.getMode());
        assertEquals(0, data.getPload(), 0.0001d);
        assertEquals(2.0d, data.geteDraw(), 0.0001d);

        assertEquals(1, Mode.getModeByInt(1).hashCode());
    }

}
