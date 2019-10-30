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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.model.EContractAddress;

public class FritzyDefaults {

    private static final Logger LOG = Log.getLogger();

    public static void main(String[] args) throws FritzyApiException {

        String url = "http://192.168.8.242/api";
        FritzyApi api = new FritzyApi(url, "FritzyApiTool");

        LOG.info("Creating users");
        Set<String> existingUsers = Arrays.asList(api.getUsers()).stream()
                .map(u -> u.getName())
                .collect(Collectors.toSet());

        List<String> users = Lists.newArrayList("sunny", "fritzy", "batty", "exxy", "netty");
        List<String> addresses = Lists.newArrayList();
        for (String user : users) {
            String username = user + "@fritzy.nl";
            if (!existingUsers.contains(user)) {
                LOG.info("Creating user {}", user);
                api.register(username, user, user);
            }
            api.login(username, user);
            addresses.add(api.getAddress());
        }

        String adminuser = "admin@fritzy.nl";
        String password = "admin";
        if (!existingUsers.contains("admin")) {
            LOG.info("Creating admin {}", adminuser);
            api.register(adminuser, password, password);
        }
        api.login(adminuser, password);

        api.addMinter(api.getAddress(), EContractAddress.EUR);
        api.addMinter(api.getAddress(), EContractAddress.KWH);

        for (String address : addresses) {
            LOG.info("Add kWh minting rights to {}", address);
            api.addMinter(address, EContractAddress.KWH);
            int eur = 100;
            LOG.info("Add {} eur to {}", eur, address);
            api.mint(address, BigDecimal.valueOf(eur), EContractAddress.EUR);
        }
    }
}
