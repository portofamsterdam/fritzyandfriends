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
package nl.technolution.marketnegotiator.pv;

import java.time.Instant;

import com.google.common.base.Preconditions;

import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.InflexibleForecast;
import nl.technolution.protocols.efi.InflexibleInstruction;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.XmlUtils;

/**
 * Negotiates prices for PV panels
 */
public class PvNagotiator extends AbstractCustomerEnergyManager<InflexibleRegistration, InflexibleForecast> {

    private ExpectedPvProduction expectedProduction = new ExpectedPvProduction();

    @Override
    public Instruction flexibilityUpdate(InflexibleForecast update) {
        Preconditions.checkArgument(update.getForecastProfiles().getElectricityProbabilityProfile() == null,
                "Not supported");
        Instant startTs = XmlUtils.fromXmlCalendar(update.getValidFrom());
        expectedProduction.registerExpectedProduction(startTs, update.getForecastProfiles().getElectricityProfile(),
                update.getFlexibilityUpdateId());

        return Efi.build(InflexibleInstruction.class, getDeviceId());
    }

    @Override
    public void flexibilityRevoke(FlexibilityRevoke revocation) {
        expectedProduction = new ExpectedPvProduction();
    }
}
