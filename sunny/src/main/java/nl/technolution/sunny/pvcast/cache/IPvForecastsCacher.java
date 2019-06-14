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
package nl.technolution.sunny.pvcast.cache;

import java.io.IOException;

import nl.technolution.dropwizard.services.IService;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.model.Forecasts;

/**
 * 
 */
public interface IPvForecastsCacher extends IService<SunnyConfig> {

    /**
     * Update cached value
     * 
     * @throws IOException
     */
    void update() throws IOException;

    /**
     * Get cached value
     * 
     * @return cached value
     */
    Forecasts getPvForecasts();
}
