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
package nl.technolution.fritzy.wallet;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import nl.technolution.fritzy.gen.model.WebOrder;

/**
 * 
 */
public final class OrderHelper {

    private static final String NULL_ADDR = "0x0000000000000000000000000000000000000000";

    /**
     * Constructor for {@link OrderHelper} objects
     */
    private OrderHelper() {
        // hide
    }

    public static boolean isAccepted(WebOrder order) {
        Preconditions.checkNotNull(order);
        if (Strings.isNullOrEmpty(order.getTakerAddress())) {
            return false;
        }
        return !NULL_ADDR.equals(order.getTakerAddress());
    }
}
