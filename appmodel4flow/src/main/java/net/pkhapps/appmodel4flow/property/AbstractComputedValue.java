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

package net.pkhapps.appmodel4flow.property;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.util.Objects;

/**
 * Base class for computed values ({@link CombinedValue} and {@link ComputedValue}). The computed value is kept in a
 * cache and is only recomputed on demand.
 *
 * @param <T> the type of the computed value.
 */
@NotThreadSafe
public abstract class AbstractComputedValue<T> extends AbstractObservableValue<T> {

    private static final long serialVersionUID = 1L;

    private transient T cachedValue;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (cachedValue instanceof Serializable) {
            out.writeObject(cachedValue);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readObject();
        try {
            cachedValue = (T) in.readObject();
        } catch (OptionalDataException ex) {
            // Ignore it
        }
    }

    /**
     * Recomputes and updates the cached value. If this results in a change of the value, an event is
     * {@link #fireValueChangeEvent(Object, Object) fired}.
     */
    @SuppressWarnings("WeakerAccess")
    protected void updateCachedValue() {
        var old = cachedValue;
        cachedValue = computeValue();
        if (!Objects.equals(old, cachedValue)) {
            fireValueChangeEvent(old, cachedValue);
        }
    }

    /**
     * Computes the value.
     *
     * @return the computed value.
     */
    protected abstract T computeValue();

    @Override
    public T getValue() {
        return cachedValue;
    }

    @Override
    public boolean isEmpty() {
        return cachedValue == null;
    }
}
