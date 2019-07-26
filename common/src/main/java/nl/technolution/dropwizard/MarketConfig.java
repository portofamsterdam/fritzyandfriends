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
package nl.technolution.dropwizard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Config needed to use api
 */
public class MarketConfig {

    @JsonProperty("useStub")
    private final boolean useStub;

    @JsonProperty("marketUrl")
    private final String marketUrl;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("password")
    private final String password;

    @JsonCreator
    public MarketConfig(@JsonProperty("useStub") boolean useStub,
            @JsonProperty("marketUrl") String marketUrl,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.useStub = useStub;
        this.marketUrl = marketUrl;
        this.email = email;
        this.password = password;
    }

    public boolean isUseStub() {
        return useStub;
    }

    public String getMarketUrl() {
        return marketUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
