/*
 TECHNOLUTION BV, GOUDA NL
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

import java.util.Collection;
import java.util.Map;

/**
 * Suppresses type safety messages when casting from object to a type. By using
 * this cast the developer states that the cast is safe.
 */
public final class UnsafeCast {

    /**
     * Private constructor to prevent instantiation
     */
    private UnsafeCast() {
    }

    /**
     * Casts the object to the output type. This works only on collections.
     *
     * @param <T>
     *            The type
     * @param object
     *            The object
     * @return The casted object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Collection> T castCollection(final Object object) {
        return (T)object;
    }

    /**
     * Casts the object to the output type. This works only on maps.
     * @param <K> The key type
     * @param <V> The value type
     * @param object The input map to cast
     * @return The input map casted to the correct type.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castMap(final Object object) {
        return (Map<K, V>)object;
    }

    /**
     * Casts the object to the explicitly specified type
     *
     * @param <T>
     *            The type to cast to
     * @param clazz
     *            The class of the type to cast to.
     * @param object
     *            The object to cast
     * @return The casted object
     */
    public static <T> T cast(final Class<T> clazz, final Object object) {
        return clazz.cast(object);
    }

    /**
     * Do unsafe cast of object. Use it like this:
     *
     * <code>String variable = UnsafeCast.cast(strAsObject);</code>
     *
     * or like this when used as parameter for other method:
     *
     * <code>doSomething(UnsafeCast.&lt;String&gt;cast(strAsObject));</code>.
     * @param object The object to cast.
     * @param <T> The type to cast to
     * @return The casted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T)object;
    }
}
