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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

import com.google.common.collect.Maps;

import nl.technolution.protocols.efi.ElectricityProfile;

/**
 * Production tracker per 15 minutes
 */
public class ExpectedPvProduction {

    private final Map<Instant, List<ProductionSet>> register = Maps.newHashMap();
    
    /**
     * 
     * @param profiles
     */
    public void registerExpectedProduction(Instant startTs, ElectricityProfile profile, String updateId) {
        
        Instant startOfElemenet = startTs;
        
        for (ElectricityProfile.Element element : profile.getElement()) {
            
            Instant endOfElement = addXmlDuration(startOfElemenet, element.getDuration());
            
            long minutesPerElement = startOfElemenet.until(endOfElement, ChronoUnit.MINUTES);
            double usagePerMinute = element.getPower() / minutesPerElement;

            for (Instant tradePeriod = roundDownQuarter(startOfElemenet); 
                    tradePeriod.isAfter(endOfElement); 
                    tradePeriod.plusSeconds(900)) {
                
                Instant endOfPeriod = roundUpQuarter(startOfElemenet);
                Instant endOfProductionThisPeriod = endOfPeriod.isAfter(endOfElement) ? endOfElement : endOfPeriod;
                
                long minutesOfProductionThisPeriod = startOfElemenet.until(endOfProductionThisPeriod,
                        ChronoUnit.MINUTES);
                double usageThisPeriod = minutesOfProductionThisPeriod * usagePerMinute;
                register.computeIfAbsent(tradePeriod, i -> new ArrayList<>())
                        .add(new ProductionSet(updateId, usageThisPeriod));
            }
            
            startOfElemenet = endOfElement.plusSeconds(1);
            
        }
    }
    
    /**
     * @param ts to find expected production for
     * @return expected production
     */
    public double getExprectedProduction(Instant ts) {
        return register.get(roundDownQuarter(ts)).stream().map(v -> v.production).reduce((a, b) -> a + b).orElse(0d);
    }
    
    private final class ProductionSet {
        
        private final String updateId;
        private final double production;

        private ProductionSet(String updateId, double production) {
            this.updateId = updateId;
            this.production = production;
        }
    }

    private Instant addXmlDuration(Instant ts, Duration duration) {
        return ts.plus(duration.getTimeInMillis(new Date()), ChronoUnit.MILLIS);
    }

    private Instant roundDownQuarter(Instant ts) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(ts, ZoneId.systemDefault());
        GregorianCalendar calendar = GregorianCalendar.from(dateTime);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        int nearestQuarter = calendar.get(Calendar.MINUTE) - calendar.get(Calendar.MINUTE) % 15;
        calendar.set(Calendar.MINUTE, nearestQuarter);
        return Instant.ofEpochMilli(calendar.getTimeInMillis());
    }

    private Instant roundUpQuarter(Instant ts) {
        return roundDownQuarter(ts).plusSeconds(900L);
    }
}
