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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;

import nl.technolution.IJsonnable;
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
    private static final String BURN_ADDRESS = "0x0000000000000000000000000000000000000000";

    private final ObjectMapper mapper = new ObjectMapper();
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
    public void login(String user, String password) {
        LOG.info("Login user {}", user);
        WebTarget target = client.target(url + "/login");
        Builder request = target.request();
        LoginParameters loginParameters = new LoginParameters();
        loginParameters.setEmail(user);
        loginParameters.setPassword(password);
        Response response = request.post(Entity.entity(loginParameters, MediaType.APPLICATION_JSON));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.error("login failed: {}", response);
            throw new IllegalStateException("Unable to login as " + user);
        }
        LoginResponse login = response.readEntity(LoginResponse.class);
        this.address = login.getUser().getAddress();
        this.actor = login.getUser().getName();
        this.accessToken = login.getAccessToken();

    }

    /**
     * @param email
     * @param user
     * @param password
     */
    @Override
    public WebUser register(String email, String user, String password) {
        LOG.info("Register user {} ({})", email, user);
        WebTarget target = client.target(url + "/register");
        Builder request = target.request();

        RegisterParameters registerParameters = new RegisterParameters();
        registerParameters.setEmail(email);
        registerParameters.setName(user);
        registerParameters.setPassword(password);

        return request.post(Entity.entity(registerParameters, MediaType.APPLICATION_JSON), WebUser.class);
    }


    @Override
    public void addMinter(String address, EContractAddress contractAddress) {
        LOG.info("Adding minter {}", address);
        WebTarget target = client.target(url + "/node/addMinter");
        Builder request = target.request();
        Form form = new Form();
        form.param("address", address);
        form.param("contractAddress", contractAddress.getContractName());
        request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    }

    /**
     * Get all orders
     * 
     * @return
     */
    @Override
    public GetOrdersResponse orders() {
        LOG.info("Fetching Orders");
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/orders");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.get();
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("orders() failed: {}", response);
        }
        return request.get(GetOrdersResponse.class);
    }

    /**
     * Get an order
     * 
     * @return
     */
    @Override
    public WebOrder order(String orderHash) {
        LOG.info("Fetching order {}", orderHash);
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/orders/" + orderHash);
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        return request.get(WebOrder.class);
    }

    /**
     * Fill order by hash
     * 
     * @param orderHash
     * @return hash of order
     */
    @Override
    public String fillOrder(String orderHash) {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Filling order {} by {}", orderHash, actor);
        WebTarget target = client.target(url + "/me/order/" + orderHash);
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Response response = request.post(Entity.json(null));

        // LOG.debug("Response: " + response.readEntity(String.class));

        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("createOrder failed(): " + response);
        }
        return response.readEntity(FillOrderResponse.class).getTransactionHash();
    }

    @Override
    public String createOrder(EContractAddress makerToken, EContractAddress takerToken, BigDecimal makerAmount,
            BigDecimal takerAmount) {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Creating order {} {} for {} {} by {}", makerAmount, makerToken, takerAmount, takerToken, actor);
        WebTarget target = client.target(url + "/me/order");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("makerToken", makerToken.getContractName());
        form.param("takerToken", takerToken.getContractName());
        form.param("makerAmount", makerAmount.multiply(TOKEN_FACTOR).toPlainString());
        form.param("takerAmount", takerAmount.multiply(TOKEN_FACTOR).toPlainString());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("createOrder failed(): " + response);
        }
        return response.readEntity(CreateOrderResponse.class).getOrder().getHash();
    }

    /**
     * @param hash
     */
    @Override
    public void cancelOrder(String hash) {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Cancel order {} by {}", hash, actor);
        WebTarget target = client.target(url + "/me/order/" + hash + "/cancel");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);

        Response response = request.post(Entity.json(null));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("cancelOrder failed: " + response);
        }
    }

    /**
     * Get balance
     * 
     * @return balance
     */
    @Override
    public FritzyBalance balance() {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("Fetching balance of {}", actor);
        WebTarget target = client.target(url + "/me/balance");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        return request.get(Balance.class).getBalance();
    }

    /**
     * Mint value to an address
     * 
     * @param address to send value to
     * @param value to mint
     */
    @Override
    public void mint(String address, BigDecimal value, EContractAddress contractAddress) {
        Preconditions.checkArgument(accessToken != null, "login first");
        BigDecimal tokens = value.multiply(TOKEN_FACTOR);
        LOG.info("Minting {} {} to {} tokens: {}", value, contractAddress, actor, tokens);
        WebTarget target = client.target(url + "/me/token/mint");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("address", address);

        form.param("value", "" + tokens.toPlainString());
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("mint() failed: {}", response);
        }
    }


    @Override
    public void burn(BigDecimal value, EContractAddress contractAddress) {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("{} is burning {} {}", actor, value, contractAddress.getContractName());
        WebTarget target = client.target(url + "/me/token/burn");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("value", "" + value.multiply(TOKEN_FACTOR));
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("burn failed: {}", response);
        }
    }

    @Override
    public String transfer(BigDecimal value, EContractAddress contractAddress, String toAddress) {
        Preconditions.checkArgument(accessToken != null, "login first");
        LOG.info("{} is tranfering {} {} to {}", actor, value, contractAddress.getContractName(), toAddress);
        WebTarget target = client.target(url + "/me/token/transfer");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        Form form = new Form();
        form.param("to", toAddress);
        form.param("value", "" + value.multiply(TOKEN_FACTOR));
        form.param("contractAddress", contractAddress.getContractName());
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("transfer failed: {}", response);
        }
        return response.readEntity(WebTransaction.class).getTx();
    }

    /**
     * Get users
     * 
     * @return all users
     */
    @Override
    public WebUser[] getUsers() {
        LOG.info("Fetching all users");
        WebTarget target = client.target(url + "/users");
        Builder request = target.request();
        return request.get(WebUser[].class);
    }

    /**
     * @param tag
     * @param msg
     * @param data
     */
    @Override
    public void log(EEventType tag, String msg, IJsonnable data) {
        LOG.info("Logging event {}: {}", tag, msg != null ? msg : "");
        String dataString;
        try {
            dataString = data == null ? null : mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            dataString = "<eventdata not found>";
        }
        WebTarget target = client.target(url + "/event");
        Builder request = target.request();
        Form form = new Form();
        form.param("environment ", "test");
        form.param("actor", actor);
        form.param("msg ", msg);
        form.param("tag ", tag.getTag());
        form.param("timestamp ", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        form.param("data", dataString);
        Response response = request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.warn("log event failed: {}", response);
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

}
