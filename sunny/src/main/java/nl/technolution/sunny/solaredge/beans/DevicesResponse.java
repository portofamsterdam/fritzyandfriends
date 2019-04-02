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
 * Class defines RPC response
 */
public final class DevicesResponse extends Rpc implements IRpcResponse {
    private Result result = new Result();   

    public Result getResult() {
        return result;
    }    
    
    /**
     * Class defines RPC Response result 
     */
    public final class Result {
        private List<Device<String>> devices = new ArrayList<Device<String>>();
        private int totalDevicesReturned;

        public List<Device<String>> getDevices() {
            return devices;
        }

        public int getTotalDevicesReturned() {
            return totalDevicesReturned;
        }

        public void setTotalDevicesReturned(int totalDevicesReturned) {
            this.totalDevicesReturned = totalDevicesReturned;
        }
    }

}