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
package nl.technolution.sunny.pvcast.client;

import nl.technolution.dropwizard.services.IService;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.model.Forecasts;
import nl.technolution.sunny.pvcast.model.PvMeasurements;

/**
 * Defines SolarEdgeMonitoringClient interface
 */
public interface IPvCastClient extends IService<SunnyConfig> {

    /**
     * @param pvMeasurements
     */
    void postPvMeasurements(PvMeasurements pvMeasurements);

    /**
     * @param forecasts
     * @return
     */
    Forecasts getPvForecasts();
}