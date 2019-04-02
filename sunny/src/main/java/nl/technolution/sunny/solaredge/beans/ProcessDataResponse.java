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
public final class ProcessDataResponse extends Rpc implements IRpcResponse {
    private Result result = new Result();   

    public Result getResult() {
        return result;
    }    
    
    /**
     * Class defines RPC Response result 
     */
    public final class Result {
        private List<Device<Channel>> devices = new ArrayList<Device<Channel>>();

        public List<Device<Channel>> getDevices() {
            return devices;
        }
    }

}