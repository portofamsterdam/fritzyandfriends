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
package nl.technolution.apxprices.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * Configuration for APXPrices
 */
public class APXPricesConfig extends Configuration {
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

    @JsonCreator
    public APXPricesConfig(@JsonProperty("baseURL") String baseURL,
            @JsonProperty("securityToken") String securityToken) {
        super();
        this.baseURL = baseURL;
        this.securityToken = securityToken;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    // TODO WHO: setting voor 'fixed prices' aan/uit + lijst met 24 fixed prijzen voor ieder uur.
}
