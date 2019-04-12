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
package nl.technolution.batty;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import nl.technolution.batty.xstorage.XStorageConnection;
import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;

/**
 * 
 */
public class ConnectionTest {

    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        XStorageConnection conn = new XStorageConnection("https://172.30.133.212/assets/inc/server.inc.php", "batty",
                "batty");
        conn.init("src/main/resource/truststore.jks", "12345678");

        // conn.powerOff();

        MachineInfo machineInfo = conn.getMachineInfo();
        System.out.println(machineInfo.toString());
        MachineData machineData = conn.getMachineData();
        System.out.println(machineData.toString());

        BmsData bmsData = conn.getBmsData();
        System.out.println(bmsData.toString());

        // conn.powerOn();
        // conn.discharge(0);
        // conn.charge(100);
    }
}
