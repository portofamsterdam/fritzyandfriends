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

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dashboard.EEventType;
import nl.technolution.fritzy.wallet.FritzyApi;

/**
 * 
 */
public class FritzyApiTester {

    private static final Logger LOG = Log.getLogger();

    private static final String PASS = "qazqaz";
    private static final String USER1 = "test@fiets.be";

    private static FritzyApi api;

    /**
     * @param args
     */
    public static void main(String[] args) {

        String url = "http://82.196.13.251/api";
        api = new FritzyApi(url, "FritzyApiTool");

        api.login(USER1, PASS);

        api.log(EEventType.CHAT, "Fiets", null);
    }

}
