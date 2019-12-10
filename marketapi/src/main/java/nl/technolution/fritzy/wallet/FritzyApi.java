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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebTransaction;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.login.LoginParameters;
import nl.technolution.fritzy.wallet.login.LoginResponse;
import nl.technolution.fritzy.wallet.model.Balance;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.model.GetEventResponse;
import nl.technolution.fritzy.wallet.model.HashlessOrder;
import nl.technolution.fritzy.wallet.model.OrderResponse;
import nl.technolution.fritzy.wallet.model.UsersResponseEntry;
import nl.technolution.fritzy.wallet.order.CreateOrderResponse;
import nl.technolution.fritzy.wallet.order.FillOrderResponse;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.register.RegisterParameters;

/**
 * Access to acount for Fritzy
 */
public class FritzyApi implements IFritzyApi {
    private static final BigDecimal TOKEN_FACTOR = new BigDecimal("1000000000000000000");
    private static final Logger LOG = Log.getLogger();

    private final String url;
    private final String environment;

    private Client client = JerseyClientBuilder.newClient();

    private String actor;
    private String address;
    private String accessToken;

    /**
     *
     */
    public FritzyApi(String url, String environment) {
        this.url = url;
        this.environment = environment;
    }

    /**
     * @param user of login
     * @param password of login
     */
    @Override
    public void login(String user, String password) throws FritzyApiException {
        LOG.info("Login user {}", user);
        WebTarget target = client.target(url + "/login");
        Builder request = target.request();
        LoginParameters loginParameters = new LoginParameters();
        loginParameters.setEmail(user);
        loginParameters.setPassword(password);
        Response response = request.post(Entity.entity(loginParameters, MediaType.APPLICATION_JSON));
        FritzyApiException.checkResponse(response);
        LoginResponse login = response.readEntity(LoginResponse.class);
        this.address = login.getUser().getAddress();
        this.actor = login.getUser().getName();
        this.accessToken = login.getAccessToken();

    }

    /**
     * @param email of user
     * @param user name
     * @param password pw
     * @throws FritzyApiException when registration fails
     */
    @Override
    public WebUser register(String email, String user, String password) throws FritzyApiException {
        LOG.info("Register user {} ({})", email, user);
        WebTarget target = client.target(url + "/register");
        Builder request = target.request();

        RegisterParameters registerParameters = new RegisterParameters();
        registerParameters.setEmail(email);
        registerParameters.setName(user);
        registerParameters.setPassword(password);
        Response response = request.post(Entity.entity(registerParameters, MediaType.APPLICATION_JSON));
        FritzyApiException.checkResponse(response);
        return null;
    }


    @Override
    public void addMinter(String address, EContractAddress contractAddress) throws FritzyApiException {
        LOG.info("Adding minter {}", address);
        WebTarget target = client.target(url + "/node/addMinter");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("address", address);
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
    }

    @Override
    public void mintEth(String address, BigDecimal value) throws FritzyApiException {
        LOG.info("Minter eth to {}", address);
        WebTarget target = client.target(url + "/node/mintEth");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("address", address);
        form.param("value", "" + value.multiply(TOKEN_FACTOR).toBigInteger().toString());
        form.param("contractAddress", EContractAddress.ETH.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
    }

    /**
     * Get all orders
     * 
     * @return
     * @throws FritzyApiException
     */
    @Override
    public GetOrdersResponse orders() throws FritzyApiException {
        LOG.info("Fetching Orders");
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/orders");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.get();
        FritzyApiException.checkResponse(response);
        return response.readEntity(GetOrdersResponse.class);
    }

    /**
     * Get an order
     * 
     * @return
     * @throws FritzyApiException
     */
    @Override
    public WebOrder order(String orderHash) throws FritzyApiException {
        LOG.info("Fetching order {}", orderHash);
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/order/" + orderHash);
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.get();
        try {
            FritzyApiException.checkResponse(response);
        } catch (FritzyApiException e) {
            // Order probably doesn't exist, convert to null
            return null;
        }
        OrderResponse ordercontainer = response.readEntity(OrderResponse.class);
        return convert(ordercontainer, orderHash);
    }

    /**
     * Fill order by hash
     * 
     * @param orderHash of order to fill
     * @return hash of order
     * @throws FritzyApiException
     */
    @Override
    public String fillOrder(String orderHash) throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Filling order {} by {}", orderHash, actor);
        WebTarget target = client.target(url + "/me/order/" + orderHash);
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.post(Entity.json(null));

        FritzyApiException.checkResponse(response);
        return response.readEntity(FillOrderResponse.class).getTransactionHash();
    }

