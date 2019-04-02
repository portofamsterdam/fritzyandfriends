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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.login.LoginParameters;
import nl.technolution.fritzy.wallet.login.LoginResponse;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Order;
import nl.technolution.fritzy.wallet.register.RegisterParameters;

/**
 * Access to acount for Fritzy
 */
public class FritzyApi {

    private final Logger log = Log.getLogger();
    private final String url;
    private Client client = JerseyClientBuilder.newClient();

    private String address;
    private String accessToken;

    /**
     *
     */
    public FritzyApi(String url) {
        this.url = url;
    }

    /**
     * @param user of login
     * @param password of login
     */
    public void login(String user, String password) {
        WebTarget target = client.target(url + "/login");
        Builder request = target.request();
        LoginParameters loginParameters = new LoginParameters();
        loginParameters.setEmail(user);
        loginParameters.setPassword(password);
        LoginResponse response = request.post(Entity.entity(loginParameters, MediaType.APPLICATION_JSON),
                LoginResponse.class);
        this.address = response.getUser().getAddress();
        this.accessToken = response.getAccessToken();
    }

    /**
     * @param email
     * @param user
     * @param password
     */
    public void register(String email, String user, String password) {
        WebTarget target = client.target(url + "/register");
        Builder request = target.request();

        RegisterParameters registerParameters = new RegisterParameters();
        registerParameters.setEmail(email);
        registerParameters.setName(user);
        registerParameters.setPassword(password);

        request.post(Entity.entity(registerParameters, MediaType.APPLICATION_JSON));
    }

    /**
     * Get al orders
     * 
     * @return
     */
    public GetOrdersResponse orders() {
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/orders");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        return request.get(GetOrdersResponse.class);
    }

    /**
     * Fill order by hash
     * 
     * @param orderHash
     * @return hash of order
     */
    public String fillOrder(String orderHash) {
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/me/order/" + orderHash);
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        WebOrder webOrder = request.post(Entity.entity(new Object(), MediaType.APPLICATION_JSON), WebOrder.class);
        return webOrder.getHash();
    }

    /**
     * @param order to create
     */
    public String createOrder(Order order) {
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/me/order");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        WebOrder orderResponse = request.post(Entity.entity(order, MediaType.APPLICATION_JSON), WebOrder.class);
        return orderResponse.getHash();
    }

    /**
     * @param hash
     */
    public void cancelOrder(String hash) {
        Preconditions.checkArgument(accessToken != null, "login first");
        WebTarget target = client.target(url + "/me/order/" + hash + "/cancel");
        Builder request = target.request();
        request.header("Authorization", "Bearer " + accessToken);
        request.post(Entity.entity(new Object(), MediaType.APPLICATION_JSON));
    }
}