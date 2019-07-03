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
package nl.technolution.sunny.trader;

import java.util.concurrent.TimeUnit;

import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.ITaskRunner;
import nl.technolution.dropwizard.tasks.TimedTask;

/**
 * 
 */
@TimedTask(period = 1, unit = TimeUnit.MINUTES)
public class SunnyTraderTask implements ITaskRunner {

    @Override
    public void execute() {
        ISunnyTrader trader = Services.get(ISunnyTrader.class);
        trader.evaluateDevice();
        trader.evaluateMarket();
    }
}
