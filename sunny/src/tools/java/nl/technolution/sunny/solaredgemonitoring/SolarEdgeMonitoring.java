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
package nl.technolution.sunny.solaredgemonitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.EnergyToMeasurement;
import nl.technolution.sunny.pvcast.client.IPvCastClient;
import nl.technolution.sunny.pvcast.client.PvCastClient;
import nl.technolution.sunny.pvcast.model.Forecasts;
import nl.technolution.sunny.pvcast.model.PvMeasurements;
import nl.technolution.sunny.solaredgemonitoring.client.ISolarEdgeMonitoringClient;
import nl.technolution.sunny.solaredgemonitoring.client.SolarEdgeMonitoringClient;
import nl.technolution.sunny.solaredgemonitoring.model.SiteEnergy;

/**
 * Tool for testing with SolarEdge monitoring API
 */
public final class SolarEdgeMonitoring {
    private static final Logger LOG = LoggerFactory.getLogger(SolarEdgeMonitoring.class);

    private SolarEdgeMonitoring() {
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // NOTE WHO: Needed for request logging (see also nl.technolution.sunny.pvcast.client.PvCastClient.init)
        SLF4JBridgeHandler.install();

        SunnyConfig config = new SunnyConfig("deviceId", "host", null, 0,
                "https://monitoringapi.solaredge.com/site/529405", "JRK97634IPJD9ABBG4MFACJVZGLK4NUN",
                "https://api.pvcast.de/plants/908", "cxDhZtryzwyGHG2yMzqy");
        ISolarEdgeMonitoringClient client = new SolarEdgeMonitoringClient();
        client.init(config);

        SiteEnergy siteEnergy = client.getHourlyEnergy(21);
        LOG.info("Received object:\n" + siteEnergy);

        PvMeasurements pvMeasurements = EnergyToMeasurement.energyToMeasurements(siteEnergy);
        LOG.info("Converted object:\n" + pvMeasurements);

        IPvCastClient pvCastClient = new PvCastClient();
        pvCastClient.init(config);
        pvCastClient.postPvMeasurements(pvMeasurements);
        Forecasts forecasts = pvCastClient.getPvForecasts();
        LOG.info("Received forecast object:\n" + forecasts);

    }
}
