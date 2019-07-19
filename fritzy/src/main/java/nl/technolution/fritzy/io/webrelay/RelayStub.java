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
package nl.technolution.fritzy.io.webrelay;

import java.io.IOException;

/**
 * Open on even hours
 */
public class RelayStub implements IWebRelay {

    boolean isCooling = false;

    @Override
    public WebRelayState getState() throws IOException {
        return new WebRelayState(isCooling, isCooling, 0, 0);
    }

    @Override
    public WebRelayState setRelay(boolean state) throws IOException {
        isCooling = state;
        return new WebRelayState(isCooling, isCooling, 0, 0);
    }
}
