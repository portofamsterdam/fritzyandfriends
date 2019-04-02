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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MeterInfo;

/**
 * Connection to Nissan xStorage device
 */
public class XStorageConnection {

    private final ObjectMapper mapper = new ObjectMapper();

    private final InetAddress address;
    private String username;
    private String password;

    /**
     * https://172.30.133.212/Dashboard.php
     * 
     * Constructor for {@link XStorageConnection} objects
     *
     * @param address where to find the battery
     */
    public XStorageConnection(InetAddress address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    /**
     * @return machine info
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public MachineInfo getMachineInfo() throws JsonParseException, JsonMappingException, IOException {
        InputStream src = null;
        ApiResult result = mapper.readValue(src, ApiResult.class);
        String[] data = result.getData();
        return MachineInfo.fromData(data);
    }

    /**
     * @return machine data
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public MachineData getMachineData() throws JsonParseException, JsonMappingException, IOException {
        InputStream src = null;
        ApiResult result = mapper.readValue(src, ApiResult.class);
        String[] data = result.getData();
        return MachineData.fromData(data);
    }

    /**
     * 
     * @param percetage
     */
    public void charge(int percetage) {

    }

    /**
     * 
     * @param percentage
     */
    public void discharge(int percentage) {

    }

    /**
     * 
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public MeterInfo getMeterInfo() throws JsonParseException, JsonMappingException, IOException {
        InputStream src = null;
        ApiResult result = mapper.readValue(src, ApiResult.class);
        String[] data = result.getData();
        return MeterInfo.fromData(data[0]);
    }
}
