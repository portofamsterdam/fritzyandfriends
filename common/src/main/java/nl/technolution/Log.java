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
package nl.technolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class with helper methods for logging with SLF4J.
 */
public final class Log {

    private Log() {
    }

    /**
     * Helper method to get a logger. To be used once per class in the top of a class.
     *
     * @return A {@link org.slf4j.Logger Logger} for the calling class.
     */
    public static Logger getLogger() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int index = Math.min(stackTrace.length - 1, 2);
        final String className = stackTrace[index].getClassName();
        return LoggerFactory.getLogger(className);
    }

    /**
     * Helper method to get the name of the method calling 'currentMethod'.
     *
     * @return The name of the method calling this method.
     */
    public static String currentMethod() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int index = Math.min(stackTrace.length - 1, 2);
        return stackTrace[index].getMethodName();
    }
}