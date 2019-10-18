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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.technolution.apis.ApiConfig;
import nl.technolution.apis.ApiConfigRecord;
import nl.technolution.apis.EApiNames;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.webservice.JacksonFactory;

/**
 * Configuration for APXPrices
 */
public class ExxyConfig extends FritzyAppConfig {

    /** EFI devide id */
    @JsonProperty("deviceId")
    private String deviceId;

    /** Base URL for the ENTSO-E API: https://transparency.entsoe.eu/api */
    @JsonProperty("baseURL")
    private String baseURL;

    /**
     * Security token (access token) as supplied by ENTSO-E.
     * 
     * NOTE: This is a person bound token, every user of this code should obtain their own access token! See
     * https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html
     * #_authentication_and_authorisation
     * 
     */
    @JsonProperty("securityToken")
    private String securityToken;

    /** How much kWh can exxy sell in a trade period */
    @JsonProperty("capacity")
    private int capacity;

    /**
     * Map with fixed prices in EUR per kWh for every hour of the day (local time). Hours > 23 are ignored. When
     * {@link useFixedPrices} is true these fixed prices are used instead of the live day ahead prices.
     *
     */
    @JsonProperty("fixedPrices")
    private Map<Integer, Double> fixedPrices;

    /** When true the prices from {@link fixedPrices} are used instead of the live day ahead prices. */
    @JsonProperty("useFixedPrices")
    private boolean useFixedPrices;

    /** For each size in the given list an order to buy and sell is created. */
    @JsonProperty("orderSizes")
    private List<Double> orderSizes;

    @JsonCreator
    public ExxyConfig(@JsonProperty("baseURL") String baseURL, @JsonProperty("securityToken") String securityToken,
            @JsonProperty("capacity") int capacity, @JsonProperty("fixedPrices") Map<Integer, Double> fixedPrices,
            @JsonProperty("useFixedPrices") boolean useFixedPrices) {
        this.baseURL = baseURL;
        this.securityToken = securityToken;
        this.capacity = capacity;
        this.fixedPrices = fixedPrices;
        this.useFixedPrices = useFixedPrices;
        validateConfig();
    }

    /**
     * 
     */
    public ExxyConfig() {

    }

    /**
     * Generate exxy config
     * 
     * @param args none
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {

        ObjectMapper mapper = JacksonFactory.defaultMapper();
        ExxyConfig c = new ExxyConfig();
        c.deviceId = "exxy";
        c.setEnvironment("live");

        c.baseURL = "https://transparency.entsoe.eu/api";
        c.securityToken = "0b1d9ae3-d9a6-4c6b-8dc1-c62a18387ac5";
        c.capacity = 100;
        c.useFixedPrices = false;
        c.fixedPrices = Maps.newHashMap();
        c.fixedPrices.put(0, 0.01d);
        c.fixedPrices.put(1, 0.02d);
        c.fixedPrices.put(2, 0.03d);
        c.fixedPrices.put(3, 0.04d);
        c.fixedPrices.put(4, 0.05d);
        c.fixedPrices.put(5, 0.06d);
        c.fixedPrices.put(6, 0.07d);
        c.fixedPrices.put(7, 0.08d);
        c.fixedPrices.put(8, 0.09d);
        c.fixedPrices.put(9, 0.10d);
        c.fixedPrices.put(10, 0.11d);
        c.fixedPrices.put(11, 0.12d);
        c.fixedPrices.put(12, 0.13d);
        c.fixedPrices.put(13, 0.14d);
        c.fixedPrices.put(14, 0.15d);
        c.fixedPrices.put(15, 0.16d);
        c.fixedPrices.put(16, 0.17d);
        c.fixedPrices.put(17, 0.18d);
        c.fixedPrices.put(18, 0.19d);
        c.fixedPrices.put(19, 0.20d);
        c.fixedPrices.put(20, 0.21d);
        c.fixedPrices.put(21, 0.22d);
        c.fixedPrices.put(22, 0.23d);
        c.fixedPrices.put(23, 0.24d);

        ApiConfig apiConfig = new ApiConfig();
        ApiConfigRecord netty = new ApiConfigRecord(EApiNames.NETTY.getName(), "http://netty:8080/", 5000, 5000);
        apiConfig.setApis(Lists.newArrayList(netty));
        c.setApiConfig(apiConfig);

        c.orderSizes = Lists.newArrayList(0.1d, 0.5d, 1d);

        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, c);
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

    public List<Double> getOrderSizes() {
        return orderSizes;
    }

    public void setOrderSizes(List<Double> orderSizes) {
        this.orderSizes = orderSizes;
    }

    public void setFixedPrices(Map<Integer, Double> fixedPrices) {
        this.fixedPrices = fixedPrices;
    }

    public void setUseFixedPrices(boolean useFixedPrices) {
        this.useFixedPrices = useFixedPrices;
    }
}
