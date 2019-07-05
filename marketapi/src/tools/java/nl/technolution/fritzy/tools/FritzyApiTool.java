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

import java.math.BigDecimal;

import com.google.common.base.Preconditions;

import nl.technolution.fritzy.wallet.FritzyApi;

public class FritzyApiTool {

    private static final String PASS = "qazqaz";
    private static final String USER = "testcase@martin.nl";

    public static void main(String[] args) {

        String url = "http://82.196.13.251/api/";
        FritzyApi api = new FritzyApi(url, "FritzyApiTool");
        // api.register(USER, "test", PASS);
        api.login(USER, PASS);
        String testWalletAddress = api.getAddress();
        Preconditions.checkArgument(0L == api.balance().longValue());
        

        api.login("test@fiets.be", PASS);
        BigDecimal monies = BigDecimal.valueOf(500L);
        api.mint(testWalletAddress, monies);

        api.login(USER, PASS);
        Preconditions.checkArgument(monies.longValue() == api.balance().longValue());
        
        // Arrays.asList(api.orders().getOrders().getRecords()).forEach(o -> System.out.println(o));
        //
        // Order order = new Order();
        // order.setMakerToken("kwh");
        // order.setTakerToken("eur");
        // order.setMakerAmount("1000000000000000000");
        // order.setTakerAmount("1000000000000000000");
        // String createOrder = api.createOrder(order);
        // System.out.println(createOrder);
        // System.out.println(api.balance());

    }
}
