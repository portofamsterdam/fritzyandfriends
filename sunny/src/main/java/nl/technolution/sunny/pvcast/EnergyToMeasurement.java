/*
 (C) COPYRIGHT 2019 TECHNOLUTION BV, GOUDA NL
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

import java.time.ZoneId;

import com.google.common.base.Preconditions;

import nl.technolution.sunny.pvcast.model.Measurements;
import nl.technolution.sunny.pvcast.model.Power;
import nl.technolution.sunny.pvcast.model.PvMeasurements;
import nl.technolution.sunny.solaredgemonitoring.model.SiteEnergy;
import nl.technolution.sunny.solaredgemonitoring.model.Value;

/**
 * Helper class for converting entities
 */
public final class EnergyToMeasurement {

    // NOTE WHO: Timezone can be obtained from the API using the 'Site List' endpoint (field 'timeZone') but for
    // simplicity it is defined here.
    private static final ZoneId SITE_TIME_ZONE = ZoneId.of("Europe/Amsterdam");

    private EnergyToMeasurement() {
    }

    /**
     * Create a PvMeasurement object from a SiteEnergy object from the SolarEdge monitoring portal to a PvMeasurement
     * object for the PVCAST server
     * 
     * @param siteEnergy
     * @return
     */
    public static PvMeasurements energyToMeasurements(SiteEnergy siteEnergy) {
        // some sanity checks on the received data
        Preconditions.checkArgument(siteEnergy.getEnergy().getTimeUnit().compareTo("HOUR") == 0);
        Preconditions.checkArgument(siteEnergy.getEnergy().getUnit().compareTo("Wh") == 0);

        PvMeasurements pvMeasurements = new PvMeasurements();
        Measurements measurements = new Measurements();
        pvMeasurements.setMeasurements(measurements);

        for (Value value : siteEnergy.getEnergy().getValues()) {
            Power power = new Power();
            if (value.getValue() == null) {
                power.setPower(0);
            } else {
                power.setPower(value.getValue());
            }
            measurements.setMeasurement(value.getDate().atZone(SITE_TIME_ZONE).toInstant().getEpochSecond(), power);
        }
        return pvMeasurements;
    }
}
