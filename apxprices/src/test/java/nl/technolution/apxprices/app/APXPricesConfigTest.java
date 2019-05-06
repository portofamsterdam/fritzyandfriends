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
package nl.technolution.apxprices.app;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test APXPricesConfig
 * 
 */
public class APXPricesConfigTest {
    @Test
    public void livePricesTest() {
        APXPricesConfig config = new APXPricesConfig("url", "secret", null, false);
        assertThat(config.getBaseURL(), is("url"));
        assertThat(config.getSecurityToken(), is("secret"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void livePricesNoUrlTest() {
        new APXPricesConfig(null, "secret", null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void livePricesEmptySecretTest() {
        new APXPricesConfig("url", "", null, false);
    }

    @Test
    public void fixedPricesTest() {
        fixedPricesTest(24);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noFixedPricesTest() {
        new APXPricesConfig("", "", null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void misingFixedPricesTest() {
        // only 23 entries should rise exception
        fixedPricesTest(23);
    }

    private void fixedPricesTest(int entries) {
        Map<Integer, Double> fixedPrices = new HashMap<Integer, Double>();
        for (int i = 0; i < entries; i++) {
            fixedPrices.put(i, (double)i / 100);
        }
        APXPricesConfig config = new APXPricesConfig("", "", fixedPrices, true);
        assertThat(config.getFixedPrices(), is(fixedPrices));
        assertThat(config.isUseFixedPrices(), is(true));
    }
}
