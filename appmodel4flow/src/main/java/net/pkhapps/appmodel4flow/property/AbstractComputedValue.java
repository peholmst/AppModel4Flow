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

import java.util.Objects;

/**
 * TODO Document me!
 *
 * @param <T>
 */
public abstract class AbstractComputedValue<T> extends AbstractObservableValue<T> {

    private T cachedValue;

    protected void updateCachedValue() {
        var old = cachedValue;
        cachedValue = computeValue();
        if (!Objects.equals(old, cachedValue)) {
            fireValueChangeEvent(old, cachedValue);
        }
    }

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
