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
package nl.technolution.batty.trader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement.LowerBound;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement.UpperBound;

/**
 * Test negotiator
 */
public class NagotiatorTest {

    /**
     * 
     */
    @Test
    public void testCalculationCharge1() {
        ContinuousRunningModeElement m = new ContinuousRunningModeElement();

        LowerBound low = new LowerBound();
        low.setElectricalPower(200d);
        m.setLowerBound(low);

        UpperBound high = new UpperBound();
        high.setElectricalPower(1000d);
        m.setUpperBound(high);

        m.setFillLevelLowerBound(0);
        m.setFillLevelUpperBound(80);

        double actual = BatteryNegotiator.getKiloWattPerQuarterFromRunningMode(m, 60d);

        assertEquals(200d, actual, 0.0d);
    }

    /**
     * 
     */
    @Test
    public void testCalculationCharge2() {
        ContinuousRunningModeElement m = new ContinuousRunningModeElement();

        LowerBound low = new LowerBound();
        low.setElectricalPower(1000d);
        m.setLowerBound(low);

        UpperBound high = new UpperBound();
        high.setElectricalPower(2000d);
        m.setUpperBound(high);

        m.setFillLevelLowerBound(0);
        m.setFillLevelUpperBound(100);

        double actual = BatteryNegotiator.getKiloWattPerQuarterFromRunningMode(m, 50d);

        assertEquals(375d, actual, 0.0d);
    }

    /**
     * 
     */
    @Test
    public void testCalculationDischarge() {
        ContinuousRunningModeElement m = new ContinuousRunningModeElement();

        LowerBound low = new LowerBound();
        low.setElectricalPower(-1000d);
        m.setLowerBound(low);

        UpperBound high = new UpperBound();
        high.setElectricalPower(-2000d);
        m.setUpperBound(high);

        m.setFillLevelLowerBound(0);
        m.setFillLevelUpperBound(100);

        double actual = BatteryNegotiator.getKiloWattPerQuarterFromRunningMode(m, 50d);

        assertEquals(-375d, actual, 0.0d);
    }
}
