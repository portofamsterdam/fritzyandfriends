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
package nl.technolution.dropwizard.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.technolution.dropwizard.FritzyAppConfig;

/**
 * 
 */
public class ServiceTest {

    @Test
    public void setupTest() {
        ServiceFinder.setupDropWizardServices(new TestConfig());
        assertTrue(Services.get(ITestService.class).getInit());
    }

    public static class TestConfig extends FritzyAppConfig {

    }

    public interface ITestService extends IService<FritzyAppConfig> {
        boolean getInit();
    }

    public static class TestService implements ITestService {

        private boolean init = false;

        @Override
        public void init(FritzyAppConfig config) {
            init = true;
        }

        @Override
        public boolean getInit() {
            return init;
        }
    }
}
