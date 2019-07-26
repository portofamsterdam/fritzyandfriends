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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import nl.technolution.Log;

/**
 * Utility class for finding types.
 * Code was inspired by Spring's PathMatchingResourcePatternResolver and from
 * http://snippets.dzone.com/posts/show/4831 .
 *
 * It uses a cache for storing all project classes.
 * <br>
 * This class is accessible from multiple threads. The static fields allClasses and includedPackages are
 * guarded by the static class lock and should only be read or written to from static synchronized methods.
 * <br><br>
 * See {@link com.google.common.reflect.ClassPath} for similar functionality from Guava.
 */
public final class TypeFinder {
    private static final String CLASS_EXT = ".class";

    private static final Logger LOG = Log.getLogger();

    /**
     * Predicate to match classes implementing the indicated interface or extending the indicated base class.
     */
    public static final class ImplementingClassPredicate implements Predicate<Class<?>> {
        private final Class<?> theInterface;

        public ImplementingClassPredicate(Class<?> theInterface) {
            this.theInterface = theInterface;
        }

        @Override
        public boolean test(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            if (clazz.isInterface()) {
                return false;
            }
            if (clazz.equals(theInterface)) {
                return false;
            }
            if (!theInterface.isAssignableFrom(clazz)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Predicate to match interfaces implementing the indicated interface.
     */
    public static final class ExtendingInterfacePredicate implements Predicate<Class<?>> {
        private final Class<?> theInterface;

        public ExtendingInterfacePredicate(Class<?> theInterface) {
            this.theInterface = theInterface;
        }

        @Override
        public boolean test(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            if (!clazz.isInterface()) {
                return false;
            }
            if (clazz.equals(theInterface)) {
                return false;
            }
            if (!theInterface.isAssignableFrom(clazz)) {
                return false;
            }
            return true;
        }
    }

    /**
     * The class cache
     */
    private static Set<Class<?>> allClasses = new HashSet<>();
    private static List<String> includedPackages = new ArrayList<>();

    // use setExcludedTypes to exclude classes in order to avoid error logs
    private static String[] excludedClassNames = new String[0];

    /**
     * Private Constructor, standard for utility classes
     */
    private TypeFinder() {
    }

    /**
     * Clear all internally cached classes; is useful in case of tests.
     */
    public static void clearCache() {
        setExcludedTypes(null);
    }

    /**
     * Remove these classes from the classes that are loaded, in order to prevent error log entries.
     * This must be done before the first call to getAllClasses, and is shared between calls.
     *
     * @param excludedTypes Comma-separated list of class/partial names, e.g. "JettyRunner,TimingOracleDriver";
     * make sure that 'partial' names are unique enough throughout all classes.
     */
    public static synchronized void setExcludedTypes(String excludedTypes) {
        if (excludedTypes == null) {
            TypeFinder.excludedClassNames = new String[0];
        } else {
            TypeFinder.excludedClassNames = excludedTypes.split(",");
        }

        // flush the cached classes as they are effected by excludedTypes
        allClasses.clear();
        includedPackages.clear();
    }

    /**
     * Get the list of all classes in the package from the cache.
     *
     * @param packageName The package to (recursively) retrieve all types from
     */
    private static synchronized List<Class<?>> getAllClasses(String packageName) {
        boolean packageIncluded = false;
        String packageSignature = packageName + ".";

        for (String includedPackage : includedPackages) {
            if (packageSignature.startsWith(includedPackage)) {
                // if a parent package is included, the package is included automatically
                packageIncluded = true;
            }
        }

        if (!packageIncluded) {
            allClasses.addAll(determineTypes(packageName));
            includedPackages.add(packageSignature);
        }

        List<Class<?>> result = new ArrayList<>();
        for (Class<?> c : allClasses) {
            String classPackageName = c.getPackage().getName();
            if (classPackageName.startsWith(packageSignature) || classPackageName.equals(packageName)) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Determines a list of all the types in the indicated package.
     *
     * @param packageName The package to (recursively) retrieve all types from
     * @return The list of classes
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private static List<Class<?>> determineTypes(String packageName) {
        List<String> classNames = null;
        try {
            classNames = findAllClassNamesUnderPackage(packageName);
        } catch (IOException e) {
            throw new IllegalStateException("Could not get resources for path: " + packageName, e);
        }

        // convert class name to class and check against the interface
        List<Class<?>> classes = new ArrayList<>();
        for (String className : classNames) {
            String pathToClass = JarUtil.nameToPath(className);
            try {
                if (isExcluded(pathToClass)) {
                    LOG.debug("Excluding class: " + className);
                    continue;
                }
                // NOTE: Can't use Class.forName to avoid problems with JBoss
                // Also known ResourceFinder is also using this particular class loader
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));

                // Classes missing from the classpath result in a NoClassDefFoundError
            } catch (Exception | LinkageError e) { // SUPPRESS CHECKSTYLE IllegalCatchCheck // NOPMD
                if (pathToClass.contains("test") || pathToClass.contains("Test") ||
                    pathToClass.contains("Mock") || pathToClass.contains("Simulator")) {
                    // if it is a test class, it's no big deal that it can't be loaded
                    LOG.debug("Could not load test class: {} due to: {}", className, e);
                } else {
                    LOG.error("Could not load class: {} due to: {}", className, e);
                }
            }
        }
        return classes;
    }

    private static boolean isExcluded(String pathToClass) {
        for (String excludedType : excludedClassNames) {
            if (!excludedType.isEmpty() && pathToClass.contains(excludedType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find all classes (not interfaces) implementing or extending a specific interface or class, under a
     * specific package.
     *
     * @param <T> The type of the interface.
     * @param thePackage The package to (recursively) retrieve all types from.
     * @param theInterface The interface the found classes must implement/extend (may also be a base class).
     * @return The list of matching classes.
     */
    public static <T> List<Class<? extends T>> findImplementingClasses(Package thePackage, Class<T> theInterface) {
        return findImplementingClasses(thePackage.getName(), theInterface);
    }

    /**
     * Find all classes (not interfaces) implementing or extending a specific interface or class, under a
     * specific package.
     *
     * @param <T> The type of the interface.
     * @param packageName The name of the package to (recursively) retrieve all types from.
     * @param theInterface The interface the found classes must implement/extend (may also be a base class).
     * @return The list of matching classes.
     */
    public static <T> List<Class<? extends T>> findImplementingClasses(String packageName, Class<T> theInterface) {
        ImplementingClassPredicate pred = new ImplementingClassPredicate(theInterface);
        return cast(findMatchingClasses(pred, packageName));
    }

    /**
     * Find all classes matching the given predicate, under a specific package.
     *
     * @param predicate Evaluation callback.
     * @param packageNames The name of the packages to (recursively) retrieve all types from.
     * @return The list of matching classes.
     */
    public static List<Class<?>> findMatchingClasses(Predicate<Class<?>> predicate, String... packageNames) {
        List<Class<?>> result = new ArrayList<>();
        for (String packageName : packageNames) {
            result.addAll(getAllClasses(packageName).stream().filter(predicate).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * Find all interfaces (not classes) implementing or extending a specific interface, under a specific package.
     *
     * @param <T>  The type of the interface.
     * @param packageName The package to (recursively) retrieve all types from
     * @param theInterface The interface the found interfaces must implement/extend (may also be a base class).
     * @return The list of matching classes
     */
    public static <T> List<Class<? extends T>> findInterfaces(String packageName, Class<T> theInterface) {
        Predicate<Class<?>> pred = new ExtendingInterfacePredicate(theInterface);
        return cast(findMatchingClasses(pred, packageName));
    }

    private static List<String> findAllClassNamesUnderPackage(String thePackage) throws IOException {
        return ResourceFinder.findAllFilesUnderPackage(thePackage).stream()
                .filter(pathName -> pathName.endsWith(".class"))
                .map(pathName -> pathToClassName(pathName))
                .collect(Collectors.toList());
    }

    /**
     * Translate a relative path to a .class file into a full class name.
     *
     * @param entryPath e.g. nl/technolution/core/resources/TypeFinder.class
     * @return e.g. nl.technolution.core.resources.TypeFinder
     */
    private static String pathToClassName(String entryPath) {
        int stripLength = CLASS_EXT.length();
        return JarUtil.pathToName(entryPath.substring(0, entryPath.length() - stripLength));
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object object) {
        return (T)object;
    }
}
