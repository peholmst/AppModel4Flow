/*
 * Copyright (c) 2018 the original authors (see project POM file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pkhapps.appmodel4flow.util;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that wraps a {@link Class} to {@link Method} map. This is used internally by AppModel4Flow and should
 * not be used by clients.
 */
@NotThreadSafe
@Slf4j
public class MethodCache implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Map<Class, MethodEntry> cacheMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        cacheMap = new HashMap<>();
    }

    /**
     * Invokes the cached method on the target, if a method can be found. If no method exists, nothing happens.
     *
     * @param methodLookupFunction the function to use to look up the method, never {@code null}.
     * @param target               the target object to invoke the method on, never {@code null}.
     * @param args                 the arguments to pass to the method.
     * @throws Throwable any exception thrown by the method itself. Any exceptions from invoking the method (such
     *                   as the method not being accessible) are logged and ignored.
     */
    public void invoke(@Nonnull MethodLookupFunction methodLookupFunction, @Nonnull Object target,
                       @Nullable Object... args) throws Throwable {
        var type = target.getClass();
        var entry = cacheMap.computeIfAbsent(type, aClass -> createMethodEntry(methodLookupFunction, aClass));
        entry.invoke(target, args);
    }

    private MethodEntry createMethodEntry(MethodLookupFunction methodLookupFunction, Class<?> type) {
        try {
            return new MethodEntry(methodLookupFunction.findMethod(type));
        } catch (NoSuchMethodException | SecurityException ex) {
            log.debug("Failed to lookup method in " + type, ex);
            return new MethodEntry(null);
        }
    }

    /**
     * Functional interface for locating a specific method of a class.
     */
    @FunctionalInterface
    public interface MethodLookupFunction {

        /**
         * Locates the method to cache. The implementation should know what method to look for.
         *
         * @param type the class that is expected to contain the method, never {@code null}.
         * @return the method, never {@code null}.
         * @throws NoSuchMethodException if the method did not exist.
         * @throws SecurityException     if a security manager prevented the method from being located. This is only
         *                               included here because {@link Class#getMethod(String, Class[])} throws it.
         */
        @Nonnull
        Method findMethod(@Nonnull Class<?> type) throws NoSuchMethodException, SecurityException;
    }

    private static class MethodEntry {
        private final Method method;

        private MethodEntry(@Nullable Method method) {
            this.method = method;
        }

        void invoke(Object target, Object... args) throws Throwable {
            if (method != null) {
                try {
                    method.invoke(target, args);
                } catch (InvocationTargetException ex) {
                    throw ex.getCause();
                } catch (Exception ex) {
                    log.error("Error invoking " + method + " on " + target, ex);
                }
            }
        }
    }
}
