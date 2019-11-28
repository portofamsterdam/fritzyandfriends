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
package nl.technolution.actorsetup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.OrderHelper;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.model.UsersResponseEntry;
import nl.technolution.fritzy.wallet.order.Record;

/**
 * 
 */
public final class Defaults {

    private static final Logger LOG = LoggerFactory.getLogger(Defaults.class);
    private static final List<String> USERS = Lists.newArrayList("sunny", "fritzy", "batty", "exxy", "netty");

    private static final String ADMIN_USER = "admin@fritzy.nl";
    private static final String ADMIN_PASSWORD = "admin";

    private static final String HELP = "help";
    private static final String HOST_OPTION = "host";
    private static final String CREATE = "createuser";
    private static final String MINT_EUR = "minteur";
    private static final String MINT_ETH = "minteth";
    private static final String PRINT_BALANCE = "balance";
    private static final String CANCEL = "cancelorders";

    /**
     * @param args
     * @throws FritzyApiException
     */
    public static void main(String[] args) throws FritzyApiException {
        Options options = new Options();
        Option host = new Option(HOST_OPTION, true, "url to API e.g. http://192.168.8.242/api");
        host.setRequired(true);
        options.addOption(host);
        options.addOption(HELP, false, "print help");
        options.addOption(CANCEL, false, "Cancel all open order");
        options.addOption(CREATE, false, "Create users");
        options.addOption(MINT_ETH, false, "Mint eth to users");
        options.addOption(MINT_EUR, false, "Mint eur to users");
        options.addOption(PRINT_BALANCE, false, "Show balance of all users");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption(HELP)) {
                printUsage(options);
                return;
            }
        } catch (ParseException e1) {
            System.err.println("Invalid argument " + e1.getMessage());
            printUsage(options);
            return;
        }

        FritzyApi api = new FritzyApi(cmd.getOptionValue(HOST_OPTION), "FritzyApiTool");
        runScript(api, cmd);
        LOG.info("Done");
    }

    private static void runScript(IFritzyApi api, CommandLine cmd) throws FritzyApiException {
        if (cmd.hasOption(CREATE)) {
            createUsers(api);
        }
        if (cmd.hasOption(MINT_EUR) || cmd.hasOption(MINT_ETH)) {
            mint(api, cmd.hasOption(MINT_EUR), cmd.hasOption(MINT_ETH));
        }
        if (cmd.hasOption(PRINT_BALANCE)) {
            logBalance(api);
        }
        if (cmd.hasOption(CANCEL)) {
            cancelOrders(api);
        }
    }

    private static void cancelOrders(IFritzyApi api) throws FritzyApiException {
        LOG.info("Clearing orders");
        List<Record> orders = Arrays.asList(api.orders().getOrders().getRecords());
        for (String user : USERS) {
            api.login(getUsername(user), user);
            orders.stream()
                    .filter(o -> o.getOrder().getMakerAddress().equals(api.getAddress()))
                    .filter(o -> !OrderHelper.isAccepted(o.getOrder()))
                    .forEach(o -> {
                        try {
                            api.cancelOrder(o.getOrder().getHash());
                        } catch (FritzyApiException e) {
                            LOG.warn("Unable to cancel order {} ", o.getOrder().getHash());
                        }
                    });

        }

    }

    private static void createUsers(IFritzyApi api) throws FritzyApiException {
        LOG.info("Checking existing users");
        Map<String, UsersResponseEntry> existingUsers;
        try {
            existingUsers = Arrays.asList(api.getUsers()).stream()
                    .collect(Collectors.toMap(u -> u.getName(), u -> u));
        } catch (Exception e1) {
            LOG.info("Unable to get users from API, assume there aren't any");
            existingUsers = Maps.newHashMap();
        }

        LOG.info("Creating users");
        for (String user : USERS) {
            if (!existingUsers.containsKey(user)) {
                LOG.info("Creating user {}", user);
                api.register(getUsername(user), user, user);
            }
        }

        if (!existingUsers.containsKey("admin")) {
            LOG.info("Creating admin {}", ADMIN_USER);
            api.register(ADMIN_USER, ADMIN_PASSWORD, ADMIN_PASSWORD);
        }
        api.login(ADMIN_USER, ADMIN_PASSWORD);
        for (EContractAddress addr : Lists.newArrayList(EContractAddress.EUR, EContractAddress.KWH)) {
            try {
                LOG.info("Making sure admin can mint {}", addr);
                api.addMinter(api.getAddress(), addr);
            } catch (FritzyApiException e) {
                LOG.warn("Admin already is minter for {}", addr);
            }
        }

        existingUsers.values().forEach(u -> {
            try {
                api.addMinter(u.getAddress(), EContractAddress.KWH);
            } catch (FritzyApiException e) {
                LOG.error("Failed to promote {} to kWh minter: {}", u.getName(), e.getMessage());
            }
        });
    }

    private static void mint(IFritzyApi api, boolean mintEur, boolean mintEth) throws FritzyApiException {
        api.login(ADMIN_USER, ADMIN_PASSWORD);
        Map<String, UsersResponseEntry> existingUsers = Arrays.asList(api.getUsers()).stream()
                .collect(Collectors.toMap(u -> u.getName(), u -> u));

        USERS.stream().map(u -> existingUsers.get(u)).forEach(wu -> {
            try {
                if (mintEur) {
                    int eur = 100;
                    LOG.info("Giving {} eur to {}", eur, wu.getName());
                    api.mint(wu.getAddress(), BigDecimal.valueOf(eur), EContractAddress.EUR);

                }
                if (mintEth) {
                    long eth = 1L;
                    LOG.info("Giving {} eth to {}", eth, wu.getName());
                    api.mintEth(wu.getAddress(), BigDecimal.valueOf(eth));
                }
            } catch (FritzyApiException e) {
                LOG.info("Failed to mint", e);
            }
        });

    }

    private static void logBalance(IFritzyApi api) throws FritzyApiException {
        for (String user : USERS) {
            api.login(getUsername(user), user);
            FritzyBalance balance = api.balance();
            LOG.info("Balance {} is now {}", user, balance);
        }
    }

    private static String getUsername(String user) {
        return user + "@fritzy.nl";
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Fritzy Defaults", options);
    }

}
