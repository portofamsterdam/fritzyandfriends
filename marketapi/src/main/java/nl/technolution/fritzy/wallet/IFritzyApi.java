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

import nl.technolution.IJsonnable;
import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;
import nl.technolution.fritzy.wallet.order.Order;

/**
 * 
 */
public interface IFritzyApi {

    /**
     * @param user of login
     * @param password of login
     */
    void login(String user, String password);

    /**
     * @param email
     * @param user
     * @param password
     * @return
     */
    WebUser register(String email, String user, String password);

    /**
     * Get all orders
     * 
     * @return
     */
    GetOrdersResponse orders();

    /**
     * Get an order
     * 
     * @return
     */
    WebOrder order(String orderHash);

    /**
     * Fill order by hash
     * 
     * @param orderHash
     * @return hash of order
     */
    String fillOrder(String orderHash);

    /**
     * @param order to create
     */
    String createOrder(Order order);


    /**
     * @param hash
     */
    void cancelOrder(String hash);

    /**
     * Get balance
     * 
     * @return balance
     */
    FritzyBalance balance();

    /**
     * @param tag
     * @param msg
     * @param data
     */
    void log(EEventType tag, String msg, IJsonnable data);

    /**
     * mint tokens. E.g. kwh, eur or eth
     * 
     * @param address to send tokens to
     * @param value amount to mint
     * @param contractAddress type of token to mint.
     */
    void mint(String address, BigDecimal value, EContractAddress contractAddress);

    /**
     * burn tokens
     * 
     * @param value amount to burn
     * @param contractAddress token to burn
     */
    void burn(BigDecimal value, EContractAddress contractAddress);

    /**
     * Get all known users registered in the market
     * 
     * @return registered users
     */
    WebUser[] getUsers();


    /**
     * @param address who can mint
     * @param contractAddress what can be minted
     */
    void addMinter(String address, EContractAddress contractAddress);

    /**
     * Transfer money to an address
     * 
     * @param value how much
     * @param contractAddress of this
     * @param toAddress to
     * @return txId
     */
    String transfer(BigDecimal value, EContractAddress contractAddress, String toAddress);

    /**
     * Get address of logged-in user
     * 
     * @return address
     */
    String getAddress();

}