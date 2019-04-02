/*
 (C) COPYRIGHT 2015 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredge.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Class defines RPC request for GetProcessData call
 */
public final class ProcessDataRequest extends Rpc implements IRpcRequest {
    private final String proc = "GetProcessData";
    private Params params = new Params();

    public String getProc() {
        return proc;
    }
    
    public Params getParams() {
        return params;
    }    
    
    /**
     * Params for process data request 
     */
    public final class Params {
        private List<Device<String>> devices = new ArrayList<Device<String>>();

        public List<Device<String>> getDevices() {
            return devices;
        }
    }
}