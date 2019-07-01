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
import java.net.UnknownHostException;

import nl.technolution.sunny.app.SunnyConfig;

/**
 * 
 */
public class SESessionFactory implements ISESessionFactory {

    private ISolarEdgeSession solarEdgeSession;

    @Override
    public void init(SunnyConfig config) {
        if (config.isUseStub()) {
            solarEdgeSession = new SolarEdgeSessionStub();
            return;
        } else {
            SolarEdgeSession session = new SolarEdgeSession();
            try {
                session.init(InetAddress.getByName(config.getSolarEdgeModbusIpAddress()),
                        config.getSolarEdgeModbusPort(), config.getSolarEdgeModbusDeviceId());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            solarEdgeSession = session;
        }
    }

    @Override
    public ISolarEdgeSession getSESession() {
        return solarEdgeSession;
    }

    // TODO WHO: enable Override once it is added to IService
    // @Override
    public void deInit() {
        solarEdgeSession.stop();
    }
}
