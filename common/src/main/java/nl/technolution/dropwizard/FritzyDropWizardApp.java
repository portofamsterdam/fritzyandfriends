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
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import nl.technolution.dropwizard.services.ServiceFinder;
import nl.technolution.dropwizard.tasks.TimedTaskService;
import nl.technolution.dropwizard.webservice.WebserviceFinder;

/**
 * Basic app for Fritzy applications
 * 
 * @param <T> Dropwizard Configuration Type
 */
public class FritzyDropWizardApp<T extends Configuration> extends Application<T> {

    public static final String PKG = "nl.technolution";

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        ServiceFinder.setupDropWizardServices(configuration);
        WebserviceFinder.setupWebservices(environment);
        environment.lifecycle().manage(new TimedTaskService());
    }
}
