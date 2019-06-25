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
package nl.technolution.sunny.solaredgemonitoring.client;

import nl.technolution.dropwizard.services.IService;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.solaredgemonitoring.model.SiteEnergy;

/**
 * Defines SolarEdgeMonitoringClient interface
 */
public interface ISolarEdgeMonitoringClient extends IService<SunnyConfig> {

    /**
     * Get hour energy values for the last x days
     * 
     * @return
     */
    SiteEnergy getHourlyEnergy(int nrOfDays);
}