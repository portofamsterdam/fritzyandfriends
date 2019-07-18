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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import nl.technolution.IJsonnable;
import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Order;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;

/**
 * Stub the Fritsy API
 */
public class FritzyApiStub implements IFritzyApi {


    private final List<DashboardEvent> events = new ArrayList<>();
    private final List<WebUser> users = new ArrayList<>();
    private final Map<String, FritzyBalance> balances = new HashMap<>();
    private final Orders orders;

    private WebUser loginInUser;

    public FritzyApiStub() {
        orders = new Orders();
        orders.setRecords(new Record[0]);
    }

    @Override
    public void login(String user, String password) {
        this.loginInUser = users.stream()
                .filter(u -> u.getEmail().equals(user))
                .findFirst()
                .orElseThrow(AssertionError::new);
    }

    @Override
    public WebUser register(String email, String username, String password) {
        WebUser user = new WebUser();
        String address = generateHash(username.hashCode());
        user.setAddress(address);
        user.setEmail(email);
        user.setName(username);
        users.add(user);
        return user;
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
                .map(r -> r.getOrder())
                .filter(o -> o.getHash().equals(orderHash))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String fillOrder(String orderHash) {
        WebOrder order = order(orderHash);
        Preconditions.checkNotNull(order);
        Preconditions.checkArgument(order.getTakerAddress() == null);
        order.setTakerAddress(loginInUser.getAddress());

        // TODO MKE implement filling order
        return null;
    }

    @Override
    public String createOrder(Order order) {
        List<Record> ordersList = Lists.newArrayList(Arrays.asList(orders.getRecords()));
        WebOrder webOrder = new WebOrder();
        String generateHash = generateHash(Instant.now().hashCode());
        webOrder.setHash(generateHash);
        webOrder.setMakerAddress(loginInUser.getAddress());
        webOrder.setMakerAssetAmount(order.getMakerAmount());
        webOrder.setMakerAssetData(order.getTakerToken());
        Record e = new Record();
        e.setOrder(webOrder);
        ordersList.add(e);
        orders.setRecords(ordersList.toArray(new Record[ordersList.size()]));
        return generateHash;
    }

    @Override
    public void cancelOrder(String hash) {
        List<Record> ordersList = Lists.newArrayList(Arrays.asList(orders.getRecords()));
        Iterator<Record> itr = ordersList.iterator();
        while (itr.hasNext()) {
            if (itr.next().getOrder().getHash().equals(hash)) {
                itr.remove();
            }
        }
        orders.setRecords(ordersList.toArray(new Record[ordersList.size()]));
    }

    @Override
    public void log(EEventType tag, String msg, IJsonnable data) {
        events.add(new DashboardEvent("test", loginInUser.getName(), msg, tag.getTag(), new Date(), data.toString()));
    }

    @Override
    public FritzyBalance balance() {
        return balances.computeIfAbsent(loginInUser.getAddress(), k -> new FritzyBalance());
    }

    @Override
    public void mint(String address, BigDecimal value, EContractAddress contractAddress) {
        FritzyBalance balence = balances.computeIfAbsent(address, k -> new FritzyBalance());
        switch (contractAddress) {
        case ETH:
            balence.setEth(balence.getEth().add(value));
            break;
        case EUR:
            balence.setEur(balence.getEur().add(value));
            break;
        case KWH:
            balence.setKwh(balence.getKwh().add(value));
            break;
        }
    }

    @Override
    public void burn(BigDecimal value, EContractAddress contractAddress) {
        FritzyBalance balence = balances.computeIfAbsent(loginInUser.getAddress(), k -> new FritzyBalance());
        switch (contractAddress) {
        case ETH:
            balence.setEth(balence.getEth().subtract(value));
            break;
        case EUR:
            balence.setEur(balence.getEur().subtract(value));
            break;
        case KWH:
            balence.setKwh(balence.getKwh().subtract(value));
            break;
        }
    }

    @Override
    public WebUser[] getUsers() {
        return users.toArray(new WebUser[users.size()]);
    }

    private String generateHash(int hash) {
        return "0x" + StringUtils.leftPad("", 32, "f") + String.format("%08X", hash);
    }

    @Override
    public void addMinter(String address, EContractAddress contractAddress) {
        //

    }

    @Override
    public String transfer(BigDecimal value, EContractAddress contractAddress, String toAddress) {
        //
        return null;
    }
}
