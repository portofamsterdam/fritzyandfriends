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

import java.net.InetAddress;

/**
 * Tool for testing SolarEdge modbus connection
 */
public final class SolarEdgeTester {

    private SolarEdgeTester() {
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName("192.168.8.240");
        int port = 502;
        int deviceId = 2;

        SolarEdgeSession session = new SolarEdgeSession();
        session.init(address, port, deviceId);

        try {
            System.out.println("Power: " + session.getInverterPower());
        } finally {
            session.stop();
        }
    }
}
