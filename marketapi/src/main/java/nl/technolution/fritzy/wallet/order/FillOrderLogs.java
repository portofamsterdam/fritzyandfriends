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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 */
public class FillOrderLogs {

    @JsonProperty("logIndex")
    private int logIndex;

    @JsonProperty("transactionIndex")
    private int transactionIndex;

    @JsonProperty("transactionHash")
    private String transactionHash;

    @JsonProperty("blockHash")
    private String blockHash;

    @JsonProperty("blockNumber")
    private long blockNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("data")
    private String data;

    @JsonProperty("topics")
    private List<String> topics;

    @JsonProperty("type")
    private String type;
}
