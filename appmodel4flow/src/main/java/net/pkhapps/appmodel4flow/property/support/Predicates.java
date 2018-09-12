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

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Utility class with predicates primarily intended to be used by {@link Combiners}, but may be used by developers as
 * well when needed.
 */
@SuppressWarnings("WeakerAccess")
public final class Predicates {

    private Predicates() {
    }

    /**
     * Predicate that checks if the argument is true.
     *
     * @return a predicate that evaluates to true if the argument is true.
     */
    @Nonnull
    public static Predicate<Boolean> isTrue() {
        return value -> value;
    }

    /**
     * Predicate that checks if the argument is false.
     *
     * @return a predicate that evaluates to true if the argument is false.
     */
    @Nonnull
    public static Predicate<Boolean> isFalse() {
        return value -> !value;
    }
}
