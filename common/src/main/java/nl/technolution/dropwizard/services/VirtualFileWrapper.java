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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for VirtualFile and VirtualFileURLConnection, allowing these classes to be used without
 * linking with the JBoss jars. Reflection is used to invoke methods.
 *
 * This class is used by ResourceFinder.
 */
public final class VirtualFileWrapper {
    /**
     * Private constructor as standard for utility classes
     */
    private VirtualFileWrapper() {
    }

    /**
     * Exception thrown when invoking a method on a VirtualFile fails.
     */
    public static final class VirtualFileInvocationException extends RuntimeException {
        /**
         * Default constructor.
         *
         * @param msg The message.
         * @param e The cause.
         */
        public VirtualFileInvocationException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Wrapper around JBoss file class.
     */
    public static final class VirtualFile {
        private final Object object;

        /**
         * Constructor
         * @param object A JBoss file instance
         */
        public VirtualFile(final Object object) {
            this.object = object;
        }

        /**
         * Wrapper for exists
         * @return {@code true} if the file exists, {@code false} if not.
         */
        public boolean exists() {
            return (Boolean)invoke("exists", object);
        }

        /**
         * Wrapper for isLeaf
         * @return {@code true} if the file is a leaf, {@code false} if it is not.
         */
        public boolean isLeaf() {
            return (Boolean)invoke("isLeaf", object);
        }

        public String getName() {
            return (String)invoke("getName", object);
        }

        /**
         * Get a VirtualFile which represents the parent of this instance.
         * @return the parent or null if there is no parent
         */
        public VirtualFile getParent() {
            Object parent = invoke("getParent", object);
            if (parent == null) {
                return null;
            }
            return new VirtualFile(parent);
        }

        /**
         * Get a child virtual file. The child may or may not exist in the virtual filesystem.
         *
         * @param path the path
         * @return the child
         * @throws IllegalArgumentException if the path is null
         */
        public VirtualFile getChild(String path) {
            Object child = invoke("getChild", object, path);
            if (child == null) {
                return null;
            }
            return new VirtualFile(child);
        }

        /**
         * Access the file contents.
         * @return an InputStream for the file contents.
         * @throws java.io.IOException for any error accessing the file system
         */
        public InputStream openStream() throws IOException {
            return (InputStream)invoke("openStream", object);
        }

        /**
         * Wrapper for getChildren
         * @return The list of children of this file.
         */
        public List<VirtualFile> getChildren() {
            final List<VirtualFile> result = new ArrayList<>();
            for (final Object o : UnsafeCast.castCollection(invoke("getChildren", object))) {
                result.add(new VirtualFile(o));
            }
            return result;
        }
    }

    private static Object invoke(final String methodName, final Object object, final Object... arguments) {
        Class<?>[] argClasses = null;
        if (arguments != null) {
            argClasses = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Object argument = arguments[i];
                if (argument != null) {
                    argClasses[i] = argument.getClass();
                }
            }
        }

        try {
            final Method method = object.getClass().getMethod(methodName, argClasses);
            return method.invoke(object, arguments);
        } catch (final InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null && RuntimeException.class.isAssignableFrom(cause.getClass())) {
                throw (RuntimeException)cause;
            }
            throw new VirtualFileInvocationException("Cannot invoke method on JBoss components", e);
        } catch (final IllegalArgumentException | SecurityException | ReflectiveOperationException e) {
            throw new VirtualFileInvocationException("Cannot invoke method on JBoss components", e);
        }
    }
}
