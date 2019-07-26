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
package nl.technolution.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import nl.technolution.apis.ApiEndpoints;
import nl.technolution.dropwizard.services.ServiceFinder;
import nl.technolution.dropwizard.tasks.TimedTaskService;
import nl.technolution.dropwizard.webservice.WebserviceFinder;

/**
 * Basic app for Fritzy applications
 * 
 * @param <T> Dropwizard Configuration Type
 */
public class FritzyDropWizardApp<T extends FritzyAppConfig> extends Application<T> {

    public static final String PKG = "nl.technolution";

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        ServiceFinder.setupDropWizardServices(configuration);
        WebserviceFinder.setupWebservices(environment);
        ApiEndpoints.register(configuration.getApiConfig());
        environment.lifecycle().manage(new TimedTaskService());
    }
}
