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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.fritzy.gen.model.WebOrder;

/**
 * 
 */
public class Market implements ISupplierMarket {

    private final Logger log = LoggerFactory.getLogger(Market.class);
    private final String url;

    public Market(MarketConfig config) {
        this.url = config.getMarketUrl();
    }

    @Override
    public void produceOrder(String id, long wh, double price) {
        Client client = JerseyClientBuilder.newClient();
        WebTarget target = client.target(url + "/produce");
        Builder request = target.request();
        Response response = request.post(getEntity(id, wh, price));
        if (200 != response.getStatus()) {
            log.warn("Status {} on call produceOrder {} {} {}", response.getStatus(), id, wh, price);
        }

    }

    private Entity<?> getEntity(String id, long wh, double price) {
        WebOrder order = new WebOrder();

        return Entity.entity(order, MediaType.APPLICATION_JSON);
    }

    @Override
    public void consumeOrder(String id, long wh, double price) {

    }

}
