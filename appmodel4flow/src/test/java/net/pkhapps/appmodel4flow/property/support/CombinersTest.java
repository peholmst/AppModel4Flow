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

import org.junit.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Combiners}.
 */
public class CombinersTest {

    @Test
    public void allTrue() {
        assertThat(Combiners.allTrue().apply(Stream.of(true, true, true))).isTrue();
        assertThat(Combiners.allTrue().apply(Stream.of(true, true, false))).isFalse();
        assertThat(Combiners.allTrue().apply(Stream.empty())).isTrue();
    }

    @Test
    public void anyTrue() {
        assertThat(Combiners.anyTrue().apply(Stream.of(true, false, false))).isTrue();
        assertThat(Combiners.anyTrue().apply(Stream.of(false, false, false))).isFalse();
        assertThat(Combiners.anyTrue().apply(Stream.empty())).isFalse();
    }

    @Test
    public void allFalse() {
        assertThat(Combiners.allFalse().apply(Stream.of(false, false, false))).isTrue();
        assertThat(Combiners.allFalse().apply(Stream.of(false, false, true))).isFalse();
        assertThat(Combiners.allFalse().apply(Stream.empty())).isTrue();
    }

    @Test
    public void anyFalse() {
        assertThat(Combiners.anyFalse().apply(Stream.of(false, true, true))).isTrue();
        assertThat(Combiners.anyFalse().apply(Stream.of(true, true, true))).isFalse();
        assertThat(Combiners.anyFalse().apply(Stream.empty())).isFalse();
    }

    @Test
    public void joinStrings_noDelimiter() {
        assertThat(Combiners.joinStrings().apply(Stream.of("A", "B", "C"))).isEqualTo("ABC");
        assertThat(Combiners.joinStrings().apply(Stream.empty())).isEmpty();
    }

    @Test
    public void joinStrings_withDelimiter() {
        assertThat(Combiners.joinStrings(",").apply(Stream.of("A", "B", "C"))).isEqualTo("A,B,C");
        assertThat(Combiners.joinStrings(",").apply(Stream.empty())).isEmpty();
    }
}
