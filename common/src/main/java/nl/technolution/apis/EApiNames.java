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
package nl.technolution.apis;

import java.util.Arrays;

import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.dropwizard.webservice.IEndpoint;

/**
 * 
 */
public enum EApiNames {

    NETTY("netty", INettyApi.class),
    EXXY("exxy", IAPXPricesApi.class);

    private final String name;
    private final Class<? extends IEndpoint> clazz;


    /**
     * Constructor for {@link EApiNames} objects
     *
     * @param name
     * @param clazz
     */
    EApiNames(String name, Class<? extends IEndpoint> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * @param name class to find for
     * @return clazz
     */
    public static EApiNames getByName(String name) {
        return Arrays.asList(values())
                .stream()
                .filter(e -> e.name.equals(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(name));
    }

    public String getName() {
        return name;
    }

    public Class<? extends IEndpoint> getEndpointClass() {
        return clazz;
    }

}
