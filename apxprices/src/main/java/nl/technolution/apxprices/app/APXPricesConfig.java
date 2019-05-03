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
     * Base URL for the entsoe API: https://transparency.entsoe.eu/api
     * 
     */
    @JsonProperty("baseURL")
    private final String baseURL;
    
	/** 
     * Security token (access token) as supplied by entsoe
     * 
     */
    @JsonProperty("securityToken")
    private final String securityToken;
    
    @JsonCreator
    public APXPricesConfig(@JsonProperty("baseURL") String baseURL, @JsonProperty("securityToken") String securityToken) {
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
	
	//TODO WHO: setting voor 'fake prices' aan/uit + lijst met 24 fake prijzen voor ieder uur.
}
