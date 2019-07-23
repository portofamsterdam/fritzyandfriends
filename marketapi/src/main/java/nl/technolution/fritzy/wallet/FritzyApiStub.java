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
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import nl.technolution.IJsonnable;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.webservice.JacksonFactory;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;

/**
 * Stub the Fritsy API
 */
public class FritzyApiStub implements IFritzyApi {

    private static FritzyApiStub stubbedApi;

    private final ObjectMapper MAPPER = JacksonFactory.defaultMapper();
    private final List<DashboardEvent> events = new ArrayList<>();
    private final List<WebUser> users = new ArrayList<>();
    private final Map<String, FritzyBalance> balances = new HashMap<>();
    private final Orders orders;

    private WebUser loginInUser;

    private FritzyApiStub() {
        orders = new Orders();
        orders.setRecords(new Record[0]);
    }

    /**
     * Singleton instance of stub
     * 
     * @return
     */
    public static FritzyApiStub instance() {
        synchronized (FritzyApiStub.class) {
            if (stubbedApi == null) {
                stubbedApi = new FritzyApiStub();
            }
        }
        return stubbedApi;
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

        BigDecimal takerGet = new BigDecimal(order.getTakerAssetAmount()).multiply(BigDecimal.valueOf(-1L));
        BigDecimal takerGive = new BigDecimal(order.getMakerAssetAmount());
        incr(EContractAddress.valueOf(order.getTakerAssetData()), takerGet, order.getTakerAddress());
        incr(EContractAddress.valueOf(order.getMakerAssetData()), takerGive, order.getTakerAddress());

        BigDecimal makerGet = new BigDecimal(order.getMakerAssetAmount()).multiply(BigDecimal.valueOf(-1L));
        BigDecimal makerGive = new BigDecimal(order.getTakerAssetAmount());
        incr(EContractAddress.valueOf(order.getMakerAssetData()), makerGet, order.getMakerAddress());
        incr(EContractAddress.valueOf(order.getTakerAssetData()), makerGive, order.getMakerAddress());

        return generateHash(Objects.hash(orderHash));
    }

    private void incr(EContractAddress contractaddress, BigDecimal takerAssetAmount, String takerAddress) {
        FritzyBalance balance = balances.computeIfAbsent(takerAddress, k -> new FritzyBalance());
        switch (contractaddress) {
        case EUR:
            balance.setEur(balance.getEur().add(takerAssetAmount));
            break;
        case KWH:
            balance.setKwh(balance.getKwh().add(takerAssetAmount));
            break;
        default:
            throw new IllegalArgumentException();
        }
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
        String username = loginInUser != null ? loginInUser.getName() : "unknown";
        String dataStr;
        try {
            dataStr = data != null ? MAPPER.writeValueAsString(data) : "";
        } catch (JsonProcessingException e) {
            dataStr = "<eventdata unparsable>";
        }
        events.add(new DashboardEvent("test", username, msg, tag.getTag(), new Date(), dataStr));
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
        FritzyBalance sender = balances.computeIfAbsent(loginInUser.getAddress(), k -> new FritzyBalance());
        FritzyBalance receiver = balances.computeIfAbsent(toAddress, k -> new FritzyBalance());
        switch (contractAddress) {
        case ETH:
            Preconditions.checkArgument(sender.getEth().compareTo(value) > 0);
            sender.setEth(sender.getEth().subtract(value));
            receiver.setEth(receiver.getEth().add(value));
            break;
        case EUR:
            Preconditions.checkArgument(sender.getEur().compareTo(value) > 0);
            sender.setEur(sender.getEur().subtract(value));
            receiver.setEur(receiver.getEur().add(value));
            break;
        case KWH:
            Preconditions.checkArgument(sender.getKwh().compareTo(value) > 0);
            sender.setKwh(sender.getKwh().subtract(value));
            receiver.setKwh(receiver.getKwh().add(value));
            break;
        }
        return generateHash(sender.hashCode() ^ receiver.hashCode());
    }

    @Override
    public String getAddress() {
        return loginInUser.getAddress();
    }

    @Override
    public String createOrder(EContractAddress makerToken, EContractAddress takerToken, BigDecimal makerAmount,
            BigDecimal takerAmount) {

        List<Record> ordersList = Lists.newArrayList(Arrays.asList(orders.getRecords()));
        WebOrder webOrder = new WebOrder();
        String generateHash = generateHash(Instant.now().hashCode());
        webOrder.setHash(generateHash);
        webOrder.setMakerAddress(loginInUser.getAddress());
        webOrder.setMakerAssetAmount(makerAmount.toPlainString());
        webOrder.setMakerAssetData(makerToken.getContractName());
        webOrder.setTakerAssetAmount(takerAmount.toPlainString());
        webOrder.setTakerAssetData(takerToken.getContractName());
        Record e = new Record();
        e.setOrder(webOrder);
        ordersList.add(e);
        orders.setRecords(ordersList.toArray(new Record[ordersList.size()]));
        return generateHash;
    }
}
