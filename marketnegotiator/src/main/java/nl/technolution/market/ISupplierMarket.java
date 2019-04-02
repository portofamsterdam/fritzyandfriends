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
package nl.technolution.market;

/**
 * 
 */
public interface ISupplierMarket {

    /**
     * Offer production order to market for the next market period.
     * 
     * @param id identity of producer
     * @param wh number of watts produced
     * @param price at given price (market specifies currency)
     */
    void produceOrder(String id, long wh, double price);

    /**
     * Offer consume order to market for the next market period.
     * 
     * @param id identity of consumer
     * @param wh number of watts consumed
     * @param price at given price (market specifies currency)
     */
    void consumeOrder(String id, long wh, double price);
}
