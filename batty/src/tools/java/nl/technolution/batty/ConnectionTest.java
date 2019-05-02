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

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.XStorageConnection;

/**
 * 
 */
public class ConnectionTest {

    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

        BattyConfig b = new BattyConfig("", "https://172.30.133.212/assets/inc/server.inc.php", "batty", "batty",
                "src/main/resource/truststore.jks", "12345678", null);

        XStorageConnection conn = new XStorageConnection();
        conn.init(b);

        // conn.powerOff();

        conn.powerOn();

        // MachineInfo machineInfo = conn.getMachineInfo();
        // System.out.println(machineInfo.toString());

        // BmsData bmsData = conn.getBmsData();
        // System.out.println(bmsData.toString());

        // conn.discharge(0);
        conn.charge(100);
        System.out.println(conn.getMachineData().toString().replaceAll(",", "\n"));
    }
}
