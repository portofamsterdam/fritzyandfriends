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
package nl.technolution.sunny.pvcast;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.ITaskRunner;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.sunny.pvcast.client.IPvCastClient;
import nl.technolution.sunny.pvcast.model.PvMeasurements;
import nl.technolution.sunny.solaredgemonitoring.client.ISolarEdgeMonitoringClient;
import nl.technolution.sunny.solaredgemonitoring.model.SiteEnergy;

/**
 * Retrieve measurements from SolarEdge monitoring portal and upload these to PVCast once a day (at 13 minutes offset to
 * balance the load on the pvcast server).
 * 
 */
@TimedTask(period = 1, unit = TimeUnit.DAYS, offset = 13, offsetUnit = TimeUnit.MINUTES)
public class UploadMeasurementsTask implements ITaskRunner {
    private static final Logger LOG = Log.getLogger();

    @Override
    public void execute() {
        SiteEnergy siteEnergy = Services.get(ISolarEdgeMonitoringClient.class).getHourlyEnergy(21);
        PvMeasurements pvMeasurements = EnergyToMeasurement.energyToMeasurements(siteEnergy);
        Services.get(IPvCastClient.class).postPvMeasurements(pvMeasurements);
        LOG.info("UploadMeasurementsTask executed.");
    }
}
