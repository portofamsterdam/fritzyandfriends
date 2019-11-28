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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.webservice.JacksonFactory;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.model.GetEventResponse;
import nl.technolution.fritzy.wallet.model.UsersResponseEntry;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;

/**
 * Stub the Fritsy API
 */
public final class FritzyApiStub implements IFritzyApi {

    private static FritzyApiStub stubbedApi;

    private final ObjectMapper mapper = JacksonFactory.defaultMapper();
    private final Logger log = Log.getLogger();

    private AtomicInteger eventId;
    private AtomicInteger orderCounter;
    private List<ApiEvent> events;
    private List<WebUser> users;
    private Map<String, FritzyBalance> balances;
    private Orders orders;

    private WebUser loginInUser;

    private FritzyApiStub() {
        // hide
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
                FritzyApiStub.reset(); // init all registers
            }
        }
        return stubbedApi;
    }

    /**
     * 
     */
    public static void reset() {
        synchronized (FritzyApiStub.class) {
            if (stubbedApi == null) {
                return;
            }
            stubbedApi.orders = new Orders();
            stubbedApi.orders.setRecords(new Record[0]);
            stubbedApi.users = new ArrayList<>();
            stubbedApi.events = new ArrayList<>();
            stubbedApi.balances = new HashMap<>();
            stubbedApi.eventId = new AtomicInteger(0);
            stubbedApi.orderCounter = new AtomicInteger(0);
        }
    }

    @Override
    public void login(String user, String password) {
        log.debug("login {}", user);
        this.loginInUser = users.stream()
                .filter(u -> u.getEmail().equals(user))
                .findFirst()
                .orElseThrow(AssertionError::new);
    }

    @Override
    public WebUser register(String email, String username, String password) {
        log.debug("register {} ({})", email, username);
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
        log.debug("orders");
        GetOrdersResponse getOrdersResponse = new GetOrdersResponse();
        getOrdersResponse.setOrders(orders);
        return getOrdersResponse;
    }

    @Override
    public WebOrder order(String orderHash) {
        WebOrder order = Arrays.asList(orders.getRecords()).stream()
                .map(r -> r.getOrder())
                .filter(o -> o.getHash().equals(orderHash))
                .findFirst()
                .orElse(null);
        if (order != null) {
            if (order.getTakerAddress() != null) {
                log.debug("reading order from {} to {}", getEmail(order.getMakerAddress()),
                        getEmail(order.getTakerAddress()));
            } else {
                log.debug("reading order by {}", getEmail(order.getMakerAddress()));
            }
        }
        return order;
    }

    @Override
    public void mintEth(String address, BigDecimal value) throws FritzyApiException {
        // NOTE only used for transaction costs, not used for stub.
        mint(address, value, EContractAddress.ETH);
    }

    @Override
    public String fillOrder(String orderHash) {
        log.debug("fillOrder {}", orderHash);
        WebOrder order = order(orderHash);
        Preconditions.checkNotNull(order);
        Preconditions.checkArgument(order.getTakerAddress() == null);
        order.setTakerAddress(loginInUser.getAddress());

        log.debug("maker {} is making {}{} and is taking {}{} from taker {}",
                getEmail(order.getMakerAddress()),
                order.getMakerAssetAmount(), order.getMakerAssetData(),
                order.getTakerAssetAmount(), order.getTakerAssetData(),
                getEmail(order.getTakerAddress()));

        String otherUser;
        if (order.getMakerAddress().equals(loginInUser.getAddress())) {
            otherUser = getEmail(order.getTakerAddress());
        } else {
            otherUser = getEmail(order.getMakerAddress());
        }

        BigDecimal takerGet = new BigDecimal(order.getTakerAssetAmount());
        BigDecimal takerGive = new BigDecimal(order.getMakerAssetAmount());

        log.debug("{} gives {}{}", loginInUser.getEmail(), takerGive, order.getMakerAssetData());
        decr(EContractAddress.valueOf(order.getTakerAssetData().toUpperCase()), takerGet, order.getTakerAddress());

        log.debug("{} gets  {}{}", loginInUser.getEmail(), takerGet, order.getTakerAssetData());
        incr(EContractAddress.valueOf(order.getMakerAssetData().toUpperCase()), takerGive, order.getTakerAddress());

        BigDecimal makerGet = new BigDecimal(order.getMakerAssetAmount());
        BigDecimal makerGive = new BigDecimal(order.getTakerAssetAmount());

        log.debug("{} gets  {}{}", otherUser, makerGet, order.getMakerAssetData());
        decr(EContractAddress.valueOf(order.getMakerAssetData().toUpperCase()), makerGet, order.getMakerAddress());
        log.debug("{} gives {}{}", otherUser, makerGive, order.getTakerAssetData());
        incr(EContractAddress.valueOf(order.getTakerAssetData().toUpperCase()), makerGive, order.getMakerAddress());

        return generateHash(Objects.hash(orderHash));
    }

    private String getEmail(String address) {
        return users.stream()
                .filter(u -> u.getAddress().equals(address))
                .findFirst()
                .orElseThrow(AssertionError::new)
                .getEmail();
    }

    private void incr(EContractAddress contractaddress, BigDecimal takerAssetAmount, String takerAddress) {
        FritzyBalance balance = balances.computeIfAbsent(takerAddress, k -> new FritzyBalance());
        switch (contractaddress) {
        case EUR:
            BigDecimal newBalenceEur = balance.getEur().add(takerAssetAmount);
            balance.setEur(newBalenceEur);
            break;
        case KWH:
            BigDecimal newBalenceKwh = balance.getKwh().add(takerAssetAmount);
            balance.setKwh(newBalenceKwh);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void decr(EContractAddress contractaddress, BigDecimal takerAssetAmount, String takerAddress) {
        FritzyBalance balance = balances.computeIfAbsent(takerAddress, k -> new FritzyBalance());
        switch (contractaddress) {
        case EUR:
            BigDecimal newBalenceEur = balance.getEur().subtract(takerAssetAmount);
            Preconditions.checkArgument(newBalenceEur.doubleValue() >= 0d,
                    "Insufficient funds " + balance.getEur() + " " + takerAssetAmount);
            balance.setEur(newBalenceEur);
            break;
        case KWH:
            BigDecimal newBalenceKwh = balance.getKwh().subtract(takerAssetAmount);
            Preconditions.checkArgument(newBalenceKwh.doubleValue() >= 0d,
                    "Insufficient kWh (" + balance.getKwh() + ") for " + takerAssetAmount);
            balance.setKwh(newBalenceKwh);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void cancelOrder(String hash) {
        log.debug("cancelOrder {}", hash);
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
    public void log(EEventType tag, String msg, String dataStr) {
        String username = loginInUser != null ? loginInUser.getName() : "unknown";
        log.debug("log {} {} {}", tag, msg, dataStr);
        ApiEvent e = new ApiEvent();
        e.setId(eventId.getAndIncrement());
        e.setEnvironment(FritzyApiStub.class.getSimpleName());
        e.setActor(username);
        e.setMsg(msg);
        e.setTag(tag.getTag());
        e.setData(dataStr);
        e.setTimestamp(Instant.now().toString());
        e.setCreatedAt(Instant.now().toString());
        e.setUpdatedAt(Instant.now().toString());
        events.add(e);
    }

    @Override
    public FritzyBalance balance() {
        log.debug("balance");
        return balances.computeIfAbsent(loginInUser.getAddress(), k -> new FritzyBalance());
    }

    @Override
    public void mint(String address, BigDecimal value, EContractAddress contractAddress) {
        log.debug("mint {} {} to {}", value, contractAddress, address);
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
        log.debug("burn {} {}", value, contractAddress);
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
    public UsersResponseEntry[] getUsers() {
        log.debug("getUsers");
        return users.stream()
                .map(u -> new UsersResponseEntry(u.getName(), u.getEmail(), u.getAddress(), ""))
                .collect(Collectors.toList())
                .toArray(new UsersResponseEntry[users.size()]);
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
        log.debug("transfer {} {} to {}", value, contractAddress, toAddress);
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
        log.debug("getAddress {}", loginInUser.getAddress());
        return loginInUser.getAddress();
    }

    @Override
    public String createOrder(EContractAddress makerToken, EContractAddress takerToken, BigDecimal makerAmount,
            BigDecimal takerAmount) {
        log.debug("createOrder {} {} for {} {}", makerAmount, makerToken, takerAmount, takerToken);

        List<Record> ordersList = Lists.newArrayList(Arrays.asList(orders.getRecords()));
        WebOrder webOrder = new WebOrder();
        String generateHash = generateHash(orderCounter.getAndIncrement());
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

    @Override
    public GetEventResponse getEvents(Instant from, Instant till) {
        log.debug("getEvents {} to {} ", from, till);
        GetEventResponse getEventResponse = new GetEventResponse();
        List<ApiEvent> filteredEvents = getAllEvents().stream()
                .filter(e -> Instant.parse(e.getCreatedAt()).isAfter(from.minusNanos(1)))
                .filter(e -> Instant.parse(e.getCreatedAt()).isBefore(till.plusNanos(1)))
                .collect(Collectors.toList());
        getEventResponse.setEvents(filteredEvents);
        return getEventResponse;
    }

    @VisibleForTesting
    public List<ApiEvent> getAllEvents() {
        return Lists.newArrayList(events);
    }

    /**
     * @param type to get
     * @return vent
     */
    @VisibleForTesting
    public ApiEvent getFirstEventOfType(EEventType type) {
        return events.stream().filter(e -> type.getTag().equals(e.getTag())).findFirst().orElse(null);
    }

    /**
     * stub order
     * 
     * @param makerAddress from
     * @param takerAddress to
     * @param makerAmount how much
     * @param makerToken of
     * @param takerAmount to how much
     * @param takerToken of
     * @returnhash
     */
    @VisibleForTesting
    public String mockCompleteOrder(String makerAddress, String takerAddress, BigDecimal makerAmount,
            EContractAddress makerToken, BigDecimal takerAmount, EContractAddress takerToken) {
        List<Record> ordersList = Lists.newArrayList(Arrays.asList(orders.getRecords()));
        WebOrder webOrder = new WebOrder();
        String generateHash = generateHash(orderCounter.getAndIncrement());
        webOrder.setHash(generateHash);
        webOrder.setMakerAddress(makerAddress);
        webOrder.setTakerAddress(takerAddress);
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
