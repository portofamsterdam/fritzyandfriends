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
package nl.technolution.dashboard;

/**
 * 
 */
public enum EEventType {
    CHAT("CHAT"),
    ORDER_OFFER("ORDER_OFFER"),
    ORDER_ACCEPT("ORDER_ACCEPT"),
    REWARD_OFFER("REWARD_OFFER"),
    REWARD_CLAIM("REWARD_CLAIM"),
    LIMIT_ACTOR("LIMIT_ACTOR"),
    LIMIT_TOTAL("LIMIT_TOTAL"),
    LIMIT_EXCEEDED("LIMIT_EXCEEDED"),
    BALANCE("BALANCE"),
    DEVICE_STATE("DEVICE_STATE");

    private final String tag;

    EEventType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
