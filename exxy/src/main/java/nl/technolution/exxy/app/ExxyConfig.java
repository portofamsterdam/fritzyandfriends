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
package nl.technolution.exxy.app;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.dropwizard.Configuration;

/**
 * Configuration for APXPrices
 */
public class ExxyConfig extends Configuration {
    /**
     * Base URL for the ENTSO-E API: https://transparency.entsoe.eu/api
     * 
     */
    @JsonProperty("baseURL")
    private final String baseURL;

    /**
     * Security token (access token) as supplied by ENTSO-E.
     * 
     * NOTE: This is a person bound token, every user of this code should obtain their own access token! See
     * https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html
     * #_authentication_and_authorisation
     * 
     */
    @JsonProperty("securityToken")
    private final String securityToken;

    /**
     * Map with fixed prices in EUR per kWh for every hour of the day (local time). Hours > 23 are ignored. When
     * {@link useFixedPrices} is true these fixed prices are used instead of the live day ahead prices.
     *
     */
    @JsonProperty("fixedPrices")
    private Map<Integer, Double> fixedPrices;

    /**
     * When true the prices from {@link fixedPrices} are used instead of the live day ahead prices.
     *
     */
    @JsonProperty("useFixedPrices")
    private boolean useFixedPrices;

    @JsonCreator
    public ExxyConfig(@JsonProperty("baseURL") String baseURL, @JsonProperty("securityToken") String securityToken,
            @JsonProperty("fixedPrices") Map<Integer, Double> fixedPrices,
            @JsonProperty("useFixedPrices") boolean useFixedPrices) {
        super();
        this.baseURL = baseURL;
        this.securityToken = securityToken;
        this.fixedPrices = fixedPrices;
        this.useFixedPrices = useFixedPrices;
        validateConfig();
    }

    /**
     * 
     */
    private void validateConfig() {
        if (useFixedPrices) {
            Preconditions.checkArgument((fixedPrices != null), "With useFixedPrices=true fixedPrices should be set");
            for (int i = 0; i < 24; i++) {
                if (!fixedPrices.containsKey(i)) {
                    throw new IllegalArgumentException("Fixed price missing for hour " + i);
                }
            }
        } else {
            Preconditions.checkArgument(!(Strings.isNullOrEmpty(baseURL)), "baseURL should be set");
            Preconditions.checkArgument(!(Strings.isNullOrEmpty(securityToken)), "securityToken should be set");
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public Map<Integer, Double> getFixedPrices() {
        return fixedPrices;
    }

    public boolean isUseFixedPrices() {
        return useFixedPrices;
    }

}
