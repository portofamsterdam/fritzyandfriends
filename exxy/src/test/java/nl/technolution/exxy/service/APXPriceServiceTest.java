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
package nl.technolution.exxy.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.Parameter;
import org.mockserver.verify.VerificationTimes;

import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.client.ITransparencyPlatformClient;
import nl.technolution.exxy.client.TransparencyPlatformClient;
import nl.technolution.exxy.service.APXPricesService.NoPricesAvailableException;

/**
 * Tests for APXPriceService
 * 
 */
public class APXPriceServiceTest {

    private static final String SECURITY_TOKEN = "TEST_TOKEN";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    private IAPXPricesService priceService;
    private ClientAndServer mockServer;

    /*
     * The template for the xml response is based on the real server reply for this request:
     * https://transparency.entsoe.eu/api?securityToken=0b1d9ae3-d9a6-4c6b-8dc1-c62a18387ac5&documentType=A44
     * &in_Domain=10YNL----------L&out_Domain=10YNL----------L&TimeInterval=2019-04-24T12:00:00Z/2019-04-24T13:00:00Z
     * 
     */
    // @formatter:off
    private String xmlTemplate = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
            "<Publication_MarketDocument xmlns=\"urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0\">" + 
            "    <mRID>da5f779cfe584a469588cc497e9a4f8c</mRID>" + 
            "    <revisionNumber>1</revisionNumber>" + 
            "    <type>A44</type>" + 
            "    <sender_MarketParticipant.mRID codingScheme=\"A01\">10X1001A1001A450</sender_MarketParticipant.mRID>" +
            "    <sender_MarketParticipant.marketRole.type>A32</sender_MarketParticipant.marketRole.type>" + 
            "    <receiver_MarketParticipant.mRID codingScheme=\"A01\">10X1001A1001A450" +
            "    </receiver_MarketParticipant.mRID>" +
            "    <receiver_MarketParticipant.marketRole.type>A33</receiver_MarketParticipant.marketRole.type>" + 
            "    <createdDateTime>2019-05-28T09:20:38Z</createdDateTime>" + 
            "    <period.timeInterval>" + 
            "        <start>%1$s</start>" + 
            "        <end>%2$s</end>" + 
            "    </period.timeInterval>" + 
            "    <TimeSeries>" + 
            "        <mRID>1</mRID>" + 
            "        <businessType>A62</businessType>" + 
            "        <in_Domain.mRID codingScheme=\"A01\">10YNL----------L</in_Domain.mRID>" + 
            "        <out_Domain.mRID codingScheme=\"A01\">10YNL----------L</out_Domain.mRID>" + 
            "        <currency_Unit.name>EUR</currency_Unit.name>" + 
            "        <price_Measure_Unit.name>MWH</price_Measure_Unit.name>" + 
            "        <curveType>A01</curveType>" + 
            "            <Period>" + 
            "                <timeInterval>" + 
            "                    <start>%1$s</start>" + 
            "                    <end>%2$s</end>" + 
            "                </timeInterval>" + 
            "                <resolution>PT60M</resolution>" + 
            "                    <Point>" + 
            "                        <position>1</position>" + 
            "                        <price.amount>00.00</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>2</position>" + 
            "                        <price.amount>01.01</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>3</position>" + 
            "                        <price.amount>02.02</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>4</position>" + 
            "                        <price.amount>03.03</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>5</position>" + 
            "                        <price.amount>04.04</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>6</position>" + 
            "                        <price.amount>05.05</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>7</position>" + 
            "                        <price.amount>06.06</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>8</position>" + 
            "                        <price.amount>07.07</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>9</position>" + 
            "                        <price.amount>08.08</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>10</position>" + 
            "                        <price.amount>09.09</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>11</position>" + 
            "                        <price.amount>10.10</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>12</position>" + 
            "                        <price.amount>11.11</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>13</position>" + 
            "                        <price.amount>12.12</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>14</position>" + 
            "                        <price.amount>13.13</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>15</position>" + 
            "                        <price.amount>14.14</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>16</position>" + 
            "                        <price.amount>15.15</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>17</position>" + 
            "                        <price.amount>16.16</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>18</position>" + 
            "                        <price.amount>17.17</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>19</position>" + 
            "                        <price.amount>18.18</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>20</position>" + 
            "                        <price.amount>19.19</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>21</position>" + 
            "                        <price.amount>20.20</price.amount>" + 
            "                    </Point>" +
            "                    <Point>" + 
            "                        <position>22</position>" + 
            "                        <price.amount>21.21</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>23</position>" + 
            "                        <price.amount>22.22</price.amount>" + 
            "                    </Point>" + 
            "                    <Point>" + 
            "                        <position>24</position>" + 
            "                        <price.amount>23.23</price.amount>" + 
            "                    </Point>" + 
            "            </Period>" + 
            "    </TimeSeries>" + 
            "</Publication_MarketDocument>";
// @formatter:on

