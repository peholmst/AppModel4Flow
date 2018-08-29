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

package net.pkhapps.appmodel4flow.property.support;

import com.vaadin.flow.function.SerializableFunction;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO Document and test me!
 */
public final class Combinators {

    private Combinators() {
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allTrue() {
        return values -> values.allMatch(Predicates.isTrue());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyTrue() {
        return values -> values.anyMatch(Predicates.isTrue());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allFalse() {
        return values -> values.allMatch(Predicates.isFalse());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyFalse() {
        return values -> values.anyMatch(Predicates.isFalse());
    }

    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings() {
        return values -> values.collect(Collectors.joining());
    }

    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings(@Nonnull CharSequence delimiter) {
        return values -> values.collect(Collectors.joining(delimiter));
    }
}
