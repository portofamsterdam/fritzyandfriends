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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.VirtualFileWrapper.VirtualFile;

/**
 * Helper class to find a list of resources in the classpath. Used for instance by TypeFinder.
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public final class ResourceFinder {
    static final List<String> VFS_PROTOCOLS = ImmutableList.of("vfs", "vfszip");
    private static final List<String> FILE_PROTOCOLS = ImmutableList.of("file", "vfsfile");
    private static final Logger LOG = Log.getLogger();

    /**
     * Private constructor for utility classes
     */
    private ResourceFinder() {
    }

    /**
     * Returns all files in the indicated packages.
     *
     * @param thePackage The package to look in, e.g. nl.minvenw.rws.fis
     * @return A collection with all files paths under the indicated package (e.g. nl/technolution/core/ResourceFinder)
     * @throws IOException If I/O errors occur.
     */
    public static Set<String> findAllFilesUnderPackage(String thePackage) throws IOException {
        Set<String> result = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // the set of resources the class loader uses for this package
        Enumeration<URL> resources = classLoader.getResources(JarUtil.nameToPath(thePackage));
        for (URL resource : Collections.list(resources)) {
            LOG.trace("Found resource: {}", resource);

            if (VFS_PROTOCOLS.contains(resource.getProtocol())) {
                // this resource resides in a vfszip
                URLConnection con = resource.openConnection();
                result.addAll(findVfsZipFiles(con, thePackage));
            } else if ("jar".equals(resource.getProtocol())) {
                // this resource resides in a jar
                result.addAll(findFilesInJarResource(resource, JarUtil.nameToPath(thePackage)));
            } else if (FILE_PROTOCOLS.contains(resource.getProtocol())) {
                // add from file system
                String fileName = resource.getFile();
                try {
                    String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
                    result.addAll(findFilesInDirectory(new File(fileNameDecoded), thePackage));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Could not decode url: " + fileName, e);
                }
            } else {
                LOG.warn("Skipping classpath resource while finding resources: {}", resource);
                // skip this resource
            }
        }
        return result;
    }

    /**
     * This method read from VfsZipfile, the in-archive format used by JBoss.
     * It contains a lot off reflection, in order to work without creating a dependency on JBoss
     */
    private static Collection<String> findVfsZipFiles(URLConnection con, String thePackage) throws IOException {
        return findFilesInVirtualDirectory(new VirtualFile(con.getContent()), thePackage);
    }

    /**
     * Recursive method used to find all files in a given directory and subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base directory
     * @throws IOException
     */
    private static List<String> findFilesInVirtualDirectory(VirtualFile directory, String packageName)
                    throws IOException {
        List<String> result = new ArrayList<>();
        if (!directory.exists()) {
            return result;
        }
        for (VirtualFile file : directory.getChildren()) {
            if (!file.isLeaf()) {
                result.addAll(findFilesInVirtualDirectory(file, packageName + "/" + file.getName()));
            } else {
                result.add(packageName + "/" + file.getName());
            }
        }
        return result;
    }

    private static List<String> findFilesInJarResource(URL resource, String thePackage) {
        try {
            List<String> result = new ArrayList<>();
            URLConnection con = resource.openConnection();

            if (!(con instanceof JarURLConnection)) {
                throw new IllegalStateException("Unexpected: connection to " + resource + " is not a JarURLConnection");
            }

            JarURLConnection jarCon = (JarURLConnection)con;
            try (JarFile jarFile = jarCon.getJarFile()) {

                for (JarEntry entry : Collections.list(jarFile.entries())) {
                    String entryPath = entry.getName();
                    if (entryPath.startsWith(thePackage)) { // filter on package
                        result.add(entryPath);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Exception loading from jar: " + resource, e);
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return A list of paths (a path is e.g. nl/technolution/core/ResourceFinder.class).
     */
    private static List<String> findFilesInDirectory(File directory, String packageName) {
        List<String> result = new ArrayList<>();
        if (!directory.exists()) {
            return result;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(findFilesInDirectory(file, packageName + "." + file.getName()));
                } else {
                    result.add(JarUtil.nameToPath(packageName) + "/" + file.getName());
                }
            }
        }
        return result;
    }
}