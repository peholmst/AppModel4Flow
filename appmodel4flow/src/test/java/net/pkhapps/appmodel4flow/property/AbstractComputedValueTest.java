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

import com.vaadin.flow.function.SerializableSupplier;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link AbstractComputedValue} (those cases that aren't covered by {@link CombinedValueTest} and
 * {@link ComputedValueTest}).
 */
@SuppressWarnings("Convert2Diamond") // var and <> seems to produce a raw variable without generics
public class AbstractComputedValueTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeWithSerializableValue() throws Exception {
        var calls = new AtomicInteger(0);
        var value = new TestComputedValue<String>(() -> "hello world " + calls.incrementAndGet());
        var deserialized = (TestComputedValue<String>) serializeAndDeserialize(value);
        assertThat(value.getValue()).isEqualTo("hello world 1");
        assertThat(deserialized.getValue()).isEqualTo("hello world 1");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeWithNonserializableValue() throws Exception {
        var calls = new AtomicInteger(0);
        var value = new TestComputedValue<NonserializableString>(() -> new NonserializableString("hello world " + calls.incrementAndGet()));
        var deserialized = (TestComputedValue<NonserializableString>) serializeAndDeserialize(value);
        assertThat(value.getValue().value).isEqualTo("hello world 1");
        assertThat(deserialized.getValue().value).isEqualTo("hello world 2");
        // Call it once more to make sure the cached value is returned
        assertThat(deserialized.getValue().value).isEqualTo("hello world 2");
    }

    private Object serializeAndDeserialize(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new ObjectOutputStream(bos).writeObject(o);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        return new ObjectInputStream(bis).readObject();
    }

    public static class TestComputedValue<T> extends AbstractComputedValue<T> {

        private final SerializableSupplier<T> supplier;

        TestComputedValue(SerializableSupplier<T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
            updateCachedValue();
        }

        @Override
        protected T computeValue() {
            return supplier.get();
        }
    }

    @AllArgsConstructor
    public static class NonserializableString {
        final String value;
    }
}