    @Override
    public String createOrder(EContractAddress makerToken, EContractAddress takerToken, BigDecimal makerAmount,
            BigDecimal takerAmount) throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Creating order {} {} for {} {} by {}", makerAmount, makerToken, takerAmount, takerToken, actor);
        WebTarget target = client.target(url + "/me/order");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("makerToken", makerToken.getContractName());
        form.param("takerToken", takerToken.getContractName());
        form.param("makerAmount", makerAmount.multiply(TOKEN_FACTOR).toBigInteger().toString());
        form.param("takerAmount", takerAmount.multiply(TOKEN_FACTOR).toBigInteger().toString());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
        return response.readEntity(CreateOrderResponse.class).getOrder().getHash();
    }

    /**
     * @param hash
     * @throws FritzyApiException
     */
    @Override
    public void cancelOrder(String hash) throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Cancel order {} by {}", hash, actor);
        WebTarget target = client.target(url + "/me/order/" + hash + "/cancel");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);

        Response response = request.post(Entity.json(null));
        FritzyApiException.checkResponse(response);
    }

    /**
     * Get balance
     * 
     * @return balance
     * @throws FritzyApiException
     */
    @Override
    public FritzyBalance balance() throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Fetching balance of {}", actor);
        WebTarget target = client.target(url + "/me/balance");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.get();
        FritzyApiException.checkResponse(response);
        return response.readEntity(Balance.class).getBalance();
    }

    /**
     * Mint value to an address
     * 
     * @param address to send value to
     * @param value to mint
     * @throws FritzyApiException
     */
    @Override
    public void mint(String address, BigDecimal value, EContractAddress contractAddress) throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        BigDecimal tokens = value.multiply(TOKEN_FACTOR);
        LOG.info("Minting {} {} by {} for {} tokens: {}", value, contractAddress, actor, address, tokens);
        WebTarget target = client.target(url + "/me/token/mint");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("address", address);

        form.param("value", "" + tokens.toBigInteger().toString());
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
    }


    @Override
    public void burn(BigDecimal value, EContractAddress contractAddress) throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("{} is burning {} {}", actor, value, contractAddress.getContractName());
        WebTarget target = client.target(url + "/me/token/burn");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("value", "" + value.multiply(TOKEN_FACTOR).toBigInteger());
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
    }

    @Override
    public String transfer(BigDecimal value, EContractAddress contractAddress, String toAddress)
            throws FritzyApiException {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("{} is tranfering {} {} to {}", actor, value, contractAddress.getContractName(), toAddress);
        WebTarget target = client.target(url + "/me/token/transfer");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("to", toAddress);
        form.param("value", "" + value.multiply(TOKEN_FACTOR).toBigInteger());
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
        return response.readEntity(WebTransaction.class).getTx();
    }

    /**
     * Get users
     * 
     * @return all users
     * @throws FritzyApiException
     */
    @Override
    public UsersResponseEntry[] getUsers() throws FritzyApiException {
        LOG.info("Fetching all users");
        WebTarget target = client.target(url + "/users");
        Builder request = target.request();
        Response response = request.get();
        FritzyApiException.checkResponse(response);
        return response.readEntity(UsersResponseEntry[].class);
    }

    /**
     * @param tag of event
     * @param msg information about event
     * @param data json object with actor specific information
     * @throws FritzyApiException
     */
    @Override
    public void log(EEventType tag, String msg, String dataString) throws FritzyApiException {
        LOG.info("Logging event {}: {}", tag, msg != null ? msg : "");
        WebTarget target = client.target(url + "/event");
        Builder request = target.request();
        Form form = new Form();
        form.param("environment", environment);
        form.param("actor", actor);
        form.param("msg", msg);
        form.param("tag", tag.getTag());
        ZonedDateTime now = ZonedDateTime.now();
        form.param("timestamp", now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        long epoch = now.toEpochSecond();
        long roundId = epoch - (epoch % 900);
        form.param("roundId", "" + roundId);
        form.param("data", dataString);
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        FritzyApiException.checkResponse(response);
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public GetEventResponse getEvents(Instant from, Instant to) throws FritzyApiException {
        LOG.info("Getting events from {} till {}", from, to);

        DateTimeFormatter fromatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String fromParam = ZonedDateTime.ofInstant(from, ZoneId.of("UTC")).format(fromatter);
        String toParam = ZonedDateTime.ofInstant(to, ZoneId.of("UTC")).format(fromatter);
        String targetUrl = String.format("%s/event?to=%s&from=%s", url, toParam, fromParam);
        WebTarget target = client.target(targetUrl);

        Builder request = target.request();
        Response response = request.get();
        FritzyApiException.checkResponse(response);
        return response.readEntity(GetEventResponse.class);
    }

    private static WebOrder convert(OrderResponse singleOrder, String hash) {
        WebOrder o = new WebOrder();
        HashlessOrder hashlessOrder = singleOrder.getMetaDataOrder().getHashlessOrder();
        o.setHash(hash);
        o.setSignature(hashlessOrder.getSignature());
        o.setSenderAddress(hashlessOrder.getSenderAddress());
        o.setMakerAddress(hashlessOrder.getMakerAddress());
        o.setTakerAddress(hashlessOrder.getTakerAddress());
        o.setMakerFee(hashlessOrder.getMakerFee());
        o.setTakerFee(hashlessOrder.getTakerFee());
        o.setMakerAssetAmount(hashlessOrder.getMakerAssetAmount());
        o.setTakerAssetAmount(hashlessOrder.getTakerAssetAmount());
        o.setMakerAssetData(hashlessOrder.getMakerAssetData());
        o.setTakerAssetData(hashlessOrder.getTakerAssetData());
        o.setSalt(hashlessOrder.getSalt());
        o.setExchangeAddress(hashlessOrder.getExchangeAddress());
        o.setFeeRecipientAddress(hashlessOrder.getFeeRecipientAddress());
        o.setExpirationTimeSeconds(hashlessOrder.getExpirationTimeSeconds());
        return o;
    }
}