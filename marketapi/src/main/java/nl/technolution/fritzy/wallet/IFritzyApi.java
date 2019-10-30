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

import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.model.GetEventResponse;
import nl.technolution.fritzy.wallet.order.GetOrdersResponse;

/**
 * 
 */
public interface IFritzyApi {

    /**
     * @param user of login
     * @param password of login
     */
    void login(String user, String password) throws FritzyApiException;

    /**
     * @param email
     * @param user
     * @param password
     * @return
     */
    WebUser register(String email, String user, String password) throws FritzyApiException;

    /**
     * Get all orders
     * 
     * @return
     */
    GetOrdersResponse orders() throws FritzyApiException;

    /**
     * Get an order
     * 
     * @return
     */
    WebOrder order(String orderHash) throws FritzyApiException;

    /**
     * Fill order by hash
     * 
     * @param orderHash
     * @return hash of order
     */
    String fillOrder(String orderHash) throws FritzyApiException;

    /**
     * @param makerToken what do you sell
     * @param takerToken what do you buy
     * @param makerAmount how many do you sell
     * @param takerAmount how many
     * @return tx Hash
     */
    String createOrder(EContractAddress makerToken, EContractAddress takerToken, BigDecimal makerAmount,
            BigDecimal takerAmount) throws FritzyApiException;

    /**
     * @param hash
     */
    void cancelOrder(String hash) throws FritzyApiException;

    /**
     * Get balance
     * 
     * @return balance
     */
    FritzyBalance balance() throws FritzyApiException;

    /**
     * @param tag
     * @param msg
     * @param data
     */
    void log(EEventType tag, String msg, String data) throws FritzyApiException;

    /**
     * mint tokens. E.g. kwh, eur or eth
     * 
     * @param address to send tokens to
     * @param value amount to mint
     * @param contractAddress type of token to mint.
     */
    void mint(String address, BigDecimal value, EContractAddress contractAddress) throws FritzyApiException;

    /**
     * burn tokens
     * 
     * @param value amount to burn
     * @param contractAddress token to burn
     */
    void burn(BigDecimal value, EContractAddress contractAddress) throws FritzyApiException;

    /**
     * Get all known users registered in the market
     * 
     * @return registered users
     */
    WebUser[] getUsers() throws FritzyApiException;


    /**
     * @param address who can mint
     * @param contractAddress what can be minted
     */
    void addMinter(String address, EContractAddress contractAddress) throws FritzyApiException;

    /**
     * Transfer money to an address
     * 
     * @param value how much
     * @param contractAddress of this
     * @param toAddress to
     * @return txId
     */
    String transfer(BigDecimal value, EContractAddress contractAddress, String toAddress) throws FritzyApiException;

    /**
     * Get address of logged-in user
     * 
     * @return address
     */
    String getAddress();

    /**
     * 
     */
    GetEventResponse getEvents(Instant from, Instant till) throws FritzyApiException;

}