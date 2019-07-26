/*
 (C) COPYRIGHT 2013 TECHNOLUTION BV, GOUDA NL
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

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility class to find Java archive files.
 */
public final class JarUtil {

    public static final String META_INF = "META-INF";
    private static final String JAR_PROTOCOL = "jar";

    private JarUtil() {
    }

    /**
     * Returns the a JAR file containing the indicated class file.
     *
     * @param clazz The class to find.
     * @return The JarFile containing the indicated class file or {@code null} if no such JarFile could be found.
     * @throws IOException In case of an IO-error.
     */
    public static JarFile getJarForResource(Class<? extends Object> clazz) throws IOException {
        URL url = findJarURLForResource(clazz);
        if (url == null) {
            return null;
        }
        // Add a '!/' to the URL to obtain a JAR URL.
        URL url2 = new URL(url.getProtocol(), null, url.getFile() + "!/");
        return ((JarURLConnection)url2.openConnection()).getJarFile();
    }

    /**
     * Returns the URL of to JAR file containing the indicated class file.
     *
     * @param clazz The class to find.
     * @return The JarFile containing the indicated class file or {@code null} if no such JarFile could be found.
     * @throws IOException In case of an IO-error.
     */
    public static URL findJarURLForResource(Class<? extends Object> clazz) throws IOException {
        if (clazz == null) {
            return null;
        }
        final String className = nameToPath(clazz.getName());

        for (URL resource : Collections.list(clazz.getClassLoader().getResources(META_INF))) {
            if (JAR_PROTOCOL.equals(resource.getProtocol())) {
                URLConnection con = resource.openConnection();
                if (con instanceof JarURLConnection) {
                    URL result = findUrlInJarConnection(className, resource.getPath(), (JarURLConnection)con);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private static URL findUrlInJarConnection(final String className, String path, JarURLConnection jarCon)
                    throws MalformedURLException, IOException {
        try (JarFile jarFile = jarCon.getJarFile()) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                String entryPath = entry.getName();
                if (entryPath.startsWith(className)) {
                    // Return the URL of the Jar file, not the META-INF folder.
                    return new URL(JAR_PROTOCOL, null, path.substring(0, path.indexOf('!')));
                }
            }
        }
        return null;
    }

    /**
     * Translates all dots in the indicated name to forward slashes.
     *
     * @param name a package name, e.g. nl.technolution.core.util.JarUtil
     * @return a full path name, e.g. nl/technolution/core/util/JarUtil
     */
    public static String nameToPath(String name) {
        if (name == null) {
            return null;
        }
        return name.replace('.', '/');
    }

    /**
     * Translates all forward slashes in the indicated name to dots.
     *
     * @param path a full path name, e.g. nl/technolution/core/util/JarUtil
     * @return a package name, e.g. nl.technolution.core.util.JarUtil
     */
    public static String pathToName(String path) {
        if (path == null) {
            return null;
        }
        return path.replace('/', '.');
    }
}
