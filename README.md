Fritzy2.0: een showcase voor decentrale
organisatie van industriële energie

Uitgangspunten
• Leren door te experimenteren.
• Echt: de essentie, zo eenvoudig mogelijk.
• Boeiend: eenvoudig inzichtelijk.
• Experimenteel: eenvoudig aan te passen.
• Aanstekelijk: eenvoudig aan te sluiten.

# Configuraties

## Fritzy
/** EFI id of the device */
@JsonProperty("deviceId")

/** host address of webrelay */
@JsonProperty("host")

/** Port of webrelay (default 80) */
@JsonProperty("port")

/** Serial port used to read temp sensor */
@JsonProperty("serialPort")

/** stub the temperature sensor */
@JsonProperty("stubTemparature")

/** stub the webrelay */
@JsonProperty("stubRelay")

/** lowest acceptable temperature */
@JsonProperty("minTemp")

/** highest acceptable temperature */
@JsonProperty("maxTemp")

/** safety margin, when temperature at maxTemp + maxMargin Fritzy will be turned on regardless of market position */
@JsonProperty("maxMargin")

/**  The power consumption of Fritzy in Watt (in lieu of a power meter) used for calculating the amount of energy to buy and to report consumption */
@JsonProperty("power")

/** The leakage rate of Fritzy in °C per second (when turned off) */
@JsonProperty("leakageRate")

/** The cooling speed of Fritzy in °C per second (when turned on) */
@JsonProperty("coolingSpeed")

/** offset in euro cent used at the start of the negation. First bid will be market - offset. */
@JsonProperty("marketPriceStartOffset")


## Sunny
/** EFI id of the device */
@JsonProperty("deviceId")

/** offset in euro cent used at the start of the negation. First bid will be marketprice + offset. */
@JsonProperty("marketPriceStartOffset")

/** use stub instead of inverter */
@JsonProperty("useSolarEdgeStub")

/** base URL to SolarEdge monitoring portal. Used to retrieve hourly values which are needed by pvCast */
@JsonProperty("solarEdgeMonitoringBaseURL")

/** API key for SolarEdge monitoring portal. */
@JsonProperty("solarEdgeMonitoringApikey")

/** base URL to pvCast. pvCast supplies the forecasts so Sunny 'knows' how much energy it has to offer */
@JsonProperty("pvCastBaseURL")

/** API key for pvCast. */
@JsonProperty("pvCastApiKey")

/** IP address of the SolarEdge inverter */
@JsonProperty("solarEdgeModbusIpAddress")

/** modbus port of the SolarEdge inverter */
@JsonProperty("solarEdgeModbusPort")

/** modbus device id of the SolarEdge inverter */
@JsonProperty("solarEdgeModbusDeviceId")


## Netty
/** EFI devide id */
@JsonProperty("deviceId")

/** Base URL for the ENTSO-E API: https://transparency.entsoe.eu/api */
@JsonProperty("baseURL")

/**  Security token (access token) as supplied by ENTSO-E.
NOTE: This is a person bound token, every user of this code should obtain their own access token! See https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_authentication_and_authorisation */
@JsonProperty("securityToken")

/** How much kWh can exxy sell in a trade period */
@JsonProperty("capacity")

/** Map with fixed prices in EUR per kWh for every hour of the day (local time). Hours > 23 are ignored. When useFixedPrices is true these fixed prices are used instead of the live day ahead prices. */
@JsonProperty("fixedPrices")

/** When true the prices from fixedPrices are used instead of the live day ahead prices. */
@JsonProperty("useFixedPrices")

/** For each size in the given list an order to buy and sell is created. */
@JsonProperty("orderSizes")
