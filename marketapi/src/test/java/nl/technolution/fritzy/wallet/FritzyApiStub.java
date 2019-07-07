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
package nl.technolution.fritzy.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.technolution.IJsonnable;
import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Order;
import nl.technolution.fritzy.wallet.order.Orders;

public class FritzyApiStub implements IFritzyApi {

    private final List<DashboardEvent> events = new ArrayList<>();
    private String user;
    private Orders orders;

    @Override
    public void login(String user, String password) {
        //
        this.user = user;
    }

    @Override
    public void register(String email, String user, String password) {
        // User is always registered in stub
    }

    @Override
    public GetOrdersResponse orders() {
        GetOrdersResponse getOrdersResponse = new GetOrdersResponse();
        getOrdersResponse.setOrders(orders);
        return getOrdersResponse;
    }

    @Override
    public WebOrder order(String orderHash) {
        return Arrays.asList(orders.getRecords()).stream()
                .filter(o -> o.getHash().equals(orderHash))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String fillOrder(String orderHash) {
        //
        return null;
    }

    @Override
    public String createOrder(Order order) {
        //
        return null;
    }

    @Override
    public void cancelOrder(String hash) {
        //

    }

    @Override
    public void log(EEventType tag, String msg, IJsonnable data) {
        events.add(new DashboardEvent("test", user, msg, tag.getTag(), new Date(), data.toString()));
    }

    @Override
    public FritzyBalance balance() {
        //
        return null;
    }

    @Override
    public void mint(String address, BigDecimal value, EContractAddress contractAddress) {
        //

    }
}
