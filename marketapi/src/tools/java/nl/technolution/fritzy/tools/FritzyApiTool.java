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
package nl.technolution.fritzy.tools;

import java.util.Arrays;

import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.order.Order;

public class FritzyApiTool {

    public static void main(String[] args) {

        String url = "http://82.196.13.251/api/";
        FritzyApi api = new FritzyApi(url);
        api.login("test@fiets.be", "qazqaz");
        Arrays.asList(api.orders().getOrders().getRecords()).forEach(o -> System.out.println(o));

        Order order = new Order();
        order.setMakerToken("kwh");
        order.setTakerToken("eur");
        order.setMakerAmount("1000000000000000000");
        order.setTakerAmount("1000000000000000000");
        String createOrder = api.createOrder(order);
        System.out.println(createOrder);

    }
}
