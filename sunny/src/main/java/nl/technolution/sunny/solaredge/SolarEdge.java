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
package nl.technolution.sunny.solaredge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.technolution.sunny.solaredge.beans.DevicesRequest;
import nl.technolution.sunny.solaredge.beans.DevicesResponse;

/**
 * 
 */
public class SolarEdge {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        DevicesRequest req = new DevicesRequest();
        req.setVersion("1.0");
        req.setId(UUID.randomUUID().toString());
        req.setFormat("JSON");

        String json = mapper.writeValueAsString(req);
        System.out.println(json);

        URL url = new URL("http://192.168.8.240:502/rpc");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(1000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream out = connection.getOutputStream()) {


            String reqBody = String.format("RPC=%s", json);

            out.write(reqBody.getBytes("UTF-8"));
            out.flush();
            

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Noooo");
            }
            
            try (InputStream in = connection.getInputStream()) {
            DevicesResponse value = mapper.readValue(in, DevicesResponse.class);
                System.out.println(value.getResult().getTotalDevicesReturned());
            }
            

        } finally {
            connection.disconnect();
        }
    }
}
