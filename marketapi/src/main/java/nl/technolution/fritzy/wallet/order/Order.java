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
package nl.technolution.fritzy.wallet.order;

public class Order {

    private String makerToken;
    private String takerToken;
    private String makerAmount;
    private String takerAmount;

    public String getMakerToken() {
        return makerToken;
    }

    public void setMakerToken(String makerToken) {
        this.makerToken = makerToken;
    }

    public String getTakerToken() {
        return takerToken;
    }

    public void setTakerToken(String takerToken) {
        this.takerToken = takerToken;
    }

    public String getMakerAmount() {
        return makerAmount;
    }

    public void setMakerAmount(String makerAmount) {
        this.makerAmount = makerAmount;
    }

    public String getTakerAmount() {
        return takerAmount;
    }

    public void setTakerAmount(String takerAmount) {
        this.takerAmount = takerAmount;
    }

}