    @Before
    public void before() throws IOException {
        mockServer = ClientAndServer.startClientAndServer(0);
        ExxyConfig config = new ExxyConfig("http://localhost:" + mockServer.getLocalPort() + "/api", SECURITY_TOKEN,
                0, null, false, null);
        // ServiceFinder.setupDropWizardServices(config);
        IAPXPricesService apxPrices = new APXPricesService();
        apxPrices.init(config);
        Services.put(IAPXPricesService.class, apxPrices);
        ITransparencyPlatformClient client = new TransparencyPlatformClient();
        client.init(config);
        Services.put(ITransparencyPlatformClient.class, client);
        priceService = Services.get(IAPXPricesService.class);
    }

    @After
    public void stopMockServer() {
        mockServer.stop();
    }

    @SuppressWarnings("resource")
    private void startMockClient(ZonedDateTime now) {
        ZonedDateTime requestStart = now.truncatedTo(ChronoUnit.HOURS).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime requestEnd = requestStart.plusHours(1);
        // The entsoe api gives the data for the whole day when only data for 1 hour is requested
        ZonedDateTime dataStart = now.truncatedTo(ChronoUnit.DAYS).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dataEnd = dataStart.plusDays(1);
        String response = String.format(xmlTemplate, dataStart.format(DATE_TIME_FORMAT),
                dataEnd.format(DATE_TIME_FORMAT));
        String timeInterval = requestStart.format(DateTimeFormatter.ISO_DATE_TIME) + "/" +
                requestEnd.format(DateTimeFormatter.ISO_DATE_TIME);

        new MockServerClient("localhost", mockServer.getLocalPort()).when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api")
                .withQueryStringParameters(Parameter.param("securityToken", SECURITY_TOKEN),
                        Parameter.param("documentType", "A44"), Parameter.param("in_Domain", "10YNL----------L"),
                        Parameter.param("out_Domain", "10YNL----------L"),
                        Parameter.param("TimeInterval", timeInterval)))
                .respond(HttpResponse.response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(response)
                        .withHeader(Header.header("Content-Type", "application/xml")));
    }

    /**
     * Test getPrice method using the mock server.
     * 
     * @throws NoPricesAvailableException
     */
    @Test
    public void getPrice() throws NoPricesAvailableException {
        // next should give the price at midnight(UTC) at 1-1-2019
        ZonedDateTime now = ZonedDateTime.parse("2019-01-01T00:00:00Z");
        startMockClient(now);
        Instant instant = now.toInstant();
        double price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        double expectedPrice = now.toLocalDateTime().getHour() / 100d + now.getHour();
        assertEquals(expectedPrice / 1000, price, 0.001);
        // end of hour, should give same price
        instant = Instant.parse("2019-01-01T00:59:59.99Z");
        price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        assertEquals(expectedPrice / 1000, price, 0.001);
        // 2 requests to the mockserver are expected (cache is not used in this test)
        mockServer.verify(HttpRequest.request(), VerificationTimes.exactly(2));
    }

    /**
     * Test getPrice method during day light saving using the mock server.
     * 
     * @throws NoPricesAvailableException
     */
    @Test
    public void getPriceDuringDST() throws NoPricesAvailableException {
        ZonedDateTime now = ZonedDateTime.parse("2019-04-24T12:00:00Z");
        startMockClient(now);
        Instant instant = now.toInstant();
        double price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        double expectedPrice = now.toLocalDateTime().getHour() / 100d + now.getHour();
        assertEquals(expectedPrice / 1000, price, 0.001);
    }

    /**
     * Test getPricePerkWhNextQuarter (from the cache) using the mock server.
     * 
     * @throws NoPricesAvailableException
     */
    @Test
    public void getNextquarterHouerPrice() throws NoPricesAvailableException {
        ZonedDateTime now = ZonedDateTime.now();
        startMockClient(now);
        // fill cache
        IPriceReceiver priceRetriever = new APXPriceRetriever();
        Services.put(IPriceReceiver.class, priceRetriever);
        priceRetriever.execute();
        double price = priceService.getPricePerkWhNextQuarter();
        System.out.println("price per kWh for " + now.toLocalDateTime() + ": " + price);
        int hour = now.toLocalDateTime().plusMinutes(15).getHour();
        double expectedPrice = hour / 100d + hour;
        assertEquals(expectedPrice / 1000, price, 0.001);
        // again
        price = priceService.getPricePerkWhNextQuarter();
        System.out.println("price per kWh for " + now.toLocalDateTime() + ": " + price);
        hour = now.toLocalDateTime().plusMinutes(15).getHour();
        expectedPrice = hour / 100d + hour;
        assertEquals(expectedPrice / 1000, price, 0.001);

        // only 1 request to the mockserver is expected (to fill the cache)
        mockServer.verify(HttpRequest.request(), VerificationTimes.exactly(1));
    }

    /**
     * Test getPricePerkWh when the server has no data for that day available.
     * 
     * @throws NoPricesAvailableException
     */
    @Test(expected = NoPricesAvailableException.class)
    public void noPricesAvailableOnServerTest() throws NoPricesAvailableException {
        // next should fail with an exception, no data available is simulated by NOT initializing the mockClient
        ZonedDateTime now = ZonedDateTime.parse("1019-01-01T00:00:00.00Z");
        Instant instant = now.toInstant();
        priceService.getPricePerkWh(instant);
    }

    /**
     * Test getPricePerkWh (from cache) when the cache is not filled
     * 
     * @throws NoPricesAvailableException
     */
    @Test(expected = NoPricesAvailableException.class)
    public void noPricesAvailableInCacheTest() throws NoPricesAvailableException {
        IPriceReceiver priceRetriever = new APXPriceRetriever();
        Services.put(IPriceReceiver.class, priceRetriever);
        // next should fail with an exception, no data available because cache is not filled
        priceService.getPricePerkWh();
    }

    /**
     * Test getPricePerkWh (from cache) when the cache is not filled
     * 
     * @throws NoPricesAvailableException
     */
    @Test
    public void fixedPricesTest() throws NoPricesAvailableException {
        // setup config with fixed prices
        Map<Integer, Double> fixedPrices = new HashMap<Integer, Double>();
        for (int i = 0; i < 24; i++) {
            fixedPrices.put(i, (double)i / 100);
        }
        ExxyConfig config = new ExxyConfig("", "", 0, fixedPrices, true, null);
        priceService = new APXPricesService();
        // manually re-init the service with the new config so fixed prices are used
        priceService.init(config);

        Instant instant = OffsetDateTime.now().withHour(8).toInstant();
        double price = priceService.getPricePerkWh(instant);
        assertEquals(0.08d, price, 0.001);

        price = priceService.getPricePerkWh();
        assertEquals((double)LocalTime.now().get(ChronoField.HOUR_OF_DAY) / 100, price, 0.001);

        price = priceService.getPricePerkWhNextQuarter();
        assertEquals((double)LocalTime.now().plusMinutes(15).get(ChronoField.HOUR_OF_DAY) / 100, price, 0.001);

        // no requests to the mock server are expected:
        mockServer.verifyZeroInteractions();
    }
}
