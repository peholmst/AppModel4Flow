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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Predicates}.
 */
public class PredicatesTest {

    @Test
    public void isTrue() {
        assertThat(Predicates.isTrue().test(true)).isTrue();
        assertThat(Predicates.isTrue().test(false)).isFalse();
    }

    @Test
    public void isFalse() {
        assertThat(Predicates.isFalse().test(true)).isFalse();
        assertThat(Predicates.isFalse().test(false)).isTrue();
    }
}
