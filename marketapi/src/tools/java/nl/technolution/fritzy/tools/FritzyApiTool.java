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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Order;
import nl.technolution.fritzy.wallet.order.Record;

/**
 * Tool to test API calls
 */
public class FritzyApiTool {

    private static final Logger LOG = Log.getLogger();

    private static final String ADMIN = "juriaa4thrthn@oneup.company";
    private static final String ADMINPASS = "abc123";
    private static final String PASS = "qazqaz";
    private static final String USER1 = "test@fiets.be";
    private static final String USER2 = "testcase@martin.nl";

    private static FritzyApi api;

    public static void main(String[] args) {
        String url = "http://82.196.13.251/api";
        api = new FritzyApi(url, "FritzyApiTool");

        // setMinters(ADMIN, ADMINPASS, USER1, USER2);
        resetUser(USER1, PASS);
        resetUser(USER2, PASS);

        // api.register(USER, "test", PASS);
        api.login(USER1, PASS);
        String user1Address = api.getAddress();
        LOG.info("{} addres {}", USER1, user1Address);
        FritzyBalance balance = api.balance();
        BigDecimal euros = balance.getEur();
        LOG.info("{} balance {}", USER1, balance);
        BigDecimal monies = BigDecimal.valueOf(10L);

        // user 1 mints 10 kwh
        api.mint(user1Address, monies, EContractAddress.KWH);

        // offer 10 kwh for 10 eur
        Order order = new Order();
        order.setMakerAmount(monies.toPlainString());
        order.setMakerToken(EContractAddress.KWH.getContractName());
        order.setTakerAmount(monies.toPlainString());
        order.setTakerToken(EContractAddress.EUR.getContractName());
        String txId = api.createOrder(order);

        // Check the created order
        Arrays.asList(api.orders().getOrders().getRecords())
                .stream()
                .map(r -> r.getOrder())
                .filter(o -> o.getHash().equals(txId))
                .findFirst()
                .orElseThrow(AssertionError::new);
        
        api.login(USER2, PASS);
        String user2Address = api.getAddress();

        balance = api.balance();
        LOG.info("{} balance {}", USER2, balance);
        BigDecimal kwhs = balance.getEur();
        api.mint(user1Address, monies, EContractAddress.EUR);

        api.fillOrder(txId);
        // sleep a bit for tranaction to complete
        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);

        balance = api.balance();
        LOG.info("{} balance {}", USER2, balance);
        Preconditions.checkArgument(kwhs.add(monies).equals(balance.getKwh()));
        BigDecimal user2eurBalanceAfterOrder = balance.getEur();

        api.login(USER1, PASS);
        balance = api.balance();
        LOG.info("{} balance {}", USER1, balance);
        Preconditions.checkArgument(euros.add(monies).equals(balance.getEur()));
        
        BigDecimal user1eurBalanceAfterOrder = balance.getEur();
        api.transfer(user1eurBalanceAfterOrder, EContractAddress.EUR, user2Address);

        api.login(USER2, PASS);
        BigDecimal currentBalance = api.balance().getEur();
        Preconditions.checkArgument(user2eurBalanceAfterOrder.add(user1eurBalanceAfterOrder).equals(currentBalance));
        api.burn(currentBalance, EContractAddress.EUR);

    }

    private static void setMinters(String adminUser, String adminpass, String... newMinter) {
        for (String user : newMinter) {
            // Login as user to get address
            api.login(user, PASS);
            String address = api.getAddress();

            // log in as admin to set minter
            api.login(adminUser, adminpass);
            api.addMinter(address, EContractAddress.EUR);
            api.addMinter(address, EContractAddress.KWH);
        }
    }

    private static void resetUser(String user, String password) {
        api.login(user, password);
        for (Record record : api.orders().getOrders().getRecords()) {
            WebOrder order = record.getOrder();
            if (order.getMakerAddress().equals(api.getAddress())) {
                api.cancelOrder(order.getHash());
            }
        }
        FritzyBalance balance = api.balance();
        if (balance.getKwh().doubleValue() != 0.0d) {
            api.burn(balance.getKwh(), EContractAddress.KWH);
        }
        if (balance.getEur().doubleValue() != 0.0d) {
            api.burn(balance.getEur(), EContractAddress.EUR);
        }
        LOG.info("After cleaning {}", api.balance());
    }
}
